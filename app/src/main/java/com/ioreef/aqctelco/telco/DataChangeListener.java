package com.ioreef.aqctelco.telco;

/**
 * Interface définissant les méthodes appelées à chaque changement de modèle dans la classe Data
 *
 * @version 1.0
 * @date 09/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public interface DataChangeListener {

    /**
     * Appelée lorsqu'une donnée modèle dans Data a changée
     * Cela nécessitera habituellement de recharger la vue
     */
    void onDataChanged();

}
