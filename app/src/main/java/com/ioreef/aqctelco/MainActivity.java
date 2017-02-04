package com.ioreef.aqctelco;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ioreef.aqctelco.dialogs.DialogGenerator;
import com.ioreef.aqctelco.network.Connection;
import com.ioreef.aqctelco.slidingtab.SlidingTabLayout;
import com.ioreef.aqctelco.slidingtab.ViewPagerAdapter;
import com.ioreef.aqctelco.telco.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activité principale de TelCo
 * Lancement de l'interface avec onglets
 * Initialisation de la connexion à Supervision
 *
 * @version 1.0
 * @date 03/04/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */
public class MainActivity extends AppCompatActivity {

    /** Gestion des onglets */
    private ViewPager pager;
    private List<CharSequence> tabTitles = new ArrayList<>();               /**< Noms des onglets */

    private MaterialDialog currentDialog;                                   /**< Récupération d'une dialog présentée */
    private DialogGenerator dialogGenerator = new DialogGenerator(this);    /**< Affichage d'une dialog */


    /**
     * À la création de l'activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Création des titres d'onglet */
        tabTitles.add(getResources().getString(R.string.tabSummary));
        tabTitles.add(getResources().getString(R.string.tabRelay));

        /* Création du ViewPagerAdapter grâce aux titres + nombre d'onglets et au FragmentManager */
        CharSequence[] tabItems = tabTitles.toArray(new CharSequence[tabTitles.size()]);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabItems);

        /* Attribution de l'adapter à la vue */
        pager = (ViewPager) findViewById(R.id.main_sliding_pager);
        if (pager != null) {
            pager.setAdapter(adapter);
        }

        /* Attribution du ViewPager pour le SlidingTabsLayout */
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.main_sliding_tabs);
        if (tabs != null) {
            tabs.setDistributeEvenly(true); // Chaque onglet a la même largeur
            tabs.setViewPager(pager);
        }
    }

    /**
     * Création des items de la toolbar à partir du layout
     * @param menu Le menu à créer
     * @return Affiche le menu ou non
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Action à effectuer lors d'un tap sur l'icône de la toolbar
     * @param item icône qui réagit
     * @return consommation de l'action ou non
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnMainCredits) {
            MaterialDialog.Builder creditsBuilder = new MaterialDialog.Builder(this)
                    .title(getString(R.string.creditsTitle))
                    .customView(R.layout.credits_view, true)
                    .autoDismiss(true);

            MaterialDialog creditsDialog = creditsBuilder.build();
            TextView textView = creditsDialog.getContentView();
            if (textView != null) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
            creditsDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Lorsque l'app est relancée
     */
    @Override
    protected void onResume() {
        super.onResume();
        /* Demande de se reconnecter */
        Connection co = Connection.getConnection(this);
        if (co == null || co.getSocket() == null || !co.getSocket().isConnected() || !co.getConnectionState()) {
            co = Connection.setUpNewConnection(this);
            currentDialog = dialogGenerator.generate(DialogGenerator.DialogType.CONNECTION);
            if (currentDialog != null && !currentDialog.isShowing()) {
                currentDialog.show();
            }
        }
        if(co.getSocket() != null) {
            co.refreshingSummary(true); /**< Actualisation des données */
        }
        Data.getData().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Connection co = Connection.getConnection(this);
        if (co != null) {
            co.refreshingSummary(false); /**< On arrête l'actualisation des données */
        }
    }

    /**
     * Gestion du retour/annulation lors de l'appui sur le bouton physique Retour
     */
    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();          // L'app quitte si l'onglet actuel est Bilan
        } else {
            pager.setCurrentItem(0, true);  // Les autres onglets font revenir à Bilan
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Connection co = Connection.getConnection(this);
        if (co.getSocket() != null) {
            try {
               co.close();
            } catch (IOException ioe) {
                Log.d("CONNECTION", "Error closing socket");
            }
        }
    }

    public MaterialDialog getCurrentDialog() {
        return currentDialog;
    }
}
