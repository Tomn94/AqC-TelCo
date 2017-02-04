package com.ioreef.aqctelco.network;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.ioreef.aqctelco.MainActivity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 *
 * @version 1.0
 * @date 02/05/2016
 * @author Remy SALIM, Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class ConnectionTest extends ActivityInstrumentationTestCase2<MainActivity> {
    /* Temporisation pour l'enchaînement des lancements de l'activité testée. */
    private static final int TMP_INTER_ACTIVITY = 1000;
    private static final int FRAGMENT_RELAY_POSITION = 1;

    /* Activité testée. */
    private MainActivity activity;

    /* Instrumentation des tests. */
    private Instrumentation instrumentation;

    private Connection connection;

    /**
     * Constructeur (lié à l'activité associée à l'application testée).
     */
    public ConnectionTest() {
        super(MainActivity.class);
        connection = Connection.getConnection(activity);
    }

    /** Lancement de l'activité Android Exemple nécessaire aux tests.
     * Récupération des widgets de la vue testée.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void setUp() throws Exception{
        super.setUp();
        /* allocation d'un cache pour dexmaker (nécessaire pour EasyMock) */
        System.setProperty(
                "dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath());

		/* lancement de l'activité testée */
        this.activity  = this.getActivity();

		/* instrumentation du test */
        this.instrumentation = this.getInstrumentation();

		/* aux widgets de la vue caisse */
        this.recuperationWidgetInterface();
    }

    /**
     * Récupération des références des widgets (champs textes) de l'interface.
     */
    private void recuperationWidgetInterface(){

    }

    /**
     * Termine l'activité préalablement lancée pour les tests.
     *
     * <p>Code exécuté après les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void tearDown() throws Exception{
        super.tearDown();
		/* Temporisation entre les lancements de l'activité testée. */
        Thread.sleep(TMP_INTER_ACTIVITY);
    }

    /**
     * @brief Pré-condition d'existence des widgets
     */
    public void testPreconditions(){
        assertNotNull(activity);
        assertNotNull(connection);
    }


    /**
     * Test nominal de la fonction ipIsValid()
     */
    @UiThreadTest
    public void testIpIsValidDonneesValides() {
        String dt1 = "192.168.15.1", dt2="255.255.0.255";
        Boolean resultExpected = true;
        assertEquals(resultExpected,connection.ipIsValid(dt1));
        assertEquals(resultExpected,connection.ipIsValid(dt2));

    }

    /**
     * Tests de robustesse de la fonction ipIsValid()
     */
    @UiThreadTest
    public void testIpIsValidDonneesNonValides() {
        String dt1 = "192.168.5.256", dt2 = "192.168.5.a", dt3 = "192.168.5.1111",
                dt4 = "192.168.5", dt5="", dt6="192.168.5.5:80";
        Boolean resultExpected = false;
        assertEquals(resultExpected,connection.ipIsValid(dt1));
        assertEquals(resultExpected,connection.ipIsValid(dt2));
        assertEquals(resultExpected,connection.ipIsValid(dt3));
        assertEquals(resultExpected,connection.ipIsValid(dt4));
        assertEquals(resultExpected,connection.ipIsValid(dt5));
        assertEquals(resultExpected,connection.ipIsValid(dt6));

    }

    /**
     * Test d'une perte de connexion
     */
    public void testFailedConnection(){
        Boolean resultExpected = false;
        assertEquals(resultExpected,connection.getConnectionState());
    }

    /**
     * Test de l'instanciation de SummaryUpdater sur invocation de Connection
     */
    public void testRefreshingSummary() {
        connection.refreshingSummary(true);
        assertNotNull(connection.getSummaryUpdater());
    }

    /**
     * Test des fonction saveIp() et loadIp()
     * Méthode d'introspection pour la fonction loadIp
     */
    @UiThreadTest
    public void testSaveLoadIp(){
        String dt1 = "192.168.15.1", resultExpected="192.168.15.1";
        try {
            connection.saveIp(dt1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String methodeTestee = "loadIp";
        Class<?>[] parametresMethodetestee = {};

        Method methode;

        try {
            /* Accès à la méthode. */
            methode = Connection.class.getDeclaredMethod(methodeTestee, parametresMethodetestee);
            methode.setAccessible(true);
            String result = (String)methode.invoke(connection);
            assertEquals("Test du chargement d'une ip sauvegardée : ", result, resultExpected);

        } catch (SecurityException e) {
            fail("Problème de sécurité sur la réflexion.");
        } catch (NoSuchMethodException e) {
            fail("Méthode testée non existante.");
        } catch (IllegalArgumentException e) {
            fail("Arguments de la méthode testée invalides.");
        } catch (IllegalAccessException e) {
            fail("Accès illégal à la méthode testée.");
        } catch (InvocationTargetException e) {
            fail("Problème d'invocation de la méthode testée.");
        }
    }

}
