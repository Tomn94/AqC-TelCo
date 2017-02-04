package com.ioreef.aqctelco.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.ioreef.aqctelco.dialogs.DialogGenerator;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Tâche asynchrone d'envoi d'un message à Supervision
 *
 * @version 1.0
 * @date 09/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class Sender extends AsyncTask<JSONObject, Void, Boolean> {

    private Connection co = null;
    private Activity context;
    private Boolean success = false;            /**< Succès de l'envoi */

    public Sender(Activity context) {
        this.context = context;
    }

    /**
     * Préparation de l'envoi
     */
    @Override
    protected void onPreExecute() {
        co = Connection.getConnection(context);
    }

    /**
     * Envoie un ou plusieurs messages à partir d'un format textuel JSON via la socket
     * @param messages
     * @return
     */
    protected Boolean sendMessage(JSONObject... messages) {
        if (co.getConnectionState()) {
            try {
                OutputStream os = co.getSocket().getOutputStream();
                for(int i=0; i<messages.length; i++) {
                    os.flush();
                    os.write((messages[i].toString() + "\0").getBytes());
                    Log.i("SENDING", "Sending Data to Network: " + messages[i].toString());
                }
                os.flush();
                success = true;
            } catch (IOException ioe) {
                co.setConnectionState(false);
            }
        }
        return success;
    }

    /**
     * Envoie les données fournies via la connexion indiquée
     * @param params Chaînes à envoyer via la socket
     * @return Succès de l'opération
     */
    @Override
    protected Boolean doInBackground(JSONObject... params) {
        return sendMessage(params);
    }

    /**
     * Lorsque l'envoi est réalisé
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(success) {
            Log.d("SENDING","Message sent successfully");
        } else {
            try {
                new DialogGenerator(co.getContext()).generate(DialogGenerator.DialogType.ERROR).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
