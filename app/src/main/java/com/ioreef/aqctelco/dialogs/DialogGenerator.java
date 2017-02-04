package com.ioreef.aqctelco.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ioreef.aqctelco.LightActivity;
import com.ioreef.aqctelco.R;
import com.ioreef.aqctelco.network.Connection;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Cette classe permet d'afficher différents messages de dialogue modaux à l'utilisateur
 *
 * @version 1.0
 * @date 10/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class DialogGenerator {

    /** Types de fenêtres de dialog pouvant être créés */
    public enum DialogType {
        CONNECTION,
        TIMEOUT,
        ERROR,
        PROGRESS,
        SIMULATION,
        WRONG_RESPONSE_SWITCHER,
        WRONG_RESPONSE_RAY
    }

    /* Attributs */
    private Activity context;
    private Connection co;

    /* Constructeur */
    public DialogGenerator(Activity context) {
        this.context = context;
    }

    /**
     * Générateur de dialog. On passe en paramètre le type de la dialog que l'on souhaite créer
     * Un "builder" pour chaque dialog permet de définir les différents éléments qui la constitue
     * @param dialog : DialogType de la dialog à créer
     * @return MaterialDialog
     */
    public MaterialDialog generate(DialogType dialog) {
        co = Connection.getConnection(context);
        MaterialDialog result;

        MaterialDialog.Builder timeoutBuilder = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.timeoutTitle))
                .content(context.getResources().getString(R.string.timeoutContent))
                .positiveText(context.getResources().getString(R.string.timeoutValidate))
                .negativeText(context.getResources().getString(R.string.timeoutQuit))
                .alwaysCallInputCallback() /**< Vérification de l'input à chaque caractère */
                .cancelable(false) /**< On empêche l'utilisateur d'intéragir avec l'arrière-plan */
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        retry();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        quit();
                    }
                });

        MaterialDialog.Builder connectionBuilder = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.connectionTitle))
                .positiveText(context.getResources().getString(R.string.connectionValidate))
                .negativeText(context.getResources().getString(R.string.connectionQuit))
                .inputType(InputType.TYPE_CLASS_PHONE) /**< On restreint le clavier aux chiffres et symboles*/
                .alwaysCallInputCallback() /**< Vérification de l'input à chaque caractère */
                .cancelable(false) /**< On empêche l'utilisateur d'intéragir avec l'arrière-plan */
                .input(context.getResources().getString(R.string.connectionHint), co.getIp(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        /**
                         * Quand l'IP est invalide, on désactive le bouton de connexion
                         */
                        if (co.ipIsValid(input.toString()) == false)
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            co.setIp(input.toString());
                        }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("DIALOG","Clicked on positive");
                        Log.d("DIALOG","IP entered : " + co.getIp());
                        try {
                            co.saveIp(co.getIp());
                        } catch(IOException ioe) {
                            Log.d("DIALOG","Error saving pref");
                        }
                        try {
                            co.connectTelCo(co.getIp());
                        } catch (ExecutionException ee) {}
                        catch (InterruptedException ie) {}
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        quit();
                    }
                });

        MaterialDialog.Builder errorBuilder = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.errorTitle))
                .content(context.getResources().getString(R.string.errorContent))
                .positiveText(context.getResources().getString(R.string.errorValidate))
                .negativeText(context.getResources().getString(R.string.errorQuit))
                .alwaysCallInputCallback() /**< Vérification de l'input à chaque caractère */
                .cancelable(false) /**< On empêche l'utilisateur d'intéragir avec l'arrière-plan */
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        retry();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                       quit();
                    }
                });

        MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.connectionTitle))
                .content(context.getResources().getString(R.string.pleaseWait))
                .progress(true, 0)
                .cancelable(false); /**< On empêche l'utilisateur d'intéragir avec l'arrière-plan */

        final LightActivity lightActivity = new LightActivity();
        MaterialDialog.Builder simulationBuilder = new MaterialDialog.Builder(context)
                .title(R.string.titleSimulation)
                .content(R.string.pleaseWait)
                .progress(true, 0)
                .negativeText(R.string.simulationCancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        lightActivity.stopSimulation();
                    }
                })
                .cancelable(false);

        MaterialDialog.Builder wrongResponseSwitcherBuilder = new MaterialDialog.Builder(context)
                .title(R.string.errorTitle)
                .content(R.string.wrongResponseSwitcher)
                .positiveText(R.string.dialogOK);

        MaterialDialog.Builder wrongResponseRayBuilder = new MaterialDialog.Builder(context)
                .title(R.string.errorTitle)
                .content(R.string.wrongResponseRay)
                .positiveText(R.string.dialogOK);



        switch (dialog) {
            case CONNECTION:
                result = connectionBuilder.build();
                break;
            case TIMEOUT:
                result = timeoutBuilder.build();
                break;
            case PROGRESS:
                result = progressBuilder.build();
                break;
            case ERROR:
                result = errorBuilder.build();
                break;
            case SIMULATION:
                result = simulationBuilder.build();
                break;
            case WRONG_RESPONSE_SWITCHER:
                result = wrongResponseSwitcherBuilder.build();
                break;
            case WRONG_RESPONSE_RAY:
                result = wrongResponseRayBuilder.build();
                break;

            default:
                result = timeoutBuilder.build();
                break;
        }
        return result;
    }

    /**
     * Permet de quitter l'application, s'assure de la fermeture des sockets et des flux.
     */
    protected void quit() {
        Connection co = Connection.getConnection(context);
        Log.d("DIALOG","Quitting application");
        if(co.getConnectionTask() != null && co.getConnectionTask().getStatus() != AsyncTask.Status.FINISHED) {
            co.getConnectionTask().cancel(true);
            if (co.getSocket() != null) {
                try {
                    co.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("QUIT", "Error closing socket");
                }
            }
        }
        /**
         * Fermeture du clavier avant de terminer l'activité
         */
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        context.moveTaskToBack(true);
        context.finish();
    }

    /**
     * Recommencer l'opération de connexion
     * Action pouvant être appelée par une Dialog créée
     */
    protected void retry() {
        /**
         * On redémarre l'application
         */
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage( context.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
}
