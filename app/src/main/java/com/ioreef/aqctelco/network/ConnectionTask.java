package com.ioreef.aqctelco.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ioreef.aqctelco.MainActivity;
import com.ioreef.aqctelco.dialogs.DialogGenerator;
import com.ioreef.aqctelco.tank.ProxyTank;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Tâche achevant une connexion à Supervision
 * Création et bound du socket
 *
 * @version 1.0
 * @date 02/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class ConnectionTask extends AsyncTask<String, Void, Boolean> {
    /**
     * Données génériques, classes liées
     */
    private Socket connectionSocket;
    private Connection connection;
    private MaterialDialog dialog;
    private DialogGenerator dialogGenerator;
    private Activity activity;

    public ConnectionTask(Connection connection, Activity activity) {
        this.connection = connection; this.activity = activity;
    }

    /**
     * Préparation de la tâche
     * Affichage d'un message de chargement
     */
    @Override
    protected void onPreExecute() {
        connectionSocket = connection.getSocket();
        dialogGenerator = new DialogGenerator(activity);
        MainActivity ma = (MainActivity) activity;

        if (connection.getContext() != null) {
            if (ma.getCurrentDialog()!=null);
                ma.getCurrentDialog().dismiss();
            dialog = dialogGenerator.generate(DialogGenerator.DialogType.PROGRESS);
            dialog.show();
        }
    }

    /**
     * Création et la socket et connexion
     * @param ip IP de Supervision à laquelle se connecter
     * @return
     */
    @Override
    protected Boolean doInBackground(String... ip) {
        Boolean result = false;
        try {
            connectionSocket = new Socket();
            connectionSocket.connect(new InetSocketAddress(ip[0], connection.SERVER_PORT), 5000);
            Log.i("CONNECTION","doInBackground : Socket created");
            result = true;
            connection.setConnectionState(true);
        } catch (UnknownHostException e) {
            connection.setConnectionState(false);
            e.printStackTrace();
        } catch (IOException ioe) {
            connection.setConnectionState(false);
            ioe.printStackTrace();
        }
        return result;
    }

    /**
     * Lors de la fin d'une tentative d'établissement d'une connexion
     * Si la connexion a réussi, création du thread de réception et demande des données à Supervision
     * Sinon affichage d'un message d'erreur
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        if (connectionSocket != null && connectionSocket.isConnected()) {
            connection.setSocket(connectionSocket);

            /* Début de l'acquisition */
            connection.setReceiver(new Receiver(connection.getContext()));
            Log.d("RECEIVER","Starting Receiver...");
            connection.getReceiver().start();

            /* Lance le rafraichissement des données de SummaryFragment*/
            connection.refreshingSummary(true);

            /* Demande des données des autres onglets */
            ProxyTank tank = new ProxyTank(connection.getContext());
            tank.askSwitchersInfo();
        }
        else {
            dialog = dialogGenerator.generate(DialogGenerator.DialogType.TIMEOUT);
            dialog.show();
        }
    }
}
