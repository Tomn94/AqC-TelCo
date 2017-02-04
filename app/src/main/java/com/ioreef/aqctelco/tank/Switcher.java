package com.ioreef.aqctelco.tank;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe définissant un relai basique
 *
 * @version 1.0
 * @date 22/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class Switcher {

    /* Attributs */
    private int id;         /**< identifiant unique d'un relai */
    private int addr;       /**< adresse physique d'un relai */
    private int pin;        /**< pin sur la carte d'un relai */
    private int pool;
    private Boolean state;  /**< état actuel d'activation d'un relai */
    private String name;    /**< nom d'affichage d'un relai */


    /* Constructeurs */
    public Switcher(int id, int addr, int pin, int pool, Boolean state, String name) {
        this.id = id;
        this.addr = addr;
        this.pin = pin;
        this.pool = pool;
        this.state = state;
        this.name = name;
    }

    /**
     * Constructeur utile pour créer un objet à partir d'une sérialisation réseau
     * @param fromJSON Objet reçu sur le réseau
     * @throws JSONException
     */
    public Switcher(JSONObject fromJSON) throws JSONException {
        this(fromJSON.getInt("id"),
             fromJSON.getInt("addr"),
             fromJSON.getInt("pin"),
             fromJSON.getInt("pool"),
             fromJSON.getBoolean("state"),
             fromJSON.getString("name"));
    }


    /* Get/Set */
    public int getId() {
        return id;
    }

    public int getAddr() {
        return addr;
    }

    public int getPin() {
        return pin;
    }

    public int getPool() {
        return pool;
    }

    public Boolean isActivated() {
        return state;
    }

    /*public void activate() {
        this.activated = true;
    }

    public void deactivate() {
        this.activated = false;
    }

    public void switchState() {
        activated = !activated;
    }*/

    public String getName() {
        return name;
    }

    /*public void setName(String name) {
        this.name = name;
    }*/

    /**
     * Convertit l'objet au format JSON
     * @return L'objet en notation JSON
     */
    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("addr", addr);
            json.put("pin", pin);
            json.put("pool", pool);
            json.put("state", state);
            json.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Crée un équivalent de l'objet au format JSON et y applique une inversion de l'état
     * Fonction utilisée pour envoyer une proposition de modification à Supervision
     *
     * @return Le nouvel objet ainsi créé
     */
    public JSONObject switchAndSerialize() {
        JSONObject json = this.serialize();
        try {
            json.put("state", !state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Crée un équivalent de l'objet au format JSON et y applique une modification de son nom
     * Fonction utilisée pour envoyer une proposition de modification à Supervision
     *
     * @param newName Décrit un nouveau (ou non) nom
     * @return Le nouvel objet ainsi créé
     */
    public JSONObject renameAndSerialize(String newName) {
        JSONObject json = this.serialize();
        try {
            json.put("name", newName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
