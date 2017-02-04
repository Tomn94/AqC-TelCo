package com.ioreef.aqctelco.tank;

import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 1.0
 * @date 27/04/2016
 * @author Antoine BRETECHER, Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class SwitcherTest extends InstrumentationTestCase {

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
        final boolean activated = true;
        final String name = "Écumeur";

        final Switcher switcher = new Switcher(id, addr, pin, pool, activated, name);
        JSONObject jSwitcher = switcher.serialize();
        try {
            // Test serialize
            assertEquals(jSwitcher.getInt("id"), switcher.getId());
            assertEquals(jSwitcher.getInt("addr"), switcher.getAddr());
            assertEquals(jSwitcher.getInt("pin"), switcher.getPin());
            assertEquals(jSwitcher.getInt("pool"), switcher.getPool());
            assertTrue(jSwitcher.getBoolean("state") == switcher.isActivated());
            assertEquals(jSwitcher.getString("name"), switcher.getName());

            // Test unserialize
            final Switcher switcher2 = new Switcher(jSwitcher);
            assertEquals(switcher2.getId(), switcher.getId());
            assertEquals(switcher2.getAddr(), switcher.getAddr());
            assertEquals(switcher2.getPin(), switcher.getPin());
            assertEquals(switcher2.getPool(), switcher.getPool());
            assertTrue(switcher2.isActivated() == switcher.isActivated());
            assertEquals(switcher2.getName(), switcher.getName());
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException fail");
        }
    }

    public void testSwitchAndSerialize() {
        final int id = 1, addr = 2, pin = 3, pool = 4;
        final boolean activated = true;
        final String name = "Écumeur";
        final Switcher switcher = new Switcher(id, addr, pin, pool, activated, name);
        JSONObject jSwitcher = switcher.switchAndSerialize();
        try {
            assertTrue(jSwitcher.getBoolean("state") == !switcher.isActivated());
            assertEquals(jSwitcher.getInt("id"), switcher.getId());
            assertEquals(jSwitcher.getInt("addr"), switcher.getAddr());
            assertEquals(jSwitcher.getInt("pin"), switcher.getPin());
            assertEquals(jSwitcher.getInt("pool"), switcher.getPool()); // <- a permis de corriger un bug dans switchAndSerialize
            assertEquals(jSwitcher.getString("name"), switcher.getName());
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException fail");
        }
    }

    public void testRenameAndSerialize() {
        final int id = 1, addr = 2, pin = 3, pool = 4;
        final boolean activated = true;
        final String name = "Écumeur";
        final String newName = "L'écumeur 2";
        final Switcher switcher = new Switcher(id, addr, pin, pool, activated, name);
        JSONObject jSwitcher = switcher.renameAndSerialize(newName);
        try {
            assertNotSame(jSwitcher.getString("name"), switcher.getName());
            assertEquals(jSwitcher.getString("name"), newName);
            assertEquals(jSwitcher.getInt("id"), switcher.getId());
            assertEquals(jSwitcher.getInt("addr"), switcher.getAddr());
            assertEquals(jSwitcher.getInt("pin"), switcher.getPin());
            assertEquals(jSwitcher.getInt("pool"), switcher.getPool());
            assertTrue(jSwitcher.getBoolean("state") == switcher.isActivated());
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException fail");
        }
    }

    public void testRenameAndSerializeWithChars() {
        final int id = 1, addr = 2, pin = 3, pool = 4;
        final boolean activated = true;
        final String name = "Écumeur";
        final String newName = "Je cite : \"L'écumeur 2\"";
        final Switcher switcher = new Switcher(id, addr, pin, pool, activated, name);
        JSONObject jSwitcher = switcher.renameAndSerialize(newName);
        try {
            assertNotSame(jSwitcher.getString("name"), switcher.getName());
            assertEquals(jSwitcher.getString("name"), newName);
            assertEquals(jSwitcher.getInt("id"), switcher.getId());
            assertEquals(jSwitcher.getInt("addr"), switcher.getAddr());
            assertEquals(jSwitcher.getInt("pin"), switcher.getPin());
            assertEquals(jSwitcher.getInt("pool"), switcher.getPool());
            assertTrue(jSwitcher.getBoolean("state") == switcher.isActivated());
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
        final int id=3, resultWaited=3;
        final Boolean activated=true;
        final String name="Relai";
        final Switcher switcher = new Switcher(id, 42, 0, 0, activated, name);
        int result=switcher.getId();
        assertEquals(result, resultWaited);
    }

    /**
     * Teste fonction isActivated().
     */
    @UiThreadTest
    public void testIsActivated(){
        final int id=3;
        final Boolean activated=true,resultWaited=true;
        final String name="Relai";
        final Switcher switcher = new Switcher(id, 42, 0, 0, activated, name);
        Boolean result=switcher.isActivated();
        assertEquals(result, resultWaited);
    }

    /**
     * Teste fonction getName().
     */
    @UiThreadTest
    public void testGetName(){
        final int id=3;
        final Boolean activated=true;
        final String name="Relai", resultWaited="Relai";
        final Switcher switcher = new Switcher(id, 42, 0, 0, activated, name);
        String result=switcher.getName();
        assertEquals(result, resultWaited);
    }


}
