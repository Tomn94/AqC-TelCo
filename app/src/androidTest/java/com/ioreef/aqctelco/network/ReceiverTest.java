package com.ioreef.aqctelco.network;

import android.test.InstrumentationTestCase;

import com.ioreef.aqctelco.MainActivity;

/**
 * @version 1.0
 * @date 20/05/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class ReceiverTest extends InstrumentationTestCase {

    private Receiver receiver;

    /**
     * @brief Crée le produit nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void setUp() throws Exception{
//        receiver = new Receiver();
//        receiver.start();
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

    /**
     * Test fonction 1 avec données de test n°1.
     */
    public void testReceptionMessage() {
//        assertNotNull(receiver);
//        assertEquals(receiver.isAlive(), true);

    }

    public void testReceptionUnknownMessage() {

    }


}
