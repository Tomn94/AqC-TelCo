package com.ioreef.aqctelco.tabs;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ioreef.aqctelco.R;
import com.ioreef.aqctelco.tank.ProxyTank;
import com.ioreef.aqctelco.tank.Switcher;
import com.ioreef.aqctelco.telco.Data;
import com.ioreef.aqctelco.telco.DataChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Onglet Relais
 * Affiche une liste des relais, incluant les relais
 *
 * @version 1.0
 * @date 03/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class SwitchersFragment extends Fragment implements DataChangeListener {

    private SwitcherAdapter switcherAdapter = new SwitcherAdapter(Data.getData().getSwitcherList()); /**< Adapter de la liste des Switchers */

    /** Utile à l'interdiction de 2 appuis sur un item dans un intervalle réduit */
    private static final int MIN_TIME_NEW_TOUCH = 2000;     /**< Temps d'attente entre 2 appuis */
    private Map<Integer, Long> timeLastTouchForItem = new HashMap<>();
    private long timeLastLongTouch = 0;

    private MaterialDialog.Builder dialogRelayName; /**< Fenêtre d'édition d'un nom de relai */

    /**
     * Lors de la création de la vue-onglet
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_relays, container, false);

        /* Écran commun de modification du nom */
        dialogRelayName = new MaterialDialog.Builder(getContext())
                                .positiveText(R.string.btnRelayNameOK)
                                .negativeText(R.string.btnRelayNameCancel)
                                .inputRangeRes(2, 25, R.color.relayWrongInput);

        /* Création de la liste en grille */
        RecyclerView switcherListView = (RecyclerView) v.findViewById(R.id.relayList);
        switcherListView.setHasFixedSize(true);
        GridLayoutManager gridManager = new GridLayoutManager(getContext(), 4);
        switcherListView.setLayoutManager(gridManager);
        switcherListView.setItemAnimator(new DefaultItemAnimator());

        switcherListView.setAdapter(switcherAdapter);   // Affectation de l'adapter

        Data.getData().addListListener(this, Data.ListenerType.SWITCHER);

        return v;
    }

    /**
     * Lorsque l'app est relancée
     */
    @Override
    public void onResume() {
        super.onResume();
        Data.getData().addListListener(this, Data.ListenerType.SWITCHER);
    }

    /**
     * Lorsque l'app passe en arrière-plan
     */
    @Override
    public void onPause() {
        Data.getData().removeListListener(this, Data.ListenerType.SWITCHER);
        super.onPause();
    }

    /**
     * Appelée au changement des données affichées
     */
    @Override
    public void onDataChanged() {
        switcherAdapter.setSwitcherList(Data.getData().getSwitcherList());
    }

    /**
     * Adapter pour la liste de relais
     */
    private class SwitcherAdapter extends RecyclerView.Adapter<SwitcherAdapter.SwitcherViewHolder> {

        private List<Switcher> switcherList;

        protected SwitcherAdapter(List<Switcher> list) {
            this.switcherList = list;
        }

        public void setSwitcherList(List<Switcher> switcherList) {
            this.switcherList = switcherList;
            notifyDataSetChanged();
        }

        /**
         * Création du view holder pour la vue issue du layout d'un item
         * @param parent widget parent utilisé pour en récupérer le contexte
         * @param viewType inutilisé, 1 seul type d'item dans la liste
         * @return Le view holder créé pour un Switcher
         */
        @Override
        public SwitcherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SwitcherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_relay, parent, false));
        }

        /**
         * Lie un view holder aux données
         * @param holder view holder en question
         * @param position position dans la liste, utile pour récupérer les bonnes données
         */
        @Override
        public void onBindViewHolder(final SwitcherViewHolder holder, int position) {

            final Switcher switcher = switcherList.get(position);

            int stateColor = ContextCompat.getColor(getContext(), R.color.colorRelayOff);
            if (switcher.isActivated()) {
                stateColor = ContextCompat.getColor(getContext(), R.color.colorRelayOn);
            }
            ((GradientDrawable) holder.topCardColored.getBackground()).setColor(stateColor);

            holder.vhTitle.setText(switcher.getName());
            holder.vhTitle.setTextColor(stateColor);

            /**
             * Un tap inverse l'état d'un relai
             * L'état d'un relai en particulier ne peut être changé trop rapidement
             */
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int itemPos = holder.getAdapterPosition();

                        /* Interdiction d'attaque par force brute de doigté */
                        long currentTime = SystemClock.elapsedRealtime();
                        if (timeLastTouchForItem.get(itemPos) != null) {
                            if (currentTime - timeLastTouchForItem.get(itemPos) < MIN_TIME_NEW_TOUCH) {
                                return;
                            }
                        }
                        timeLastTouchForItem.put(itemPos, currentTime);

                        /* Envoi (dés)activation */
                        Switcher clickedSwitcher = switcherList.get(itemPos);
                        JSONObject switcherToSend = clickedSwitcher.switchAndSerialize();
                        ProxyTank proxyTank = new ProxyTank(getActivity());
                        proxyTank.askChangeSwitcherParam(switcherToSend);

                        //TODO: A supprimer
                        try {
                            switcherList.set(itemPos, new Switcher(switcherToSend));
                            notifyItemChanged(itemPos);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });

            /**
             * Un tap long permet de modifier le nom d'un relais
             */
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    /* Interdiction de modifier 2 noms en même temps, menant à des incohérences */
                    long currentTime = SystemClock.elapsedRealtime();
                    if (currentTime - timeLastLongTouch < 500) {
                        return true;
                    }
                    timeLastLongTouch = currentTime;

                    /* Présentation du dialog de modification */
                    try {
                        final int itemPos = holder.getAdapterPosition();

                        /* Modifier le nom d'un relais a moins de chance d'annuler la modification son état (bug #24141) */
                        if (timeLastTouchForItem.get(itemPos) != null) {
                            if (currentTime - timeLastTouchForItem.get(itemPos) < MIN_TIME_NEW_TOUCH) {
                                return true;
                            }
                        }
                        timeLastTouchForItem.put(itemPos, currentTime);

                        final Switcher clickedSwitcher = switcherList.get(itemPos);
                        String switcherName = clickedSwitcher.getName();

                        dialogRelayName.title(getString(R.string.editNameTitle, switcherName));
                        dialogRelayName.input(getString(R.string.editNameInputEmpty, switcherName), switcherName, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                /* Envoi renommage */
                                JSONObject switcherToSend = clickedSwitcher.renameAndSerialize(input.toString());
                                ProxyTank proxyTank = new ProxyTank(getActivity());
                                proxyTank.askChangeSwitcherParam(switcherToSend);

                                //TODO: A supprimer
                                try {
                                    switcherList.set(itemPos, new Switcher(switcherToSend));
                                    notifyItemChanged(itemPos);
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialogRelayName.show();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });
        }

        /**
         * Retourne le nombre d'éléments dans la liste des Switchers
         * @return nombre de Switchers
         */
        @Override
        public int getItemCount() {
            return switcherList.size();
        }


        /**
         * Définition d'un ViewHolder pour un item de la liste
         */
        public class SwitcherViewHolder extends RecyclerView.ViewHolder {

            protected TextView vhTitle;
            protected CardView cardView;
            protected View topCardColored;

            public SwitcherViewHolder(View v) {
                super(v);

                cardView = (CardView) v.findViewById(R.id.cardRelay);
                vhTitle = (TextView) v.findViewById(R.id.cardTitle);
                topCardColored = v.findViewById(R.id.topCardColored);
            }

        }

    }

}

