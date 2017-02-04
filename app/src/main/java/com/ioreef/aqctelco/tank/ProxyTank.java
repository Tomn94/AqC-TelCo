package com.ioreef.aqctelco.tank;

import android.app.Activity;

import com.ioreef.aqctelco.network.Sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe permettant de formater des messages en JSON
 * (représentant un objet du modèle version textuelle) à envoyer à Supervision.
 *
 * On transmet le code de l'action, et les données associées le cas échéant.
 * La méthode send() est surchargée pour couvrir plusieurs cas d'utilisations.
 *
 * @version 1.0
 * @date 04/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class ProxyTank {

    private Activity context;

    public ProxyTank(Activity context) {
        this.context = context;
    }

    /**
     * Ecrit dans le flux sortant un objet JSON converti en bytes
     * @param fullData : Objet JSON à transmettre
     */
    public void performSend(JSONObject... fullData) {
        new Sender(context).execute(fullData);
    }

    public void askSensorsInfo() {
        send(10);
    }

    public void askSwitchersInfo() {
        send(20);
    }

    public void askChangeSwitcherParam(JSONObject switcherObject) {
        send(21,switcherObject);
    }

    public void askRaysInfo() {
        send(30);
    }

    public void askChangeRayParam(JSONArray rayArray) {
        send(31,rayArray);
    }

    public void askCurves() {
        send(35);
    }

    public void askForSimulation() {
        send(38, 2);
    }

    public void askStopSimulation() {
        send(39);
    }

    public void askNotifsInfo() {
        send(40);
    }

    public void askChangenotifParam(JSONObject notifObject) {
        send(41,notifObject);
    }

    /**
     * Permet l'envoi d'un message en JSON sans donnée
     * @param action : Code de l'action à envoyer
     */
    public void send(int action) {
        send(action, (JSONObject)null);
    }

     public void send(int action, int data) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", action);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        performSend(json);
    }

    /**
     * Permet l'envoi d'un message en JSON
     * @param action : code de l'action
     * @param data : Objet JSON de données à envoyer
     */
    public void send(int action, JSONObject data) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", action);
            if (data != null) {
                json.put("data", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        performSend(json);
    }

    /**
     * Permet l'envoi d'un message en JSON
     * @param action : code de l'action
     * @param data : Tableau JSON de données à envoyer
     */
    public void send(int action, JSONArray data) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", action);
            if (data != null) {
                json.put("data", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        performSend(json);
    }

}
