package com.ioreef.aqctelco.slidingtab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ioreef.aqctelco.tabs.SummaryFragment;
import com.ioreef.aqctelco.tabs.SwitchersFragment;

/**
 * Adapter pour une activity à fragments-onglets
 *
 * @version 1.0
 * @date 03/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence titles[]; /**< Titres des onglets donnés à la création du ViewPagerAdapter */

    /**
     * Création de l'adapter grâce aux informations
     * @param fm Gestionnaire des onglets
     * @param mTitles Titre des onglets à afficher
     */
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[]) {
        super(fm);
        this.titles = mTitles;
    }

    //This method return the fragment for the every position in the View Pager

    /**
     * Retourne le bon fragment-onglet à la position donnée dans la vue principale
     * @param position Position demandée
     * @return Fragment retourné
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SummaryFragment();
            case 1:
                return new SwitchersFragment();

        }
        return null;
    }

    /**
     * Retourne le bon titre pour le fragment-onglet à la position donnée dans la vue principale
     * @param position Position demandée
     * @return Titre de fragment retourné
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    /**
     * Retourne le nombre d'onglets pour la vue principale
     * @return le nombre d'onglets
     */
    @Override
    public int getCount() {
        return titles.length;
    }
}
