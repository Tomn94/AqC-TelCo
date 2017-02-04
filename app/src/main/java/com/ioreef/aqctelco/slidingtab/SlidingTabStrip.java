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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Vue indicatrice d'onglet actuel
 * Affiche un léger soulignement sous l'onglet en cours
 * et le déplace selon le scroll entre onglets
 *
 * @version 1.0
 * @date 03/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class SlidingTabStrip extends LinearLayout {

    private static final int  DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 0; /**< Bord commun à chaque titre d'onglet */
    private static final byte DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26; /**< Couleur commune à chaque bord de titre d'onglet */
    private static final int  SELECTED_INDICATOR_THICKNESS_DIPS = 7;    /**< Épaisseur de l'indicateur de l'onglet actuel */
    private static final int  DEFAULT_SELECTED_INDICATOR_COLOR = 0xFF00baff; /**< Couleur de l'indicateur de l'onglet actuel */

    /**
     * Constantes d'affichage calculées des titres d'onglet
     */
    private final int   mBottomBorderThickness;
    private final Paint mBottomBorderPaint;
    private final int   mSelectedIndicatorThickness;
    private final Paint mSelectedIndicatorPaint;
    private int   mSelectedPosition;
    private float mSelectionOffset;
    private SlidingTabLayout.TabColorizer mCustomTabColorizer;
    private final SimpleTabColorizer      mDefaultTabColorizer;

    /* Constructeurs */
    SlidingTabStrip(Context context) {
        this(context, null);
    }

    /**
     * Création d'un titre d'onglet
     * @param context le contexte de l'activité
     * @param attrs éventuels attributs
     */
    SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        /* Configuration de l'affichage */
        setWillNotDraw(false); // optimisations, on ne dessine pas la vue tout de suite

        final float density = getResources().getDisplayMetrics().density; // densité de pixels à l'écran

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForegroundColor = outValue.data; // titre de la couleur du thème Android

        int defaultBottomBorderColor = setColorAlpha(themeForegroundColor,
                DEFAULT_BOTTOM_BORDER_COLOR_ALPHA);

        mDefaultTabColorizer = new SimpleTabColorizer();
        mDefaultTabColorizer.setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR);

        mBottomBorderThickness = (int) (DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density);
        mBottomBorderPaint = new Paint();
        mBottomBorderPaint.setColor(defaultBottomBorderColor);

        mSelectedIndicatorThickness = (int) (SELECTED_INDICATOR_THICKNESS_DIPS * density);
        mSelectedIndicatorPaint = new Paint();
    }


    /* Actions */

    /**
     * Modifier la couleur d'un onglet
     * @param customTabColorizer grâce aux paramètres de couleur
     */
    void setCustomTabColorizer(SlidingTabLayout.TabColorizer customTabColorizer) {
        mCustomTabColorizer = customTabColorizer;
        invalidate();   // on redessine la vue
    }

    /**
     * Modifier la couelur d'un onglet sélectionné
     * @param colors Couleur pour chaque onglet
     */
    void setSelectedIndicatorColors(int... colors) {
        mCustomTabColorizer = null; // on retire une précédente personnalisation
        mDefaultTabColorizer.setIndicatorColors(colors);
        invalidate();   // on redessine la vue
    }

    /**
     * Appelée lors du changement d'onglet
     * @param position  Position actuelle
     * @param positionOffset Position de la page par rapport à son origine
     */
    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset  = positionOffset;
        invalidate();   // on redessine la vue
    }

    /**
     * Dessin de la vue onglets
     * @param canvas dans ce canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        final int height     = getHeight();
        final int childCount = getChildCount();
        final SlidingTabLayout.TabColorizer tabColorizer = (mCustomTabColorizer != null)
                                                            ? mCustomTabColorizer
                                                            : mDefaultTabColorizer;

        /* Petite ligne colorée sous l'onglet sélectionné */
        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            int left = selectedTitle.getLeft();
            int right = selectedTitle.getRight();
            int color = tabColorizer.getIndicatorColor(mSelectedPosition);

            /* Si nous ne dépassons pas une extrémité */
            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                int nextColor = tabColorizer.getIndicatorColor(mSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, mSelectionOffset);
                }

                // Séparation entre les onglets
                View nextTitle = getChildAt(mSelectedPosition + 1);
                left = (int) (mSelectionOffset * nextTitle.getLeft() +
                        (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * nextTitle.getRight() +
                        (1.0f - mSelectionOffset) * right);
            }

            mSelectedIndicatorPaint.setColor(color);

            canvas.drawRect(left, height - mSelectedIndicatorThickness, right,
                    height, mSelectedIndicatorPaint);
        }

        // Thin underline along the entire bottom edge
        canvas.drawRect(0, height - mBottomBorderThickness, getWidth(), height, mBottomBorderPaint);
    }

    /**
     * Ajout d'un attribut d'opacité à une couleur existante
     */
    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Mélange de couleurs à un ratio donné
     * Utile lors du passage en fondu d'un onglet à un autre ayant une couleur différente
     *
     * @param color1 1ère couleur à mélanger
     * @param color2 2e couleur à mélanger
     * @param ratio Ratio d'importance lors du mélange
     * @return Nouvelle couleur mélangée
     */
    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    /**
     * Informations de colorisation d'un onglet
     * Alternative à setSelectedIndicatorColors
     */
    private static class SimpleTabColorizer implements SlidingTabLayout.TabColorizer {
        private int[] mIndicatorColors;

        /**
         * Permet de récupérer la couleur pour un onglet donné
         * @param position Position de l'onglet
         * @return couleur de l'onglet
         */
        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }
    }
}
