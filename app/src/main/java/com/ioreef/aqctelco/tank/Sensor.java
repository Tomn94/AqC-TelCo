package com.ioreef.aqctelco.tank;

import android.content.Context;

import com.ioreef.aqctelco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe définissant un capteur
 *
 * @version 1.0
 * @date 04/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class Sensor {

    /**
     * Énumération des types possibles de réception
     * Les fonctions associées sont utiles à la création du type
     */
    public enum SensorType {
        TEMPERATURE(1),
        WATER_LEVEL(2),
        ELECTRIC_CURRENT(3),
        DETECT_LEAK(4),
        BUBBLE_COUNT(5),
        QUANTUM_SOLACE(6),
        UNKNOWN(0);

        private final int value;
        private static Map<Integer, SensorType> map = new HashMap<>();

        static {
            for (SensorType val : SensorType.values()) {
                map.put(val.getValue(), val);
            }
        }

        SensorType(final int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /* Attributs */
    private int id;          /**< identifiant unique d'un capteur */
    private int addr;        /**< adresse physique d'un capteur */
    private int pin;         /**< pin sur la carte d'un capteur */
    private int pool;        /**< identifiant du bassin dans lequel est le capteur */
    private List<Float> values = new ArrayList<>();  /**< valeurs relevées par un capteur */
    private String name;     /**< nom d'affichage du capteur */
    private SensorType type = SensorType.UNKNOWN;  /**< type calculé du capteur */


    /* Constructeurs */
    public Sensor(int id, int addr, int pin, int pool, List<Float> values, String name) {
        this.id = id;
        this.addr = addr;
        this.pin = pin;
        this.pool = pool;
        this.values = values;
        this.name = name;
        this.type = calculateTypeFromId();
    }

    /**
     * Constructeur utile pour créer un objet à partir d'une sérialisation réseau
     * @param fromJSON Objet reçu sur le réseau
     * @throws JSONException
     */
    public Sensor(JSONObject fromJSON) throws JSONException {
        this(fromJSON.getInt("id"),
             fromJSON.getInt("addr"),
             fromJSON.getInt("pin"),
             fromJSON.getInt("pool"),
             new ArrayList<Float>(),
             fromJSON.getString("name"));
        setValuesFromSerialization(fromJSON);
        this.type = calculateTypeFromId();
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

    public List<Float> getValues() {
        return values;
    }

    public Float getCurrentValue() {
        if (values.isEmpty())
            return 0f;
        return values.get(values.size() - 1);
    }

    /**
     * Applique un suffixe à la valeur courante du capteur pour indiquer l'éventuelle unité
     * @param context Utilisé afin de récupérer les chaînes par défaut d'unités
     * @return La valeur ainsi que l'éventuel suffixe
     */
    public String getDisplayValue(Context context) {

        float value = getCurrentValue();

        switch (getType()) {

            case TEMPERATURE:
                return context.getString(R.string.sensorSuffixTemperature, value);

            case WATER_LEVEL: {
                if (value < 1) {
                    return context.getResources().getStringArray(R.array.sensorLevelNames)[0];
                } else if (value < 2) {
                    return context.getResources().getStringArray(R.array.sensorLevelNames)[1];
                } else if (value < 3) {
                    return context.getResources().getStringArray(R.array.sensorLevelNames)[2];
                }
                return context.getResources().getStringArray(R.array.sensorLevelNames)[3];
            }

            case ELECTRIC_CURRENT:
                return context.getString(R.string.sensorSuffixEnergy, value);

            case DETECT_LEAK: {
                if (value >= 1) {
                    return context.getResources().getStringArray(R.array.sensorLeakValues)[1];
                }
                return context.getResources().getStringArray(R.array.sensorLeakValues)[0];
            }

            case BUBBLE_COUNT: {
                int count = (int) value;   // pluriel
                return context.getResources().getQuantityString(R.plurals.sensorSuffixBubbles, count, value);
            }

            case QUANTUM_SOLACE:
                return context.getString(R.string.sensorSuffixQuantum, value);

            default:
                return String.valueOf(value);

        }
    }

    public String getName() {
        return name;
    }

    public SensorType getType() {
        return type;
    }

    /**
     * @return Retourne le type de capteur selon son ID actuel
     */
    private SensorType calculateTypeFromId() {

        /*


    public enum SensorType {
        TEMPERATURE(1),
        WATER_LEVEL(2),
        ELECTRIC_CURRENT(4),
        DETECT_LEAK(8),
        BUBBLE_COUNT(16),
        QUANTUM_SOLACE(32),
        UNKNOWN(0);

        private final int value;
        private static Map<Integer, SensorType> map = new HashMap<>();

        static {
            for (SensorType val : SensorType.values()) {
                map.put(val.getValue(), val);
            }
        }

        SensorType(final int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        public static SensorType fromInt(int id) {
            SensorType res = map.get(id);
            if (res == null) {
                return SensorType.UNKNOWN;
            }
            return res;
        }
    }*/

        //private int SENSOR_ID_MASK = 0x01;

        /*if (((id >> 18) & 0xFF) != SENSOR_ID_MASK) {
            return SensorType.UNKNOWN;
        }
        return SensorType.fromInt((id >> 10) & 0xFF);*/


        /*
        0000 0000 | 0000 0000 | 0000 | 00 0000
           objet  |    type   | sous |   num
             8    |      8    |  15  |   63

        ex :
           Sensor | WaterLevel |   2  |    1
        0000 0001 | 0000 0010  | 0010 | 00 0001

         Switcher | WaveMaker  |   0  |    3
        0000 0010 | 0000 0001  | 0000 | 00 0011

        */

        if (id >= 0 && id <= 29) {
            return SensorType.TEMPERATURE;
        } else if (id >= 30 && id <= 49) {
            return SensorType.WATER_LEVEL;
        } else if (id >= 50 && id <= 59) {
            return SensorType.ELECTRIC_CURRENT;
        } else if (id >= 60 && id <= 69) {
            return SensorType.DETECT_LEAK;
        } else if (id >= 70 && id <= 74) {
            return SensorType.BUBBLE_COUNT;
        } else if (id >= 75 && id <= 79) {
            return SensorType.QUANTUM_SOLACE;
        }

        return SensorType.UNKNOWN;
    }

    /**
     * Charge les valeurs du capteur à partir du JSON complet de l'objet
     */
    public void setValuesFromSerialization(JSONObject fromJSON) throws JSONException {
        List<Float> newValues = new ArrayList<>();
        JSONArray jValues = fromJSON.getJSONArray("values");
        for (int j = 0 ; j < jValues.length() ; ++j) {
            newValues.add((float)jValues.getDouble(j));
        }
        this.values = newValues;
    }

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
            json.put("name", name);
            JSONArray jValues = new JSONArray(values);
            json.put("values", jValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
