package com.ioreef.aqctelco.telco;

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
public class TelcoTestSuite {

    /**
     * Suite de tests de AqcTelco.
     *
     * @return suite la suite de tests.
     *
     * @see TestSuite
     *
     */
    public static Test suite() {

        final TestSuite suite = new TestSuite("Suite de tests du package tabs.");

        /* Ajout du cas de test */
        suite.addTest(new TestSuite(DataTest.class));

        return suite;
    }
}