/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ioreef.aqctelco.slidingtab;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioreef.aqctelco.R;

/**
 * Layout de vue à onglets
 * Comprend une barre affichant les titres d'onglets au-dessus
 * de la vue de l'onglet actuel
 * Réagit au scroll effectué par l'utilisateur
 *
 * @version 1.0
 * @date 03/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class SlidingTabLayout extends HorizontalScrollView {

    /**
     * Interface permettant une personnalisation complexe de
     * la couleur des onglets grâce à setCustomTabColorizer
     */
    public interface TabColorizer {
        /**
         * Retourne la couleur de l'indicateur à la position donnée lors de la sélection
         * @param position
         * @return
         */
        int getIndicatorColor(int position);
    }


    /* Attributs d'affichage */
    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 14;

    private int mTitleOffset;

    private int mTabViewLayoutId;
    private int mTabViewTextViewId;
    private boolean mDistributeEvenly;

    /* Attributs dynamiques */
    private ViewPager mViewPager;
    private SparseArray<String> mContentDescriptions = new SparseArray<>();
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTabStrip mTabStrip;


    /* Constructeurs */
    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setHorizontalScrollBarEnabled(false); // aucune scrollbar dans la scrollview
        setFillViewport(true);                // la barre d'onglet remplit la vue

        /* Affichage */
        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }


    /**
     * Charge les paramètres de personnalisation de couleur d'onglets
     * Alternative à setSelectedIndicatorColors
     *
     * @param tabColorizer paramètres de personnalisation de couleur
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    /**
     * Spécifie si les titres d'onglets ont tous la même taille
     * @param distributeEvenly Activé ou non
     */
    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    /**
     * Choisit les couleurs indiquant l'onglet sélectionné (barre de soulignement)
     * @param colors Tableau circulaire des couleurs.
     *               Si 1 seule couleur donnée, elle sera utilisée pour tous les onglets
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Renseigne un listener appelé lors du changement de page
     * Doit être associé grâce à cette méthode pour être certain de la mise à jour lors du scroll
     * @param listener nouveau listener associé
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     * Création d'un layout personnalisé pour une vue d'onglet donné
     * @param layoutResId layout à "inflated"
     * @param textViewId ID du TextView dans la vue "inflated"
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId   = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    /**
     * Attribution du View Pager associé
     * Sans prise en compte de changement dans le nombre d'onglets et leur titre dans cet appel
     * @param viewPager ViewPager associé
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /**
     * Création d'une vue par défaut pour un titre d'onglet
     * Appelée si aucun onglet n'est personnalisé avec setCustomTabView
     * @param context Contexte de l'activité
     * @return Retourne le TextView, soit le titre de l'onglet personnalisé ici
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);
        textView.setBackgroundResource(outValue.resourceId);
        textView.setAllCaps(true);

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    /**
     * Remplissage de la zone affichant les titres d'onglet
     */
    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            tabTitleView.setText(adapter.getPageTitle(i));
            tabView.setOnClickListener(tabClickListener);
            String desc = mContentDescriptions.get(i, null);
            if (desc != null) {
                tabView.setContentDescription(desc);
            }

            mTabStrip.addView(tabView);
            if (i == mViewPager.getCurrentItem()) {
                tabView.setSelected(true);
            }

            tabTitleView.setTextColor(getResources().getColorStateList(R.color.selector));
        }
    }

    /**
     * Définit une description pour un onglet
     * @param i position de l'onglet
     * @param desc description
     */
    public void setContentDescription(int i, String desc) {
        mContentDescriptions.put(i, desc);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    /**
     * Appelée lors d'un tap sur un onglet pour afficher celui-ci
     * @param tabIndex Position de l'onglet
     * @param positionOffset Position en X de l'onglet dans la ScrollView horizontale
     */
    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        /* Pas de changement d'onglet si déjà aux extrêmités */
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                /* Forcer le bon offset si au milieu d'un scroll */
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0); // Scroll avec la fonction d'une ScrollView
        }
    }

    /**
     * Classe définissant les méthodes utiles à un listener sur la vue
     * Avertit du scroll, de son état et de la sélection d'une page
     */
    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = 0;
            if (selectedTitle != null) {
                extraOffset = (int) (positionOffset * selectedTitle.getWidth());
            }
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);
            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    /**
     * Listener d'un tap sur un onglet
     */
    private class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

}
