package com.ioreef.aqctelco;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ioreef.aqctelco.dialogs.DialogGenerator;
import com.ioreef.aqctelco.dialogs.DialogLightEdit;
import com.ioreef.aqctelco.network.Connection;
import com.ioreef.aqctelco.tank.ProxyTank;
import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.telco.Data;
import com.ioreef.aqctelco.telco.DataChangeListener;
import com.ioreef.aqctelco.telco.DataSaveListener;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Activité de modification de l'éclairage
 *
 * @version 1.0
 * @date 28/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class LightActivity extends AppCompatActivity implements DataChangeListener, DataSaveListener {

    public  final long TIME_SIMULATION = 300000; // 5 min
    private final long TIMEOUT_SAVE    =  20000; // 20 s

    private List<Ray> temporaryRayList;                      /**< Liste des LED reçue par Supervision et éditée */
    private RayAdapter rayAdapter = null;                    /**< Adapter de la liste des Switchers */
    private MaterialDialog dialogLightSimulation;            /**< Message indiquant une simulation en cours */

    /** Bouton de sauvegarde et sa barre de progression */
    private FloatingActionButton floatingLightSave;         /**< Bouton permanent */
    private View floatingLightSaveCircle;                   /**< Cercle en surimpression lors de la sauvegarde */
    private ProgressBar floatingLightSaveProgress;          /**< Animation de progression lors de la sauvegarde */

    private CountDownTimer watchdogSave;                    /**< Timer avant une sauvegarde trop longue TIMEOUT_SAVE */
    private CountDownTimer timerSimulation;                 /**< Timer avant la fin de la simulation TIME_SIMULATION */


    /**
     * À la création de l'activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        /* Affichage d'un bouton retour dans la toolbar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.lightActivityTitle);
        }

        /* Création du message de simulation de l'éclairage */
        DialogGenerator dialogGenerator = new DialogGenerator(this);
        dialogLightSimulation = dialogGenerator.generate(DialogGenerator.DialogType.SIMULATION);

        /* Création du modèle de liste éditable */
        List<Ray> originalDataRayList = Data.getData().getRayList();
        temporaryRayList = new ArrayList<>();
        for (Ray ray : originalDataRayList) {
            temporaryRayList.add(new Ray(ray));
        }
//        rayAdapter = new RayAdapter(Data.getData().getRayList());
        rayAdapter = new RayAdapter(temporaryRayList);

        /* Création de la liste verticale */
        RecyclerView rayListView = (RecyclerView) findViewById(R.id.rayList);
        if (rayListView != null) {
            rayListView.setHasFixedSize(true);
            LinearLayoutManager linearManager = new LinearLayoutManager(this);
            linearManager.setOrientation(LinearLayoutManager.VERTICAL);
            rayListView.setLayoutManager(linearManager);
            rayListView.setAdapter(rayAdapter);
        }

        /* Affichage d'un bouton de sauvegarde en bas à droite */
        floatingLightSave = (FloatingActionButton) findViewById(R.id.floatingLightSave);
        floatingLightSaveCircle = findViewById(R.id.floatingLightSaveLoading);
        floatingLightSaveProgress = (ProgressBar) findViewById(R.id.floatingLightSaveLoadingProgress);
        if (floatingLightSaveProgress != null) {
            floatingLightSaveProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_IN);
        }
        if (floatingLightSave != null) {
            floatingLightSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Sauvegarde des LED lors du tap sur le bouton sauvegarder */
                    save();
                }
            });
            if (temporaryRayList.isEmpty()) {
                floatingLightSave.setVisibility(View.INVISIBLE);
            } else {
                floatingLightSave.setVisibility(View.VISIBLE);
            }
        }

        Data.getData().addListListener(this, Data.ListenerType.RAY);
        Data.getData().setSaveListener(this);

    }

    /**
     * Lorsque l'app est relancée
     */
    @Override
    protected void onResume() {
        super.onResume();
        rayAdapter = new RayAdapter(Data.getData().getRayList());
        Data.getData().addListListener(this, Data.ListenerType.RAY);
        Data.getData().setSaveListener(this);
        Data.getData().setCurrentActivity(this);
    }

    /**
     * Lorsque l'app passe en arrière-plan
     */
    @Override
    protected void onPause() {
        Data.getData().removeListListener(this, Data.ListenerType.RAY);
        Data.getData().setSaveListener(null);
        if (watchdogSave != null) {
            watchdogSave.cancel();
        }
        if (timerSimulation != null) {
            timerSimulation.cancel();
        }
        super.onPause();
    }

    /**
     * Création des items de la toolbar à partir du layout
     * @param menu Le menu à créer
     * @return Affiche le menu ou non
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_light, menu);
        return true;
    }

    /**
     * Action à effectuer lors d'un tap sur une icône de la toolbar
     * @param item icône qui réagit
     * @return consommation de l'action ou non
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* On quitte si appui sur le bouton <- (btnLightBack) dans la toolbar */
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.btnLightSimulate) {
            startSimulation();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fonction appelée lors qu'on veut quitter la vue
     * Vérifie que les changements ont été sauvegardés
     */
    @Override
    public void onBackPressed() {
        final List<Ray> originalRayList = Data.getData().getRayList();
        boolean changesFound = originalRayList.size() != temporaryRayList.size();
        for (int i = 0; i < temporaryRayList.size() && !changesFound ; ++i) {
            if (!temporaryRayList.get(i).sameAs(originalRayList.get(i))) {
                changesFound = true;
            }
        }

        if (!changesFound) {
            super.onBackPressed();              // On ferme l'activité s'il n'y a aucun changement
        } else {
            new MaterialDialog.Builder(this)    // On demande à l'utilisateur s'il ne veut pas d'abord sauvegarder
                    .title(R.string.lightAlertSaveTitle)
                    .content(R.string.lightAlertSaveDetail)
                    .positiveText(R.string.lightAlertSaveConfirm)
                    .negativeText(R.string.lightAlertSaveCancel)
                    .neutralText(R.string.lightAlertSaveErase)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            /* Sauvegarder et fermer */
                            save();
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            /* Ne pas sauvegarder, fermer
                             * Rappelle cette fonction mais avec une liste identique */
                            temporaryRayList = originalRayList;
                            onBackPressed();
                        }
                    })
                    .show();
        }
    }

    /* ACTIONS */

    /**
     * Sauvegarde des modifications sur les LED (envoi à Supervision)
     */
    public void save() {
        JSONArray newRayList = new JSONArray();
        for (Ray ray : temporaryRayList) {
            newRayList.put(ray.serialize());
        }
        ProxyTank proxyTank = new ProxyTank(this);
        proxyTank.askChangeRayParam(newRayList);

        if (Connection.getConnection(this).getConnectionState()) {
            floatingLightSave.setVisibility(View.INVISIBLE);
            floatingLightSaveCircle.setVisibility(View.VISIBLE);
            floatingLightSaveProgress.setVisibility(View.VISIBLE);

            watchdogSave = new CountDownTimer(TIMEOUT_SAVE, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    // Rétablissement du bouton pour une nouvelle tentative
                    floatingLightSave.setVisibility(View.VISIBLE);
                    floatingLightSaveCircle.setVisibility(View.INVISIBLE);
                    floatingLightSaveProgress.setVisibility(View.INVISIBLE);

                    // Temps d'attente de sauvegarde dépassé
                    DialogGenerator dialogGenerator = new DialogGenerator(LightActivity.this);
                    dialogGenerator.generate(DialogGenerator.DialogType.TIMEOUT).show();
                }
            }.start();
        }
    }

    /**
     * Lancement d'une simulation de l'éclairage sur Supervision
     */
    public void startSimulation() {
        /* Ne pas lancer si en cours de sauvegarde */
        if (floatingLightSaveProgress.getVisibility() == View.VISIBLE) {
            return;
        }

        ProxyTank proxyTank = new ProxyTank(this);
        proxyTank.askForSimulation();

        if (Connection.getConnection(this).getConnectionState()) {
            dialogLightSimulation.show();

            timerSimulation = new CountDownTimer(TIME_SIMULATION, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    dialogLightSimulation.hide();   // Simulation terminée
                }
            }.start();
        }
    }

    /**
     * Arrêt d'une simulation en cours de l'éclairage sur Supervision
     */
    public void stopSimulation() {
        ProxyTank proxyTank = new ProxyTank(this);
        proxyTank.askStopSimulation();
    }


    /**
     * Appelée au changement des données affichées
     */
    @Override
    public void onDataChanged() {
        List<Ray> originalDataRayList = Data.getData().getRayList();
        rayAdapter.setRayList(Data.getData().getRayList());
        temporaryRayList = new ArrayList<>();
        for (Ray ray : originalDataRayList) {
            temporaryRayList.add(new Ray(ray));
        }
        rayAdapter.notifyItemRangeChanged(0, temporaryRayList.size());
        rayAdapter.notifyDataSetChanged();
        if (temporaryRayList.isEmpty()) {
            floatingLightSave.setVisibility(View.INVISIBLE);
        } else {
            floatingLightSave.setVisibility(View.VISIBLE);
        }

    }


    /**
     * Appelée lorsque Supervision a sauvegardé les données
     */
    @Override
    public void onDataSaved() {
        watchdogSave.cancel();
        floatingLightSave.setVisibility(View.VISIBLE);
        floatingLightSaveCircle.setVisibility(View.INVISIBLE);
        floatingLightSaveProgress.setVisibility(View.INVISIBLE);
        finish();
    }


    /**
     * Adapter pour la liste de LED
     */
    private class RayAdapter extends RecyclerView.Adapter<RayAdapter.RayViewHolder> {

        private List<Ray> rayList;

        protected RayAdapter(List<Ray> rayList) {
            this.rayList = rayList;
        }

        public void setRayList(List<Ray> rayList) {
            this.rayList = rayList;
            notifyDataSetChanged();
        }

        /**
         * Création du view holder pour la vue issue du layout d'un item
         * @param parent widget parent utilisé pour en récupérer le contexte
         * @param viewType inutilisé, 1 seul type d'item dans la liste
         * @return Le view holder créé pour une LED (Ray)
         */
        @Override
        public RayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ray, parent, false));
        }

        /**
         * Lie un view holder aux données
         * @param holder view holder en question
         * @param position position dans la liste, utile pour récupérer les bonnes données
         */
        @Override
        public void onBindViewHolder(final RayViewHolder holder, int position) {

            final Ray ray = temporaryRayList.get(position);
            String rayInverted = getString(R.string.lightNotInverted);
            if (ray.isInverted()) {
                rayInverted = getString(R.string.lightInverted);
            }
            int stateColor = ContextCompat.getColor(LightActivity.this, R.color.colorLedOff);
            if (ray.isActivated()) {
                stateColor = ContextCompat.getColor(LightActivity.this, R.color.colorLedOn);
            }

            holder.ledName.setText(getString(R.string.lightNamePrefix, ray.getDisplayId()));
            holder.ledDetail.setText(getString(R.string.lightDetail, ray.getDisplayIdCurve(), rayInverted));
            ((GradientDrawable) holder.leftCardColored.getBackground()).setColor(stateColor);

            /* Édition d'une LED au tap */
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Data.getData().getCurveList().isEmpty()) {
                        final DialogLightEdit dialogLightEdit = new DialogLightEdit(LightActivity.this, ray);
                        new MaterialDialog.Builder(LightActivity.this)
                                .title(getString(R.string.lightEditTitle, ray.getDisplayId()))
                                .customView(dialogLightEdit, true)
                                .positiveText(R.string.btnLightEditOK)
                                .negativeText(R.string.btnLightEditCancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        ray.setActivated(dialogLightEdit.isActivatedChecked());
                                        ray.setInverted(dialogLightEdit.isInvertedChecked());
                                        ray.setIdCurve(dialogLightEdit.getAssociatedCurveID());
                                        notifyItemChanged(holder.getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                })
                                .show();
                    }
                }
            });

        }

        /**
         * Retourne le nombre d'éléments dans la liste des LED
         * @return nombre de Ray
         */
        @Override
        public int getItemCount() {
            return temporaryRayList.size();
        }


        /**
         * Définition d'un ViewHolder pour un item de la liste
         */
        public class RayViewHolder extends RecyclerView.ViewHolder {

            protected CardView cardView;
            protected TextView ledName,
                               ledDetail;
            protected View leftCardColored;

            public RayViewHolder(View v) {
                super(v);

                cardView  = (CardView) v.findViewById(R.id.cardRay);
                ledName   = (TextView) v.findViewById(R.id.ledName);
                ledDetail = (TextView) v.findViewById(R.id.ledDetail);
                leftCardColored = v.findViewById(R.id.leftCardColored);
            }

        }

    }

}
