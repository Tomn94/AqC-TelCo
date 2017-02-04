package com.ioreef.aqctelco.network;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @brief Classe exemple de suite de tests unitaire
 *
 * @version 1.0
 * @date 27/04/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class NetworkTestSuite {

    /**
     * Suite de tests de AqcTelco.
     *
     * @return suite la suite de tests.
     *
     * @see TestSuite
     *
     */
    public static Test suite() {

        final TestSuite suite = new TestSuite("Suite de tests de du package network.");

		/* Ajout des cas de test */
        suite.addTest(new TestSuite(ConnectionTest.class));
        suite.addTest(new TestSuite(ConnectionTaskTest.class));
        suite.addTest(new TestSuite(ProxyTankTest.class));
        suite.addTest(new TestSuite(DispatcherUITest.class));
        suite.addTest(new TestSuite(ReceiverTest.class));
        suite.addTest(new TestSuite(SenderTest.class));


        return suite;
    }
}