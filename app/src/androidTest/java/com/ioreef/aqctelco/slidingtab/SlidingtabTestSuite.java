package com.ioreef.aqctelco.slidingtab;

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
public class SlidingtabTestSuite {

    /**
     * Suite de tests de AqcTelco.
     *
     * @return suite la suite de tests.
     *
     * @see TestSuite
     *
     */
    public static Test suite() {

        final TestSuite suite = new TestSuite("Suite de tests du package slidingtab.");

		/* Ajout des cas de test */
        suite.addTest(new TestSuite(SlidingTabLayoutTest.class));
        suite.addTest(new TestSuite(SlidingTabStripTest.class));
        suite.addTest(new TestSuite(ViewPagerAdapterTest.class));

        return suite;
    }
}