package com.ioreef.aqctelco;

import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @version 1.0
 * @date 29/04/2016
 * @author Antoine BRETECHER, Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    /**
     * Test création de l'activité
     */
    public void testActivityExists() {
        MainActivity activity = getActivity();
        assertNotNull(activity);
    }

    /**
     * Test du bon nombre d'onglets avec le bon nom
     */
    public void testTabs() {
        MainActivity activity = getActivity();

        try {
            Field field = MainActivity.class.getDeclaredField("titles");
            field.setAccessible(true);
            List<CharSequence> titles = (List<CharSequence>) field.get(activity);
            assertNotNull(titles);

            /* Vérification */
            assertEquals(titles.size(), 2);
            assertEquals(titles.get(0), getActivity().getString(R.string.tabSummary));
            assertEquals(titles.get(1), getActivity().getString(R.string.tabRelay));
        } catch (NoSuchFieldException e) {
            fail("NoSuchFieldException fail");
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException fail");
        }
    }

    /**
     * Test changement d'onglet et bouton Retour
     */
    @UiThreadTest
    public void testChangeTabAndBack() {
        MainActivity activity = getActivity();
        int destTab = 1;
        ViewPager pager = (ViewPager) activity.findViewById(R.id.main_sliding_pager);
        assertNotNull(pager);

        /* Changement d'onglet */
        pager.setCurrentItem(destTab, false);
        assertEquals(pager.getCurrentItem(), destTab);

        /* Retour */
        activity.onBackPressed();
        assertEquals(pager.getCurrentItem(), 0);
    }

}
