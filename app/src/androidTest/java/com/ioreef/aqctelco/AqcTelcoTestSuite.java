package com.ioreef.aqctelco;

import com.ioreef.aqctelco.tank.TankTestSuite;
import com.ioreef.aqctelco.network.NetworkTestSuite;
import com.ioreef.aqctelco.slidingtab.SlidingtabTestSuite;
import com.ioreef.aqctelco.tabs.TabsTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @brief Classe de suite de tests unitaire
 *
 * @version 1.0
 * @date 27/04/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class AqcTelcoTestSuite {

    /**
     * Suite de tests de AqcTelco.
     *
     * @return suite la suite de tests.
     *
     * @see junit.framework.TestSuite
     *
     */
    public static Test suite() {

        final TestSuite suite = new TestSuite("Suite de tests de AqcTelo.");

		/* Ajout des cas de tests */
//        suite.addTest(ListenersTestSuite.suite());
        suite.addTest(TankTestSuite.suite());
        suite.addTest(NetworkTestSuite.suite());
        suite.addTest(SlidingtabTestSuite.suite());
        suite.addTest(TabsTestSuite.suite());
        /* Ajout des suites de tests */
        suite.addTest(new TestSuite(LightActivityTest.class));
        suite.addTest(new TestSuite(MainActivityTest.class));

        return suite;
    }
}