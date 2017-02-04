package com.ioreef.aqctelco.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ioreef.aqctelco.tabs.SummaryUpdater;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * La classe Connection est un singleton afin qu'elle ne soit instanciée qu'une fois.
 * On y stocke le socket, créé et bound dans l'asynctask "Connection Task"
 *
 * @version 1.0
 * @date 02/05/2016
 * @author Rémy SALIM
 * @copyright BSD 3-Clause
 */
public class Connection {
    private static Connection instance = null;  /**< Instance de Connection */
    //TODO: Flux dans la classe Connection --> abstraire la connexion TCP
    //TODO: Encapsuler les méthodes, faire une méthode close(), read(), write(), open();
    //TODO: Asynctask --> inner
    private Socket socket;                      /**< Socket de communication TCP */
    public int SERVER_PORT = 12345;             /**< Port du serveur de Supervision */
    private ConnectionTask connectionTask;      /**< Tâche d'initialisation du socket avec Supervision */
    private SharedPreferences savedIP;          /**< Dernière IP valide sauvegardée (vide par défaut) */
    private String ip;                          /**< IP de connexion */
    private Activity context;                   /**< Contexte de l'application */

    private Boolean connectionState = false;    /**< État de connexion actuel */

    private Receiver receiver;                  /**< Thread gérant la réception des données */

    private Timer timer;                        /**< Mise à jour périodique des informations affichées */
    private SummaryUpdater summaryUpdater;      /**< Classe de rafraîchissement de l'écran Summary */

    public Connection() {}

    /**
     * Création du singleton de connexion
     * @param context
     */
    private Connection(Activity context) {
        this.context = context;
        if (context != null) {
            try {
                this.ip = loadIp();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        if (socket == null || !socket.isConnected())
            connectionTask = new ConnectionTask(this, getContext());
        }
    }

    /**
     * Retourne l'instance de Connection
     * @param context
     * @return l'unique instance de Connection
     */
    public static Connection getConnection(Activity context) {
        if (instance == null && context != null) {
            Class laClasse = Connection.class;
            synchronized (laClasse) {
                instance = new Connection(context);
            }
        }
        instance.context = context;
        return instance;
    }

    /**
     * Réinstancie le singleton Connection
     * @param context
     * @return une instance de Connection
     */
    public static Connection setUpNewConnection(Activity context) {
        instance = new Connection(context);
        instance.context = context;
        return instance;
    }



    /**
     * Lancement du processus de connexion à Supervision
     * @param ip L'IP à laquelle se connecter
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void connectTelCo(String ip) throws ExecutionException, InterruptedException {
        if (socket == null || !socket.isConnected()) {
            Log.i("CONNECTION","connectTelCo : Connecting");

            connectionTask.execute(ip);
            this.ip = ip;
        } else
            Log.i("CONNECTION","connectTelCo : Already connected");
    }

    /**
     * Sauvegarde dans le registre de l'application la dernière adresse IP utilisée pour la connexion
     * @param ip L'IP à sauvegarder
     * @throws IOException
     */
    public void saveIp(String ip) throws IOException {
        if (context != null) {
            savedIP = context.getSharedPreferences("lastKnownIP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = savedIP.edit();
            editor.putString("lastKnownIP", ip);
            editor.apply();
        }
    }

    /**
     * Retourne l'adresse IP stockée en registre
     * @return L'IP sauvegardée en mémoire, sinon vide
     * @throws IOException
     */
    private String loadIp() throws IOException {
        if (context != null) {
            savedIP = context.getSharedPreferences("lastKnownIP", Context.MODE_PRIVATE);
            return savedIP.getString("lastKnownIP", "");
        }
        return null;
    }

    /**
     * Démarre ou arrête l'actualisation automatique des données de l'écran Bilan
     * @param refreshing Ordre d'arrêt/démarrage
     */
    public void refreshingSummary(Boolean refreshing) {
        if (refreshing) {
            summaryUpdater = new SummaryUpdater(Connection.getConnection(context));
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer("refreshTimer", true);
            timer.scheduleAtFixedRate(summaryUpdater,0,SummaryUpdater.REFRESH_RATE);
        } else {
            if (summaryUpdater != null)
                summaryUpdater.cancel();
        }
    }

    /**
     * Vérifie si ip est une adresse IPv4 valide
     * La validation se fait par une expression régulière (pattern)
     * @param ip : IPv4 à tester
     * @return true si l'IP est valide, false sinon
     */
    public Boolean ipIsValid(String ip) {
        Pattern p = Pattern
                .compile("^((25[0-5])|(2[0-4][0-9])|(1[0-9]{2})|([1-9][0-9])|([1-9]))\\.(((25[0-5])|(2[0-4][0-9])|(1[0-9]{2})|([1-9][0-9])|([0-9]))\\.){2}((25[0-5])|(2[0-4][0-9])|(1[0-9]{2})|([1-9][0-9])|([0-9]))$");
        Matcher m = p.matcher(ip);
        return m.matches();
    }

    public void close() throws IOException{
        this.getSocket().getInputStream().close();
        this.getSocket().getOutputStream().close();
        this.getSocket().close();
    }


    /* Accesseurs */
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public Boolean getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(Boolean connectionState) {
        this.connectionState = connectionState;
    }
    public AsyncTask getConnectionTask() {
        return connectionTask;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public SummaryUpdater getSummaryUpdater() {
        return summaryUpdater;
    }
}

