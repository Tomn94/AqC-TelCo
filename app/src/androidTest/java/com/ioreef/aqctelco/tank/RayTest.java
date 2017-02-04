package com.ioreef.aqctelco.tank;

import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 1.0
 * @date 29/04/2016
 * @author Antoine BRETECHER, Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class RayTest extends InstrumentationTestCase {

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

    public void testComparison() {
        Ray ray1 = new Ray(1, 2, 300, true, 1, 1, 1, false);
        Ray ray2 = new Ray(1, 2, 301, true, 1, 1, 1, false);
        Ray ray3 = new Ray(1, 2, 302, true, 1, 1, 1, false);
        assertFalse(ray1.sameAs(ray2));
        assertTrue(ray1.sameAs(new Ray(ray3)));
    }

    public void testEditAndSerialize() {
        final int id = 1, addr = 2, pin = 3, idCurve = 302, idTemp = 1, idAirCooler = 1;
        final boolean state = true, inverted = false;
        final Ray ray = new Ray(id, addr, idCurve, state, idTemp, pin, idAirCooler, inverted);
        final int newIdCurve = 301;
        JSONObject jSwitcher = ray.editAndSerialize(false, true, newIdCurve);
        try {
            assertTrue(jSwitcher.getBoolean("state") == !ray.isActivated());
            assertTrue(jSwitcher.getBoolean("inverted") == !ray.isInverted());
            assertTrue(jSwitcher.getInt("idCurve") != ray.getIdCurve());
            assertTrue(jSwitcher.getInt("idCurve") == newIdCurve);
            assertEquals(jSwitcher.getInt("idRay"), ray.getId());
            assertEquals(jSwitcher.getInt("addr"), ray.getAddr());
            assertEquals(jSwitcher.getInt("pin"), ray.getPin());
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException fail");
        }
    }

    /**
     * Teste fonction getId().
     */
    @UiThreadTest
    public void testGetId(){
        int id=3, idCurve=7, resultWaited=3;
        Boolean activated=true, inverted=true;
        final Ray ray = new Ray(id, 2, idCurve, activated, 1, 1, 1, inverted);
        int result=ray.getId();
        assertEquals(result, resultWaited);
    }

    /**
     * Teste fonction isActivated().
     */
    @UiThreadTest
    public void testIsActivated(){
        int id=3, idCurve=7;
        Boolean activated=true, inverted=true, resultWaited=true;
        final Ray ray = new Ray(id, 2, idCurve, activated, 1, 1, 1, inverted);
        Boolean result=ray.isActivated();
        assertEquals(result, resultWaited);
    }

    /**
     * Teste fonction isInverted().
     */
    @UiThreadTest
    public void testIsInverted(){
        int id=3, idCurve=7;
        Boolean activated=true, inverted=true, resultWaited=true;
        final Ray ray = new Ray(id, 2, idCurve, activated, 1, 1, 1, inverted);
        Boolean result=ray.isInverted();
        assertEquals(result, resultWaited);
    }

    /**
     * Teste fonction getIdCurve().
     */
    @UiThreadTest
    public void testGetIdCurve(){
        int id=3, idCurve=7, resultWaited=7;
        Boolean activated=true, inverted=true;
        final Ray ray = new Ray(id, 2, idCurve, activated, 1, 1, 1, inverted);
        int result=ray.getIdCurve();
        assertEquals(result, resultWaited);
    }







}
