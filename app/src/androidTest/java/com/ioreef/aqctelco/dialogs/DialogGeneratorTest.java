package com.ioreef.aqctelco.dialogs;

import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ioreef.aqctelco.MainActivity;
import com.ioreef.aqctelco.R;
import com.robotium.solo.Solo;

/**
 * @version 1.0
 * @date 20/05/2016
 * @author Antoine BRETECHER, Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class DialogGeneratorTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private DialogGenerator dialogGenerator;
    private MainActivity mainActivity;
    public DialogGeneratorTest() {
        super(MainActivity.class);
    }
    /**
     * Test création de l'activité
     */
    public void testActivityExists() {
        mainActivity = getActivity();
        assertNotNull(mainActivity);
    }

    /**
     * @brief Crée le produit nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     */
    public void setUp() throws Exception{
        dialogGenerator = new DialogGenerator(this.getActivity());
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
    public void testPreconditions(){}

    /**
     * Teste la création de la dialog de connexion, de son affichage et de sa disparition
     */
    public void testDialogConnection(){
        if (Looper.myLooper() == null) Looper.prepare();
        MaterialDialog dialogExpected = dialogGenerator.generate(DialogGenerator.DialogType.CONNECTION);
        String dialogTitle = getInstrumentation().getTargetContext().getResources().getString(R.string.connectionTitle);

        assertNotNull(dialogExpected); /**< Génération de la dialog */
        Solo solo = new Solo(getInstrumentation(), getActivity());
        getInstrumentation().waitForIdleSync();
        dialogExpected.show();
//        assertTrue("Could not find the dialog", solo.searchText(dialogTitle)); /**< Dialog affichée sur l'UI */
        assertTrue("Dialog not showing", dialogExpected.isShowing()); /**< Dialog est affichée */
        dialogExpected.dismiss();
        assertFalse("Dialog not dismissed", dialogExpected.isShowing()); /** Dialog est rejetée */
    }

    /**
     * Teste la création de la dialog de simulation, de son affichage et de sa disparition
     */
    public void testDialogSimulation(){
        if (Looper.myLooper() == null) Looper.prepare();
        MaterialDialog dialogExpected = dialogGenerator.generate(DialogGenerator.DialogType.SIMULATION);
        String dialogTitle = getInstrumentation().getTargetContext().getResources().getString(R.string.titleSimulation);

        assertNotNull(dialogExpected); /**< Génération de la dialog */
        Solo solo = new Solo(getInstrumentation(), getActivity());
        getInstrumentation().waitForIdleSync();
        dialogExpected.show();
//        assertTrue("Could not find the dialog", solo.searchText(dialogTitle)); /**< Dialog affichée sur l'UI */
        assertTrue("Dialog not showing", dialogExpected.isShowing()); /**< Dialog est affichée */
        dialogExpected.dismiss();
        assertFalse("Dialog not dismissed", dialogExpected.isShowing()); /** Dialog est rejetée */
    }

    /**
     * Teste la création de la dialog d'erreur, de son affichage et de sa disparition
     */
    public void testDialogError(){
        if (Looper.myLooper() == null) Looper.prepare();
        MaterialDialog dialogExpected = dialogGenerator.generate(DialogGenerator.DialogType.ERROR);
        String dialogTitle = getInstrumentation().getTargetContext().getResources().getString(R.string.errorTitle);

        assertNotNull(dialogExpected); /**< Génération de la dialog */
        Solo solo = new Solo(getInstrumentation(), getActivity());
        getInstrumentation().waitForIdleSync();
        dialogExpected.show();
//        assertTrue("Could not find the dialog", solo.searchText(dialogTitle)); /**< Dialog affichée sur l'UI */
        assertTrue("Dialog not showing", dialogExpected.isShowing()); /**< Dialog est affichée */
        dialogExpected.dismiss();
        assertFalse("Dialog not dismissed", dialogExpected.isShowing()); /** Dialog est rejetée */
    }

    /**
     * Teste la création de la dialog d'attente (tâche en cours), de son affichage et de sa disparition
     */
    public void testDialogProgress(){
        if (Looper.myLooper() == null) Looper.prepare();
        MaterialDialog dialogExpected = dialogGenerator.generate(DialogGenerator.DialogType.PROGRESS);
        String dialogTitle = getInstrumentation().getTargetContext().getResources().getString(R.string.pleaseWait);

        assertNotNull(dialogExpected); /**< Génération de la dialog */
        Solo solo = new Solo(getInstrumentation(), getActivity());
        getInstrumentation().waitForIdleSync();
        dialogExpected.show();
//        assertTrue("Could not find the dialog", solo.searchText(dialogTitle)); /**< Dialog affichée sur l'UI */
        assertTrue("Dialog not showing", dialogExpected.isShowing()); /**< Dialog est affichée */
        dialogExpected.dismiss();
        assertFalse("Dialog not dismissed", dialogExpected.isShowing()); /** Dialog est rejetée */
    }

    /**
     * Teste la création de la dialog de timeout, de son affichage et de sa disparition
     */
    public void testDialogTimeout(){
        if (Looper.myLooper() == null) Looper.prepare();
        MaterialDialog dialogExpected = dialogGenerator.generate(DialogGenerator.DialogType.TIMEOUT);
        String dialogTitle = getInstrumentation().getTargetContext().getResources().getString(R.string.timeoutTitle);

        assertNotNull(dialogExpected); /**< Génération de la dialog */
        Solo solo = new Solo(getInstrumentation(), getActivity());
        getInstrumentation().waitForIdleSync();
        dialogExpected.show();
//        assertTrue("Could not find the dialog", solo.searchText(dialogTitle)); /**< Dialog affichée sur l'UI */
        assertTrue("Dialog not showing", dialogExpected.isShowing()); /**< Dialog est affichée */
        dialogExpected.dismiss();
        assertFalse("Dialog not dismissed", dialogExpected.isShowing()); /** Dialog est rejetée */
    }

    public void testDisableValidate() {
        if (Looper.myLooper() == null) Looper.prepare();
//        mainActivity.getCurrentDialog().dismiss();
        MaterialDialog connectionDialog = dialogGenerator.generate(DialogGenerator.DialogType.CONNECTION);
        Solo solo = new Solo(getInstrumentation(), getActivity());

        connectionDialog.show();
        solo.clearEditText(connectionDialog.getInputEditText());
        solo.enterText(connectionDialog.getInputEditText(),"127.0.11");
        Boolean result = connectionDialog.getActionButton(DialogAction.POSITIVE).isEnabled();
        assertTrue("Validation is enabled (it shouldn't be)",result);
    }

}
