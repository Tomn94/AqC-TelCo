package com.ioreef.aqctelco.network;

import android.os.AsyncTask;
import android.util.Log;

import com.ioreef.aqctelco.dialogs.DialogGenerator;
import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.tank.Sensor;
import com.ioreef.aqctelco.tank.Switcher;
import com.ioreef.aqctelco.telco.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe en parallèle traitant un message qui lui est donnée et en informe le destinataire
 *
 * @version 1.0
 * @date 10/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class DispatcherUI extends AsyncTask<String, Void, Integer> {

    private int success = 0;                /**< Bonne exécution de l'analyse du message reçu */
    private ReceivedDataType action;        /**< Type du message reçu */
    private List<?> result;                 /**< Liste d'objets analysés dans la réception */

    /**
     * Énumération des types possibles de réception
     * Les fonctions associées sont utiles à la création du type
     */
    private enum ReceivedDataType {
        RECEIVE_SENSORS(10),
        RECEIVE_SWITCHERS(20),
        RECEIVE_RAY(30),
        RECEIVE_CURVES(35),
        RECEIVE_NOTIFICATIONS(40),
        RECEIVE_PUSH_NOTIFICATION(45),

        ORDER_RESPONSE_SWITCHER_EDIT(21),
        ORDER_RESPONSE_RAY_EDIT(31),
        ORDER_RESPONSE_NOTIFICATION_EDIT(41);

        private final int value;
        private static Map<Integer, ReceivedDataType> map = new HashMap<>();

        static {
            for (ReceivedDataType val : ReceivedDataType.values()) {
                map.put(val.getValue(), val);
            }
        }

        ReceivedDataType(final int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        /**
         * Crée le type énuméré à partir d'un Entier reçu sur le réseau
         * @param actionId Entier d'action réalisée par la demande réseau
         * @return Action du type de l'énumération
         */
        public static ReceivedDataType fromInt(int actionId) {
            return map.get(actionId);
        }
    }

    /**
     * Charge une liste de données à partir d'une liste de données reçues au format JSON
     * La liste est ainsi chargée avec des objets de type correspondant à l'action reçue
     *
     * @param fromJSONArray Liste de données reçues
     * @param actionId Type de données reçues
     * @return Une nouvelle liste de données
     * @throws JSONException
     */
    private ArrayList loadList(JSONArray fromJSONArray, ReceivedDataType actionId) throws JSONException {
        ArrayList newList = new ArrayList();
        if (fromJSONArray != null) {
            for (int i = 0 ; i < fromJSONArray.length() ; ++i) {
                JSONObject jObj = fromJSONArray.getJSONObject(i);
                switch (actionId) {
                    case RECEIVE_SENSORS: {
                        if (!jObj.has("hidden") ||
                            (jObj.has("hidden") && !jObj.getBoolean("hidden"))) {
                            newList.add(new Sensor(jObj));
                        }
                        break;
                    }
                    case RECEIVE_SWITCHERS:
                    case ORDER_RESPONSE_SWITCHER_EDIT:
                        newList.add(new Switcher(jObj));
                        break;
                    case RECEIVE_RAY:
                    case ORDER_RESPONSE_RAY_EDIT:
                        newList.add(new Ray(jObj));
                        break;
                    case RECEIVE_CURVES:
                        newList.add(new Curve(jObj));
                        break;
                    case RECEIVE_NOTIFICATIONS:
                    case ORDER_RESPONSE_NOTIFICATION_EDIT:
                        break;
                }
            }
        }
        return newList;

    }

    /**
     * Traitement d'un message reçu de Supervision vers TelCo
     * @param params Messages transmis
     * @return Bonne analyse du message
     */
    @Override
    protected Integer doInBackground(String... params) {
        JSONObject message;
        try {
            message = new JSONObject(params[0]);
            if (message != null)
                treatMessage(message);
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return success;
    }

    /**
     * Traitement d'un message JSON reçu de Supervision vers TelCo
     *
     * @param message Message transmis au format JSON
     * @return Bonne analyse du message
     */
    protected void treatMessage(JSONObject message) throws JSONException {
        if (message.has("action")) {
            action = ReceivedDataType.fromInt(message.getInt("action"));
            if (action != null) {
                switch (action) {
                    case RECEIVE_SENSORS: {
                        Log.d("JSON","RECEIVED SENSORS");
                        result = loadList(message.getJSONArray("data"),action);
                        success = 1;
                        break;
                    }
                    case RECEIVE_SWITCHERS: {
                        Log.d("JSON","RECEIVED SWITCHERS");
                        result = loadList(message.getJSONArray("data"),action);
                        success = 1;
                        break;
                    }
                    case RECEIVE_RAY: {
                        Log.d("JSON","RECEIVED RAY, Hi Ray");
                        result = loadList(message.getJSONArray("data"),action);
                        success = 1;
                        break;
                    }
                    case RECEIVE_CURVES: {
                        Log.d("JSON","RECEIVED CURVES");
                        result = loadList(message.getJSONArray("data"),action);
                        success = 1;
                        break;
                    }
                    case RECEIVE_NOTIFICATIONS: {
                        break;
                    }

                    case RECEIVE_PUSH_NOTIFICATION: {
                        break;
                    }
                    case ORDER_RESPONSE_SWITCHER_EDIT: {
                        JSONObject data = message.getJSONObject("data");
                        if (data.getInt("result") == 1) {  // Supervision a bien réalisé l'ordre
                            result = loadList(data.getJSONArray("newData"),action);
                            success = 1;
                        } else {
                            success = -1;
                        }
                        break;
                    }
                    case ORDER_RESPONSE_RAY_EDIT: {
                        JSONObject data = message.getJSONObject("data");
                        if (data.getInt("result") == 1) {  // Supervision a bien réalisé l'ordre
                            result = loadList(data.getJSONArray("newData"),action);
                            success = 1;
                        } else {
                            success = -1;
                        }
                        break;
                    }
                    case ORDER_RESPONSE_NOTIFICATION_EDIT: {
                        JSONObject data = message.getJSONObject("data");
                        if (data.getInt("result") == 1) {  // Supervision a bien réalisé l'ordre
                        } else {}
                        break;
                    }
                }
            }
        }
    }

    /**
     * Lorsque la tâche de réception est terminée, mise à jour de l'UI
     */
    @Override
    protected void onPostExecute(Integer res) {

        if (success == 1 && action != null && result != null) {
            /* Applique les données à l'UI */
            switch (action) {
                case RECEIVE_SENSORS: {
                    // TODO: Fusionner les capteurs de niveau
                    Data.getData().setSensorList((List<Sensor>) result);
                    break;
                }
                case RECEIVE_SWITCHERS:
                case ORDER_RESPONSE_SWITCHER_EDIT:
                    Data.getData().setSwitcherList((List<Switcher>) result);
                    break;
                case ORDER_RESPONSE_RAY_EDIT:
                    Data.getData().notifySaveListener();    // Indique à l'UI que les données ont été sauvegardées
                case RECEIVE_RAY:
                    Data.getData().setRayList((List<Ray>) result);
                    break;
                case RECEIVE_CURVES:
                    Data.getData().setCurveList((List<Curve>) result);
                    break;
            }
        } else if (success == -1) {
            DialogGenerator dialogGenerator = new DialogGenerator(Data.getData().getCurrentActivity());
            switch (action) {
                case ORDER_RESPONSE_SWITCHER_EDIT:
                    dialogGenerator.generate(DialogGenerator.DialogType.WRONG_RESPONSE_SWITCHER).show();
                    break;
                case ORDER_RESPONSE_RAY_EDIT:
                    dialogGenerator.generate(DialogGenerator.DialogType.WRONG_RESPONSE_RAY).show();
                    break;
            }
        }
    }
}
