package com.ioreef.aqctelco.dialogs;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @brief Classe exemple de suite de tests unitaire
 *
 * @version 1.0
 * @date 05/05/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class DialogTestSuite {

    /**
     * Suite de tests de AqcTelco.
     *
     * @return suite la suite de tests.
     *
     * @see TestSuite
     *
     */
    public static Test suite() {

        final TestSuite suite = new TestSuite("Suite de tests du package dialogs.");

		/* Ajout des cas de test */
        suite.addTest(new TestSuite(DialogLightEditTest.class));
        suite.addTest(new TestSuite(DialogGeneratorTest.class));

        return suite;
    }
}