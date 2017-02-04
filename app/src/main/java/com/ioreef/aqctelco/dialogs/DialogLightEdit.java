package com.ioreef.aqctelco.dialogs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.ioreef.aqctelco.R;
import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.telco.Data;

import java.util.List;

/**
 * Dialog de modification d'une LED
 *
 * @version 1.0
 * @date 04/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class DialogLightEdit extends LinearLayout {

    private List<Curve> curveList = Data.getData().getCurveList(); /**< Liste des courbes pouvant être associées à une LED */
    private CurveAdapter curveAdapter = new CurveAdapter();        /**< Adapter de la liste des courbes */

    private CheckBox checkActivation;                              /**< Check d'activation d'une LED ou non */
    private CheckBox checkInvert;                                  /**< Check d'inversion du dimmer d'une LED ou non */
    private int selectedCurve;                                     /**< ID de la courbe associée actuellement sélectionnée */

    /**
     * Crée une fenêtre de dialogue pour modifier une LED
     * @param context Contexte de l'activity
     * @param ray LED à modifier, utilisée pour afficher ses paramètres actuels
     */
    public DialogLightEdit(Context context, Ray ray) {
        super(context);

        /* Création de l'UI */
        inflate(context, R.layout.dialog_light_edit, this);
        checkActivation = (CheckBox) findViewById(R.id.checkActivationLight);
        checkInvert = (CheckBox) findViewById(R.id.checkInvert);

        /* Configuration de la fenêtre selon la LED */
        checkActivation.setChecked(ray.isActivated());
        checkInvert.setChecked(ray.isInverted());
        selectedCurve = ray.getIdCurve();

        /* Vérification que la courbe configurée sur la LED existe dans la configuration actuelle */
        boolean found = false;
        for (Curve c : curveList) {
            if (ray.getIdCurve() == c.getId()) {
                found = true;
                break;
            }
        }
        if (!found) {   // Sinon on choisit la 1ère disponible
            try {
                if (!curveList.isEmpty()) {
                    selectedCurve = curveList.get(0).getId();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        /* Création de la liste des courbes */
        RecyclerView curveListView = (RecyclerView) findViewById(R.id.curveList);
        if (curveListView != null) {
            curveListView.setHasFixedSize(true);
            LinearLayoutManager linearManager = new LinearLayoutManager(context);
            linearManager.setOrientation(LinearLayoutManager.VERTICAL);
            curveListView.setLayoutManager(linearManager);
            curveListView.setAdapter(curveAdapter);
        }
    }


    /* Get/Set */

    /**
     * @return Retourne si la case d'Activation est cochée
     */
    public Boolean isActivatedChecked() {
        return checkActivation.isChecked();
    }

    /**
     * @return Retourne si la case d'Inversion est cochée
     */
    public Boolean isInvertedChecked() {
        return checkInvert.isChecked();
    }

    /**
     * @return Retourne l'ID de la courbe sélectionnée
     */
    public int getAssociatedCurveID() {
        return selectedCurve;
    }


    /**
     * Adapter pour la liste de courbes
     */
    private class CurveAdapter extends RecyclerView.Adapter<CurveAdapter.CurveViewHolder> {

        /**
         * Création du view holder pour la vue issue du layout d'un item
         * @param parent widget parent utilisé pour en récupérer le contexte
         * @param viewType inutilisé, 1 seul type d'item dans la liste
         * @return Le view holder créé pour une courbe
         */
        @Override
        public CurveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CurveViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_h_radio, parent, false));
        }

        /**
         * Lie un view holder aux données
         * @param holder view holder en question
         * @param position position dans la liste, utile pour récupérer les bonnes données
         */
        @Override
        public void onBindViewHolder(final CurveViewHolder holder, int position) {

            final Curve curve = curveList.get(position);

            /* Configuration de l'état de la checkbox et du texte associé (ID de la courbe) */
            holder.radioCurve.setText(getContext().getString(R.string.labelCurveName, curve.getDisplayId()));
            holder.radioCurve.setChecked(selectedCurve == curve.getId());

            /* Un tap coche la case et désactive toutes les autres de la liste */
            holder.radioCurve.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCurve = curve.getId();
                    curveAdapter.notifyItemRangeChanged(0, getItemCount());
                }
            });
        }

        /**
         * Retourne le nombre d'éléments dans la liste des courbes
         * @return nombre de courves
         */
        @Override
        public int getItemCount() {
            return curveList.size();
        }


        /**
         * Définition d'un ViewHolder pour un item de la liste
         */
        public class CurveViewHolder extends RecyclerView.ViewHolder {

            protected RadioButton radioCurve;

            public CurveViewHolder(View v) {
                super(v);
                radioCurve = (RadioButton) v.findViewById(R.id.radioCurve);
            }

        }

    }

}
