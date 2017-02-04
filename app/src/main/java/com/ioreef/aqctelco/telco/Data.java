package com.ioreef.aqctelco.telco;

import android.app.Activity;

import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.tank.Sensor;
import com.ioreef.aqctelco.tank.Switcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de stockage de données temporaires
 *
 * @version 1.0
 * @date 09/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class Data {

    public enum ListenerType {
        SENSOR, SWITCHER, RAY, CURVE
    }

    private static Data instance = null;                     /**< Instance de Data */

    /* Attributs */
    private List<Sensor>   sensorList   = new ArrayList<>(); /**< Liste des capteurs retournés par Supervision */
    private List<Switcher> switcherList = new ArrayList<>(); /**< Liste des relais retournés par Supervision */
    private List<Ray>      rayList      = new ArrayList<>(); /**< Liste des LED retournées par Supervision */
    private List<Curve>    curveList    = new ArrayList<>(); /**< Liste des courbes retournées par Supervision */
    private Map<ListenerType, List<DataChangeListener>> listenerListForType = new HashMap<>();
    private DataSaveListener saveListener = null;

    private Activity currentActivity = null;                 /**< Stocke l'activité actuelle présentée à l'écran */


    /* Constructeurs */
    /**
     * Retourne l'instance de Data
     * @return l'unique instance de Data
     */
    public static Data getData() {
        if (instance == null) {
            synchronized (Data.class) {
                instance = new Data();
                instance.listenerListForType.put(ListenerType.SENSOR,   new ArrayList<DataChangeListener>());
                instance.listenerListForType.put(ListenerType.SWITCHER, new ArrayList<DataChangeListener>());
                instance.listenerListForType.put(ListenerType.RAY,      new ArrayList<DataChangeListener>());
                instance.listenerListForType.put(ListenerType.CURVE,    new ArrayList<DataChangeListener>());
            }
        }
        return instance;
    }


    /* GET/SET */
    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
        Data.getData().notifyListListenersOfType(ListenerType.SENSOR);
    }

    public List<Switcher> getSwitcherList() {
        return switcherList;
    }

    public void setSwitcherList(List<Switcher> switcherList) {
        this.switcherList = switcherList;
        Data.getData().notifyListListenersOfType(ListenerType.SWITCHER);
    }

    public List<Ray> getRayList() {
        return rayList;
    }

    public void setRayList(List<Ray> rayList) {
        this.rayList = rayList;
        Data.getData().notifyListListenersOfType(ListenerType.RAY);
    }

    public List<Curve> getCurveList() {
        return curveList;
    }

    public void setCurveList(List<Curve> curveList) {
        this.curveList = curveList;
        Data.getData().notifyListListenersOfType(ListenerType.CURVE);
    }


    /* Actions */
    public void addListListener(DataChangeListener listener, ListenerType ofType) {
        List<DataChangeListener> listeners = listenerListForType.get(ofType);
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListListener(DataChangeListener listener, ListenerType ofType) {
        List<DataChangeListener> listeners = listenerListForType.get(ofType);
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /**
     * Notifie les abonnés aux changements d'une certaine liste
     * @param type Type de données correspondant à une liste qui a été modifiée
     */
    public void notifyListListenersOfType(ListenerType type) {
        List<DataChangeListener> listeners = listenerListForType.get(type);
        for (DataChangeListener listener : listeners) {
            if (listener != null) {
                listener.onDataChanged();
            }
        }
    }


    public DataSaveListener getSaveListener() {
        return saveListener;
    }

    public void setSaveListener(DataSaveListener saveListener) {
        this.saveListener = saveListener;
    }

    /**
     * Notifie les abonnés que l'action de sauvegarde par Supervision des nouvelles données est terminée
     */
    public void notifySaveListener() {
        if (saveListener != null) {
            saveListener.onDataSaved();
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

}
