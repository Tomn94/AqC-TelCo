package com.ioreef.aqctelco.tabs;

import android.app.Instrumentation;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.ioreef.aqctelco.MainActivity;
import com.ioreef.aqctelco.R;
import android.support.test.InstrumentationRegistry;
import com.ioreef.aqctelco.tank.Switcher;
import com.ioreef.aqctelco.telco.Data;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @version 1.0
 * @date 29/04/2016
 * @author Antoine BRETECHER, Thomas NAUDET
 * @copyright BSD 3-Clause
 */

/*
public class MainActivityForTest extends MainActivity {
    public SwitchersFragment relaysFragmentTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        relaysFragmentTest = new SwitchersFragment();
        fragmentTransaction.add(R.id.mainFragmentContainer, relaysFragmentTest);
        fragmentTransaction.commit();
    }
}
*/

public class SwitchersFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    /* Temporisation pour l'enchaînement des lancements de l'activité testée. */
    private static final int TMP_INTER_ACTIVITY = 1000;
    private static final int FRAGMENT_RELAY_POSITION = 1;

    /* Activité testée. */
    private MainActivity activity;

    /* Instrumentation des tests. */
    private Instrumentation instrumentation;

//    private SwitchersFragment switchersFragment;

    // TODO: Passer l'écran de connexion
    // TODO: Vérifier les données envoyées et les delays

    /**
     * Constructeur (lié à l'activité associée à l'application testée).
     */
    public SwitchersFragmentTest() {
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
    @UiThreadTest
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
		/* lancement de l'activité testée */
        this.activity = this.getActivity();

		/* instrumentation du test */
        this.instrumentation = this.getInstrumentation();

        /* Affichage du fragment */
//        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
//        switchersFragment = (SwitchersFragment) fragments.get(fragments.size() - 1);

        final int destTab = FRAGMENT_RELAY_POSITION;
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                new Runnable() {
                    @Override
                    public void run() {
                        ViewPager pager = (ViewPager) activity.findViewById(R.id.main_sliding_pager);
                        assertNotNull(pager);

                        /* Changement d'onglet */
                        pager.setCurrentItem(destTab, false);
                        assertEquals(pager.getCurrentItem(), destTab);

                        /* Ajout de DT */
                        List<Switcher> switcherList = new ArrayList<>();
                        switcherList.add(new Switcher(0, 0, 0, 0, true, "Brasseur"));
                        switcherList.add(new Switcher(1, 2, 2, 1, false, "Relais 2"));
                        Data.getData().setSwitcherList(switcherList);
                        assertEquals(switcherList, Data.getData().getSwitcherList());
                    }
                });

    }

    @UiThreadTest
    public void testSwitcherTap() {
        /* Tap */
        onView(withId(R.id.relayList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @UiThreadTest
    public void testSwitcherReTap() {
        onView(withId(R.id.relayList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.relayList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @UiThreadTest
    public void testSwitcherLongTap() {
        int testIndex = 0;

        /* Long press */
        onView(withId(R.id.relayList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(testIndex, longClick()));

        /* Dialog visible */
        String switcherName = Data.getData().getSwitcherList().get(testIndex).getName();
        onView(withText(activity.getString(R.string.editNameTitle, switcherName)))
                .check(matches(isDisplayed()));

        /* Valider */
        onView(withId(android.R.id.button1)).perform(click());
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

}
