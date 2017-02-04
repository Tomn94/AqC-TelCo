package com.ioreef.aqctelco.tabs;

import android.util.Log;

import com.ioreef.aqctelco.network.Connection;
import com.ioreef.aqctelco.tank.ProxyTank;

import java.util.TimerTask;

/**
 * Tâche mettant à jour les données de manière périodique
 *
 * @version 1.0
 * @date 13/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class SummaryUpdater extends TimerTask {

    private Connection co = null;
    public static final int REFRESH_RATE = 5*1000; /**< Période de rafraichissement en ms */

    public SummaryUpdater(Connection co) {
        this.co = co;
    }

    /**
     * Crée une nouvelle demande de nouvelles données à Supervision
     */
    @Override
    public void run() {
        if (co == null || !co.getConnectionState()) {
            cancel();
            Log.d("SUMMARYUPDATER","Stop refreshingSummary, we're not connected");
        } else {
            Log.d("SUMMARYUPDATER","Refreshing data");
            ProxyTank proxyTank = new ProxyTank(co.getContext());
            proxyTank.askCurves();  //35
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            proxyTank.askSensorsInfo(); //10
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            proxyTank.askRaysInfo();    //30

        }
    }

    /**
     * Annule une actualisation des données
     * @return succès de l'annulation
     */
    @Override
    public boolean cancel() {
        return super.cancel();
    }
}
