package com.ioreef.aqctelco.network;

import android.test.InstrumentationTestCase;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @version 1.0
 * @date 20/05/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class DispatcherUITest extends InstrumentationTestCase {

    private DispatcherUI dispatcherUI;
    /**
     * @brief Crée le produit nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void setUp() throws Exception{

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
    public void testPreconditions(){

    }

    /**
     * Teste du renvoi d'une ArrayList à partir d'un JSONObject
     */
    public void testLoadList(){
        String methodeTestee = "loadList";
        JSONArray fromJSONArray;

        Class<?>[] parametresMethodeTestee = {JSONArray.class};

        Method methode;

        try {
            /* Accès à la méthode. */
            methode = DispatcherUI.class.getDeclaredMethod(methodeTestee,parametresMethodeTestee);
            methode.setAccessible(true);
            ArrayList result = (ArrayList)methode.invoke(dispatcherUI);
        } catch(Exception e) {}
    }

}
