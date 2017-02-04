package com.ioreef.aqctelco.tabs;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.ioreef.aqctelco.MainActivity;
import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.Sensor;
import com.ioreef.aqctelco.telco.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @version 1.0
 * @date 27/04/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class SummaryFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    /* Temporisation pour l'enchaînement des lancements de l'activité testée. */
    private static final int TMP_INTER_ACTIVITY = 1000;

    /* Activité testée. */
    private Activity activity;

    /* Instrumentation des tests. */
    private Instrumentation instrumentation;

    /**
     * Constructeur (lié à l'activité associée à l'application testée).
     */
    public SummaryFragmentTest() {
        super(MainActivity.class);
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
     * Pré-condition d'existence des widgets
     */
    public void testPreconditions(){

    }

    public void testAddSensors() {
        List<Sensor> sensorList = new ArrayList<>();

        sensorList.add(new Sensor(1, 0, 0, 0, Arrays.asList(25.0f, 25.2f, 25.3f, 25.4f, 25.4f,25.3f,25.3f,25.2f,25.1f,25.0f,24.8f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f), "Température bassin"));
        sensorList.add(new Sensor(2, 0, 0, 0, Arrays.asList(25.1f, 25.3f, 25.4f, 25.4f, 25.5f,25.5f,25.4f,25.3f,25.2f,25.1f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.1f,25.1f,25.1f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.1f,25.1f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f), "Température décante"));
        sensorList.add(new Sensor(3, 0, 0, 0, Arrays.asList(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.48f,0.45f,0.4f,0.4f,0.4f,0.45f,0.47f,0.5f,0.5f,0.5f,0.55f,0.57f,0.6f,0.6f,0.6f,0.57f,0.55f,0.5f), "Consommation"));
        sensorList.add(new Sensor(4, 0, 0, 0, Collections.singletonList(1.2f), "Niveau d'eau douce"));
        sensorList.add(new Sensor(5, 0, 0, 0, Collections.singletonList(1.0f), "Compte-bulles"));
        sensorList.add(new Sensor(6, 0, 0, 0, Collections.singletonList(1300.0f), "Quantum mètre"));
        Data.getData().setSensorList(sensorList);
        assertEquals(sensorList, Data.getData().getSensorList());
    }

    public void testAddCurves() {
        List<Curve> curveList = new ArrayList<>();
        curveList.add(new Curve(1, Arrays.asList(1,1,1,3,5,7,10,10,10,10,10,10,10,10,10,10,10,15,20,30,40,50,60,70,80,90,95,100,100,95,90,80,70,60,50,40,30,20,10,10,10,10,10,7,5,3,1,1)));
        curveList.add(new Curve(2, Arrays.asList(1,1,1,3,5,7,10,12,15,17,15,12,10,10,10,10,10,15,20,30,40,50,60,70,80,90,95,100,100,95,90,80,70,60,50,40,30,20,10,10,10,10,10,7,5,3,1,1)));
        curveList.add(new Curve(2, Arrays.asList(1,1,1,3,5,7,10,12,15,17,15,12,10,10,10,10,10,15,20,30,40,50,60,70,80,90,95,100,100,95,90,80,70,60,50,40,30,20,10,10,10,10,10,7,5,3,1,1)));
        Data.getData().setCurveList(curveList);
        assertEquals(curveList, Data.getData().getCurveList());
    }

    /**
     * Test de la fonction 1
     */
    @UiThreadTest
    public void testFunction1(){


    }

}
