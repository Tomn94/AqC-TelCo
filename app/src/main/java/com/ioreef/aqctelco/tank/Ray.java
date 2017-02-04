package com.ioreef.aqctelco.tank;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe définissant un relai basique
 *
 * @version 1.0
 * @date 28/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class Ray {

    /* Attributs */
    private int id;             /**< identifiant unique d'une LED */
    private int addr;           /**< adresse physique d'une LED */
    private int idCurve;        /**< identifiant de la courbe associée */
    private Boolean state;      /**< état actuel d'activation d'une LED */
    private int idTemp;
    private int pin;            /**< pin sur la carte d'une LED */
    private int idAirCooler;
    private Boolean inverted;   /**< état actuel d'inversion d'une LED */


    /* Constructeurs */
    public Ray(int id, int addr, int idCurve, Boolean state, int idTemp, int pin, int idAirCooler, Boolean inverted) {
        this.id = id;
        this.addr = addr;
        this.idCurve = idCurve;
        this.state = state;
        this.idTemp = idTemp;
        this.pin = pin;
        this.idAirCooler = idAirCooler;
        this.inverted = inverted;
    }

    /**
     * Constructeur utile pour créer un Ray à partir d'un autre
     * Utile à la comparaison, pour vérifier si des données ont été modifiées à partir d'un original
     * @param other Objet reçu sur le réseau
     * @throws JSONException
     */
    public Ray(Ray other) {
        this.id = other.getId();
        this.addr = other.getAddr();
        this.idCurve = other.getIdCurve();
        this.state = other.isActivated();
        this.idTemp = other.getIdTemp();
        this.pin = other.getPin();
        this.idAirCooler = other.getIdAirCooler();
        this.inverted = other.isInverted();
    }

    /**
     * Constructeur utile pour créer un objet à partir d'une sérialisation réseau
     * @param fromJSON Objet reçu sur le réseau
     * @throws JSONException
     */
    public Ray(JSONObject fromJSON) throws JSONException {
        this(fromJSON.getInt("id"),
             fromJSON.getInt("addr"),
             fromJSON.getInt("idCurve"),
             fromJSON.getBoolean("state"),
             fromJSON.getInt("idTemp"),
             fromJSON.getInt("pin"),
             fromJSON.getInt("idAirCooler"),
             fromJSON.getBoolean("inverted"));
    }

    /* Actions */
    /**
     * Compare cet objet Ray à un autre
     * @param other Autre Ray avec lequel comparer l'objet courant
     * @return Retourne si les objets sont identiques
     */
    public boolean sameAs(Ray other) {
        return id == other.getId() &&
               addr == other.getAddr() &&
               pin == other.getPin() &&
               state == other.isActivated() &&
               inverted == other.isInverted() &&
               idCurve == other.getIdCurve() &&
               idTemp == other.getIdTemp() &&
               idAirCooler == other.getIdAirCooler() ;
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

    public Boolean isActivated() {
        return state;
    }

    public void activate() {
        this.state = true;
    }

    public void deactivate() {
        this.state = false;
    }

    public void setActivated(Boolean activated) {
        this.state = activated;
    }

    public Boolean isInverted() {
        return inverted;
    }

    public void setInverted(Boolean inverted) {
        this.inverted = inverted;
    }

    public int getIdCurve() {
        return idCurve;
    }

    public int getIdAirCooler() {
        return idAirCooler;
    }

    public int getIdTemp() {
        return idTemp;
    }

    public void setIdCurve(int idCurve) {
        this.idCurve = idCurve;
    }

    public int getDisplayId() {
        return id - 200 + 1;
    }

    public int getDisplayIdCurve() {
        return idCurve - 300 + 1;
    }

    /**
     * Convertit l'objet au format JSON
     * @return L'objet en notation JSON
     */
    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        try {
            json.put("idRay", id);
            json.put("addr", addr);
            json.put("pin", pin);
            json.put("state", state);
            json.put("inverted", inverted);
            json.put("idCurve", idCurve);
            json.put("idTemp", idTemp);
            json.put("idAirCooler", idAirCooler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Crée un équivalent de l'objet au format JSON et y applique des modifications des attributs
     * Fonction utilisée pour envoyer une proposition de modification à Supervision
     *
     * @param newState     Décrit un nouvel (ou non) état
     * @param newInverted  Décrit un nouvel (ou non) état d'inversion des branchements
     * @param newIdCurve   Décrit un nouveau (ou non) numéro de courbe associée
     * @return Le nouvel objet ainsi créé
     */
    public JSONObject editAndSerialize(Boolean newState, Boolean newInverted, int newIdCurve) {
        JSONObject json = this.serialize();
        try {
            json.put("state", newState);
            json.put("inverted", newInverted);
            json.put("idCurve", newIdCurve);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
