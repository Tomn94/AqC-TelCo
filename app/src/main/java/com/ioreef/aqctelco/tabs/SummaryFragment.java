package com.ioreef.aqctelco.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioreef.aqctelco.R;
import com.ioreef.aqctelco.network.Connection;
import com.ioreef.aqctelco.telco.Data;
import com.ioreef.aqctelco.telco.DataChangeListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Onglet Bilan
 * Affiche une vue synthétique des capteurs et de l'éclairage
 *
 * @version 1.0
 * @date 03/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */

// TODO: Refactoriser

public class SummaryFragment extends Fragment implements DataChangeListener {

    private SummaryAdapter summaryAdapter = null;    /**< Adapter de la liste des cases */
    /**
     * Lors de la création de la vue-onglet
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_summary, container, false);

//        summaryAdapter = new SummaryAdapter(getActivity()); /**< Adapter de la liste des cases */
        summaryAdapter =  new SummaryAdapter(getActivity(), Data.getData().getSensorList(), Data.getData().getCurveList());

        /* Création de la liste en grille */
        RecyclerView summaryList = (RecyclerView) v.findViewById(R.id.summaryList);
        summaryList.setHasFixedSize(false);
        StaggeredGridLayoutManager gridManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        summaryList.setLayoutManager(gridManager);
        summaryList.setItemAnimator(new DefaultItemAnimator());
        summaryList.setAdapter(summaryAdapter);

        Data.getData().addListListener(this, Data.ListenerType.SENSOR);
        Data.getData().addListListener(this, Data.ListenerType.CURVE);

        return v;
    }

    /**
     * Lorsque l'app est relancée
     */
    @Override
    public void onResume() {
        super.onResume();
        Data.getData().addListListener(this, Data.ListenerType.SENSOR);
        Data.getData().addListListener(this, Data.ListenerType.CURVE);
    }

    /**
     * Lorsque l'app passe en arrière-plan
     */
    @Override
    public void onPause() {
        Data.getData().removeListListener(this, Data.ListenerType.SENSOR);
        Data.getData().removeListListener(this, Data.ListenerType.CURVE);
        super.onPause();
    }


    /**
     * Appelée au changement des données affichées
     */
    @Override
    public void onDataChanged() {
        summaryAdapter.setCurveList(Data.getData().getCurveList());
        summaryAdapter.setSensorList(Data.getData().getSensorList());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Connection co = Connection.getConnection(getActivity());
        if (getActivity() != null && isVisibleToUser) {
            Log.d("UPDATER", "Fragment is visible to user: Start data refresh");
            co.refreshingSummary(true);
        } else if (!isVisibleToUser) {
            Log.d("UPDATER", "Fragment isn't visible. Stop data refresh");
            co.refreshingSummary(false);
        }
    }


    /**
     * Génération de tous les labels sur l'axe des abscisses pour chaque point
     * @param nbrValues Nombre de points à traiter
     * @param useCurrentTime Le graphe nécessite un affichage depuis 24h à partir de l'heure courante
     *                       ou alors simplement un affichage 0h à 23h59
     * @return Liste des labels
     */
    public static List<String> generateTimeLabels(int nbrValues, boolean useCurrentTime) {

        List<String> xVals = new ArrayList<>();
        double currentHour = 0,
                currentMin = 0;

        if (useCurrentTime) {
            Calendar cal = Calendar.getInstance();
            currentHour = cal.get(Calendar.HOUR_OF_DAY);
            currentMin = cal.get(Calendar.MINUTE);
        }

        /* Calcul d'un pas temporel entre les valeurs */
        double nbrDataRefreshPerHour = (double) nbrValues * 60.0 / 1440.0;
        if (nbrDataRefreshPerHour == 0) {
            nbrDataRefreshPerHour = 1;  // Dividing by zero is bad
        }
        /* Traitement pour chaque valeur, leur ordre étant inversé par rapport à l'axe X */
        for (int timeStep = nbrValues - 1; timeStep >= 0; --timeStep) {
            /* Heure */
            double ratio = (double) timeStep / nbrDataRefreshPerHour;
            double hour = (currentHour - ratio) % 24;
            if (hour < 0) {
                hour += 24;
            }

            /* Minute */
            double minute = currentMin;
            if (useCurrentTime/* && timeStep != nbrValues - 1*/) {
                minute -= (int) ((hour - Math.floor(hour)) * 60);
            }
            if (minute < 0) {
                minute += 60;
            }

            /* Mise en forme */
            String labelX;
            if (hour < 10) {
                labelX = "0" + (int) Math.floor(hour) + ":";
            } else {
                labelX = (int) Math.floor(hour) + ":";
            }
            if (minute < 10) {
                labelX += "0" + (int) minute;
            } else {
                labelX += String.valueOf((int) minute);
            }
            xVals.add(labelX);
        }

        return xVals;
    }

}
