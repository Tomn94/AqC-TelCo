package com.ioreef.aqctelco.network;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Fil parallèle de réception des données
 *
 * @version 1.0
 * @date 09/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class Receiver extends Thread {

    private Activity context;

    public Receiver(Activity context) {
        this.context = context;
    }

    /**
     * Tourne en boucle tant qu'on est connecté
     */
    @Override
    public void run() {
        while (Connection.getConnection(context).getConnectionState()) {
            receiveMessage();
        }
    }

    /**
     * Récupération d'un message et transmission de celui-ci au dispatcher
     */
    protected void receiveMessage() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Connection.getConnection(context).getSocket().getInputStream()));
            String buffer;
            StringBuilder total = new StringBuilder();
            while ((buffer = br.readLine()) != null && !buffer.equals("EOF")) {
                total.append(buffer);
            }
            if (total != null && !total.toString().isEmpty()) {
                DispatcherUI dispatcherUI = new DispatcherUI();
                dispatcherUI.execute(total.toString());
            }

        } catch (IOException ioe) {
            Log.d("RECEIVER", "Error while receiving data");
            Connection.getConnection(context).setConnectionState(false);
            ioe.printStackTrace();
        }
    }

    /**
     * Méthode pour logger un message dépassant la limite de logcat.
     * (Typiquement une courbe d'éclairage)
     * @param tag
     * @param content
     */
    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

}