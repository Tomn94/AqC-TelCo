package com.ioreef.aqctelco.dialogs;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.test.ActivityInstrumentationTestCase2;

import com.ioreef.aqctelco.LightActivity;
import com.ioreef.aqctelco.R;
import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.telco.Data;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


// TODO: Passer l'écran de connexion
// TODO: Récupérer la dialog

/**
 * @version 1.0
 * @date 05/05/2016
 * @author Antoine BRETECHER, Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class DialogLightEditTest extends ActivityInstrumentationTestCase2<LightActivity> {

    private DialogLightEdit dialog;
    private Ray dtRay;

    private LightActivity activity;

    private Instrumentation instrumentation;

    public DialogLightEditTest() {
        super(LightActivity.class);
    }


    /**
     * @brief Crée le produit nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    @UiThreadTest
    public void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        this.instrumentation = this.getInstrumentation();

        activity = getActivity();

        List<Curve> curveList = new ArrayList<>();
        curveList.add(new Curve(300, new ArrayList<Integer>()));
        curveList.add(new Curve(301, new ArrayList<Integer>()));
        curveList.add(new Curve(302, new ArrayList<Integer>()));
        Data.getData().setCurveList(curveList);

        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                new Runnable() {
                    @Override
                    public void run() {
                        dtRay = new Ray(1, 2, 300, true, 1, 1, 1, false);
                        dialog = new DialogLightEdit(getActivity(), dtRay);
                    }
                });
    }

    /**
     * Test création de l'activité
     */
    public void testActivityExists() {
        assertNotNull(activity);
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
    @UiThreadTest
    public void testPreconditions() {
        assertNotNull(dtRay);
        assertNotNull(dialog);
    }

    @UiThreadTest
    public void testCheckboxesAndCurveLaunch() {
        /* Test bonne configuration initiale */
        assertEquals(dialog.isActivatedChecked(),   dtRay.isActivated());
        assertEquals(dialog.isInvertedChecked(),    dtRay.isInverted());
        assertEquals(dialog.getAssociatedCurveID(), dtRay.getIdCurve());
    }

    @UiThreadTest
    public void testCheckboxesAndCurveEdited() {
        testCheckboxesAndCurveLaunch();

        /* Changement d'état */
        onView(withId(R.id.checkActivationLight))
                .inRoot(isDialog())
                .perform(click());
        onView(withId(R.id.checkInvert))
                .inRoot(isDialog())
                .perform(click());
        onView(withId(R.id.curveList))
                .inRoot(isDialog())
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(android.R.id.button1)).perform(click());

        /* Test nouvelle configuration */
        assertTrue(dialog.isActivatedChecked()   != dtRay.isActivated());
        assertTrue(dialog.isInvertedChecked()    != dtRay.isInverted());
        assertTrue(dialog.getAssociatedCurveID() != dtRay.getIdCurve());
        assertEquals(dialog.getAssociatedCurveID(), Data.getData().getCurveList().get(0).getId());
    }

}
