package com.ioreef.aqctelco.tank;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe définissant une courbe
 *
 * @version 1.0
 * @date 04/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class Curve {

    /* Attributs */
    private int id;                                   /**< identifiant unique d'une courbe */
    private List<Integer> points = new ArrayList<>(); /**< points d'une courbe */


    /* Constructeurs */
    public Curve(int id, List<Integer> points) {
        this.id = id;
        this.points = points;
    }

    /**
     * Constructeur utile pour créer un objet à partir d'une sérialisation réseau
     * @param fromJSON Objet reçu sur le réseau
     * @throws JSONException
     */
    public Curve(JSONObject fromJSON) throws JSONException {
        this(fromJSON.getInt("id"),
             new ArrayList<Integer>());
        setValuesFromSerialization(fromJSON);    // Fonction annexe nécessaire car this doit être la 1ère ligne du constructeur, et non un try {
    }


    /* Get/Set */
    public int getId() {
        return id;
    }

    public List<Integer> getPoints() {
        return points;
    }

    public int getDisplayId() {
        return id - 300 + 1;
    }

    /**
     * Charge les valeurs de la courbe à partir du JSON complet de l'objet.
     */
    public void setValuesFromSerialization(JSONObject fromJSON) throws JSONException {
        List<Integer> newValues = new ArrayList<>();
        JSONArray jValues = fromJSON.getJSONArray("power");
        for (int j = 0 ; j < jValues.length() ; ++j) {
            newValues.add(jValues.getInt(j));
        }
        this.points = newValues;
    }

    /**
     * Convertit l'objet au format JSON
     * @return L'objet en notation JSON
     */
    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            JSONArray jPoints = new JSONArray(points);
            json.put("power", jPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
