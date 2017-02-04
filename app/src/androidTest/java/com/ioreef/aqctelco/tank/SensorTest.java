package com.ioreef.aqctelco.tank;

import android.test.InstrumentationTestCase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @date 20/05/2016
 * @author Antoine BRETECHER, Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class SensorTest extends InstrumentationTestCase {

    /**
     * @brief Crée le produit nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void setUp() throws Exception{

    }

    /**
     * @brief Termine l'activité préalablement lancée pour les tests.
     *
     * <p>Code exécuté après les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void tearDown() throws Exception{
        super.tearDown();
    }

    /**
     * @brief Pré-condition d'existence des widgets
     */
    public void testPreconditions(){

    }

    public void testSerialize() {
        final int id = 1, addr = 2, pin = 3, pool = 4;
        List<Float> values = new ArrayList<>();
        values.add(24.5f);
        final String name = "Température";

        final Sensor sensor = new Sensor(id, addr, pin, pool, values, name);
        JSONObject jSensor = sensor.serialize();
        try {
            // Test serialize
            assertEquals(jSensor.getInt("id"), sensor.getId());
            assertEquals(jSensor.getInt("addr"), sensor.getAddr());
            assertEquals(jSensor.getInt("pin"), sensor.getPin());
            assertEquals(jSensor.getInt("pool"), sensor.getPool());
            assertEquals(jSensor.getString("name"), sensor.getName());
            Log.d("test", jSensor.toString());
            JSONArray jValues = jSensor.getJSONArray("values"); // <- a permis de corriger 1 bug dans serialize()
            List<Float> jValList = new ArrayList<>();
            for (int i = 0 ; i < jValues.length() ; ++i) {
                jValList.add((float)jValues.getDouble(i));
            }
            assertEquals(jValList.size(), sensor.getValues().size());
            for (int i = 0 ; i < jValList.size() ; ++i) {
                assertEquals(jValList.get(i), sensor.getValues().get(i));
            }

            // Test unserialize
            final Sensor switcher2 = new Sensor(jSensor);
            assertEquals(switcher2.getId(), sensor.getId());
            assertEquals(switcher2.getAddr(), sensor.getAddr());
            assertEquals(switcher2.getPin(), sensor.getPin());
            assertEquals(switcher2.getPool(), sensor.getPool());
            assertEquals(switcher2.getName(), sensor.getName());
            assertEquals(switcher2.getValues().size(), sensor.getValues().size());
            for (int i = 0 ; i < switcher2.getValues().size() ; ++i) {
                assertEquals(switcher2.getValues().get(i), sensor.getValues().get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException fail");
        }
    }

    public void testTypeFromId() {
        int id;
        Sensor sensor;

        /* Test valeurs frontières température */
        id = -1;
        sensor = new Sensor(id, 0, 0, 0, new ArrayList<Float>(), "Capteur");
        assertEquals(sensor.getType(), Sensor.SensorType.UNKNOWN);

        id = 0;
        sensor = new Sensor(id, 0, 0, 0, new ArrayList<Float>(), "Capteur");
        assertEquals(sensor.getType(), Sensor.SensorType.TEMPERATURE);

        id = 1;
        sensor = new Sensor(id, 0, 0, 0, new ArrayList<Float>(), "Capteur");
        assertEquals(sensor.getType(), Sensor.SensorType.TEMPERATURE);

        id = 28;
        sensor = new Sensor(id, 0, 0, 0, new ArrayList<Float>(), "Capteur");
        assertEquals(sensor.getType(), Sensor.SensorType.TEMPERATURE);

        id = 29;
        sensor = new Sensor(id, 0, 0, 0, new ArrayList<Float>(), "Capteur");
        assertEquals(sensor.getType(), Sensor.SensorType.TEMPERATURE);

        id = 30;
        sensor = new Sensor(id, 0, 0, 0, new ArrayList<Float>(), "Capteur");
        assertEquals(sensor.getType(), Sensor.SensorType.WATER_LEVEL);
    }

}
