package com.ioreef.aqctelco.telco;

import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.Ray;
import com.ioreef.aqctelco.tank.Sensor;
import com.ioreef.aqctelco.tank.Switcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @brief Création d'un bouchon de Data
 *
 * @version 1.0
 * @date 22/05/2016
 * @author Antoine BRETECHER
 * @copyright BSD 3-Clause
 */
public class DataStub extends Data {

    public DataStub(){

        super();

        List<Switcher> switcherList = new ArrayList<>();
        List<Sensor> sensorList = new ArrayList<>();
        List<Curve> curveList = new ArrayList<>();
        List<Ray>rayList = new ArrayList<>();

        /* Création de différents Switcher */

        switcherList.add(new Switcher(0, 0, 0, 0, true, "Brasseur"));
        switcherList.add(new Switcher(1, 0, 0, 0, false, "Remplisseur"));
        switcherList.add(new Switcher(3, 0, 0, 0, true, "Éclusier"));
        switcherList.add(new Switcher(2, 0, 0, 0, true, "Écumeur"));
        switcherList.add(new Switcher(4, 0, 0, 0, true, "Filtre à phosphate"));
        switcherList.add(new Switcher(5, 0, 0, 0, true, "Dénitrateur"));
        switcherList.add(new Switcher(6, 0, 0, 0, true, "Réacteur à calcaire"));
        switcherList.add(new Switcher(7, 0, 0, 0, true, "Osmoseur"));
        for (int i = 8 ; i < 14 ; i++) {
            switcherList.add(new Switcher(i, 0, 0, 0, true, "Relai " + Integer.toString(i + 1)));
        }
        switcherList.add(new Switcher(14, 0, 0, 0, false, "Relai 15"));
        switcherList.add(new Switcher(15, 0, 0, 0, false, "Relai 16"));

        this.setSwitcherList(switcherList);

        /* Création de différents Sensor */

        sensorList.add(new Sensor(1, 0, 0, 0, Arrays.asList(25.0f, 25.2f, 25.3f, 25.4f, 25.4f,25.3f,25.3f,25.2f,25.1f,25.0f,24.8f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f), "Température bassin"));
        sensorList.add(new Sensor(2, 0, 0, 0, Arrays.asList(25.1f, 25.3f, 25.4f, 25.4f, 25.5f,25.5f,25.4f,25.3f,25.2f,25.1f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.1f,25.1f,25.1f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.1f,25.1f,25.0f,25.0f,24.9f,24.8f,24.8f,24.9f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f,25.0f), "Température décante"));
        sensorList.add(new Sensor(3, 0, 0, 0, Arrays.asList(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.48f,0.45f,0.4f,0.4f,0.4f,0.45f,0.47f,0.5f,0.5f,0.5f,0.55f,0.57f,0.6f,0.6f,0.6f,0.57f,0.55f,0.5f), "Consommation"));
        sensorList.add(new Sensor(4, 0, 0, 0, Collections.singletonList(1.2f), "Niveau d'eau douce"));
        sensorList.add(new Sensor(5, 0, 0, 0, Collections.singletonList(1.0f), "Compte-bulles"));
        sensorList.add(new Sensor(6, 0, 0, 0, Collections.singletonList(1300.0f), "Quantum mètre"));

        /* Création de différentes Curve */

        curveList.add(new Curve(1, Arrays.asList(1,1,1,3,5,7,10,10,10,10,10,10,10,10,10,10,10,15,20,30,40,50,60,70,80,90,95,100,100,95,90,80,70,60,50,40,30,20,10,10,10,10,10,7,5,3,1,1)));
        curveList.add(new Curve(2, Arrays.asList(1,1,1,3,5,7,10,12,15,17,15,12,10,10,10,10,10,15,20,30,40,50,60,70,80,90,95,100,100,95,90,80,70,60,50,40,30,20,10,10,10,10,10,7,5,3,1,1)));
        curveList.add(new Curve(2, Arrays.asList(1,1,1,3,5,7,10,12,15,17,15,12,10,10,10,10,10,15,20,30,40,50,60,70,80,90,95,100,100,95,90,80,70,60,50,40,30,20,10,10,10,10,10,7,5,3,1,1)));

        /* Création de différents Ray */
        Boolean state=false, inverted=false;
        for (int i=0; i<16; i++) {
            rayList.add(new Ray(i, i, 300, state, 0, 0, 0, inverted));
            state    = !state;
            if(i%2 == 0) {
                inverted = !inverted;
            }
        }



        /* Ajout des listes de Switcher, Sensor et Curve au bouchon de Data */

        this.setSwitcherList(switcherList);
        this.setSensorList(sensorList);
        this.setCurveList(curveList);
        this.setRayList(rayList);

    }
}
