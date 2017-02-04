package com.ioreef.aqctelco;

import android.test.InstrumentationTestCase;

import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.telco.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * @version 1.0
 * @date 29/04/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class LightActivityTest extends InstrumentationTestCase {

    private List<Ray> rayList;  /**< Données de test 1 */


    /**
     * @brief Crée le produit nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void setUp() throws Exception{

        rayList = new ArrayList<>();
         /* Création de différents Ray */
        Boolean state=false, inverted=false;
        for (int i=0; i<16; i++) {
            rayList.add(new Ray(i, i, 300, state, 0, 0, 0, inverted));
            state    = !state;
            if(i%2 == 0) {
                inverted = !inverted;
            }
        }
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
        assertNotNull(rayList);
    }

    /**
     * Test ajout des données à TelCo
     */
    public void testAddRays() {
        Data.getData().setRayList(rayList);
        assertEquals(rayList, Data.getData().getRayList());
    }

    public void test2() {
/*
        final Object object = new Object(mock);

        EasyMock.expect(
                this.mock.function2()
        ).andReturn(
                null
        );

        EasyMock.replay(this.mock);*/

        //try {
//            object.function1();
        /*}
        catch (ExempleException e) {
			/* passage attendu dans ce bloc. */
        //}
    }
}
