package com.ioreef.aqctelco.telco;

/**
 * Interface définissant les méthodes appelés lors d'une sauvegarde des données du modèle LED
 *
 * @version 1.0
 * @date 10/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public interface DataSaveListener {

    /**
     * Appelée lorsque les données des LED ont été sauvegardées
     * Cela nécessitera habituellement de fermer la vue
     */
    void onDataSaved();

}
