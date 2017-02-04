package com.ioreef.aqctelco.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ioreef.aqctelco.LightActivity;
import com.ioreef.aqctelco.R;
import com.ioreef.aqctelco.network.Connection;
import com.ioreef.aqctelco.tank.Curve;
import com.ioreef.aqctelco.tank.ProxyTank;
import com.ioreef.aqctelco.tank.Sensor;
import com.ioreef.aqctelco.telco.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter pour la liste de cases de SummaryFragment
 *
 * @version 1.0
 * @date 19/05/2016
 * @author Thomas NAUDET
 * @copyright BSD 3-Clause
 */

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder> {

    /**
     * Types de cases
     */
    public enum SummaryCardType {
        INVISIBLE(0),  /**< Case invisible permettant d'avoir une case éclairage plus large */
        SMALL(1),      /**< Case ne contenant qu'un titre et une valeur */
        MEDIUM(2),     /**< Case contenant un titre, une valeur et un graphique */
        LIGHT(3);      /**< Case contenant les informations d'éclairage */

        private final int value;
        private static Map<Integer, SummaryCardType> map = new HashMap<>();

        static {
            for (SummaryCardType val : SummaryCardType.values()) {
                map.put(val.getValue(), val);
            }
        }

        SummaryCardType(final int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        /**
         * Crée le type énuméré à partir d'un Entier donné par l'adapter
         * @param viewType Type de case
         * @return Action du type de l'énumération
         */
        public static SummaryCardType fromInt(int viewType) {
            return map.get(viewType);
        }
    }

    /* Attributs */
    private Activity activity;
    private Context context;
    private List<Sensor> sensorList;
    private List<Curve> curveList;

    /* Constructeur */
    public SummaryAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.sensorList = Data.getData().getSensorList();
        this.curveList = Data.getData().getCurveList();
    }

    public SummaryAdapter(Activity activity, List<Sensor> sensorList, List<Curve> curveList) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.sensorList = sensorList;
        this.curveList = curveList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
        notifyDataSetChanged();
    }
    public void setCurveList(List<Curve> curveList) {
        this.curveList = curveList;
        notifyDataSetChanged();
    }

    /* Fonctions Adapter */

    /**
     * Création du view holder pour la vue issue du layout d'une case
     * @param parent widget parent utilisé pour en récupérer le contexte
     * @param viewType type de case à dessiner
     * @return Le view holder créé pour une case
     */
    @Override
    public SummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (SummaryCardType.fromInt(viewType)) {
            case LIGHT:
                return new SummaryChartViewHolder(li.inflate(R.layout.card_summary_light, parent, false));
            case MEDIUM:
                return new SummaryChartViewHolder(li.inflate(R.layout.card_summary_medium, parent, false));
            case SMALL:
                return new SummaryViewHolder(li.inflate(R.layout.card_summary_small, parent, false));
            default:
                return new SummaryViewHolder(li.inflate(R.layout.card_summary_invisible, parent, false));
        }
    }

    /**
     * Lie un view holder aux données
     * @param holder view holder en question
     * @param position position dans la liste, utile pour récupérer les bonnes données
     */
    @Override
    public void onBindViewHolder(SummaryViewHolder holder, int position) {

        int index = position;
        if (index > 1) {    // décalage par rapport aux données dû à la case Consigne éclairage et à la séparation
            index -= 2;
        }
        SummaryCardType caseType = SummaryCardType.fromInt(getItemViewType(position));

        /* Configuration des textes */
        if (caseType == SummaryCardType.LIGHT) {
            /* Il s'agit de la consigne d'éclairage */
            holder.vhTitle.setText(R.string.summaryLightTitle);
            holder.vhValue.setText(R.string.summaryBtnLightEdit);

        } else if (caseType != SummaryCardType.INVISIBLE) {
            /* Il s'agit d'un capteur, on affiche son nom et sa dernière valeur */
            Sensor sensor = sensorList.get(index);
            holder.vhTitle.setText(sensor.getName());
            if (sensor.getValues().size() > 0) {
                holder.vhValue.setText(sensor.getDisplayValue(context));
            } else {
                holder.vhValue.setText(R.string.summaryUnknownValue);
            }
        }

        /* Un tap sur les cases fixes affiche l'écran d'éclairage */
        if (caseType == SummaryCardType.LIGHT || caseType == SummaryCardType.INVISIBLE) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Demande des données des LED */
                    if (Connection.getConnection(activity).getConnectionState()) {
                        ProxyTank proxyTank = new ProxyTank(activity);
                        proxyTank.askRaysInfo();
                        proxyTank.askCurves();
                    }

                    Intent intent = new Intent(context, LightActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        /* Configuration du graphe si la case en comporte */
        if (caseType == SummaryCardType.LIGHT || caseType == SummaryCardType.MEDIUM) {

            /* Récupération des valeurs */
            List<ILineDataSet> dataSets = new ArrayList<>();
            int nbrMaxValues = 0;

            if (caseType == SummaryCardType.LIGHT) {
                for (Curve curve : curveList) {
                    ArrayList<Entry> yVals = new ArrayList<>();
                    List<Integer> points = curve.getPoints();
                    for (int i = 0; i < points.size(); ++i) {
                        yVals.add(new Entry(points.get(i), i));
                    }
                    if (points.size() > nbrMaxValues) {
                        nbrMaxValues = points.size();
                    }
                    SummaryLineDataSet set = new SummaryLineDataSet(yVals, "DataSet");
                    dataSets.add(set);
                }
            } else {
                ArrayList<Entry> yVals = new ArrayList<>();
                Sensor sensor = sensorList.get(index);
                List<Float> sensorValues = sensor.getValues();
                nbrMaxValues = sensorValues.size();
                for (int i = 0; i < nbrMaxValues; ++i) {
                    yVals.add(new Entry(sensorValues.get(i), i));
                }
                SummaryLineDataSet set1 = new SummaryLineDataSet(yVals, "DataSet");
                dataSets.add(set1);
            }

            /* Insertion dans le graphe */
            SummaryChartViewHolder chartHolder = (SummaryChartViewHolder) holder;
            if (caseType == SummaryCardType.LIGHT) {
                YAxis chartY = chartHolder.chart.getAxisLeft();
                chartY.setAxisMaxValue(100);
                chartY.setAxisMinValue(0);
            }
            LineChart chart = chartHolder.chart;

            List<String> xVals = SummaryFragment.generateTimeLabels(nbrMaxValues, caseType == SummaryCardType.MEDIUM);
            LineData data = new LineData(xVals, dataSets);
            chart.setData(data);
            chart.notifyDataSetChanged();
        }
    }

    /**
     * Retourne le nombre de cases + 2 cases fixes
     * @return nombre total de cases dans la liste en grille
     */
    @Override
    public int getItemCount() {
        if (sensorList.isEmpty()) {
            return 0;
        }
        return sensorList.size() + 2;
    }

    /**
     * Informe du type de la case à afficher selon la position
     * @param position position de la case dans la liste en grille
     * @return type de case
     */
    @Override
    public int getItemViewType(int position) {
        /* 2 cases fixes */
        if (position == 0) {
            return SummaryCardType.LIGHT.getValue();      // Consigne éclairage fixe
        } else if (position == 1) {
            return SummaryCardType.INVISIBLE.getValue();  // Séparation fixe
        }

        /* Capteur */
        int index = position - 2;   // 2 cases fixes
        Sensor sensor = sensorList.get(index);
        if (sensor.getValues().size() > 1) {    // On lui attribue un graphe s'il a plusieurs valeurs
            return SummaryCardType.MEDIUM.getValue();
        }

        return SummaryCardType.SMALL.getValue();
    }


    /**
     * Définition d'un ViewHolder pour une case de la liste
     */
    public class SummaryViewHolder extends RecyclerView.ViewHolder {

        protected CardView cardView;
        protected TextView vhTitle,
                           vhValue;

        public SummaryViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cardView);
            vhTitle  = (TextView) v.findViewById(R.id.cardTitle);
            vhValue  = (TextView) v.findViewById(R.id.cardValue);
        }

    }

    /**
     * Définition d'un ViewHolder pour une case de la liste avec un graphe
     */
    public class SummaryChartViewHolder extends SummaryViewHolder {

        protected LineChart chart;

        public SummaryChartViewHolder(View v) {
            super(v);
            chart = (LineChart) v.findViewById(R.id.chart);
            chart.setDescription("");
            chart.setNoDataText(context.getString(R.string.noChartData));
            chart.setTouchEnabled(false);
            chart.getLegend().setEnabled(false);

            XAxis chartX = chart.getXAxis();
            chartX.setPosition(XAxis.XAxisPosition.BOTTOM);
            chartX.setTextSize(12);
            chartX.setTextColor(ContextCompat.getColor(context, R.color.curveAxisLabelColor));
            chartX.setAxisLineColor(ContextCompat.getColor(context, R.color.curveAxisColor));
            chartX.setGridColor(ContextCompat.getColor(context, R.color.curveGridVerticalColor));

            YAxis chartYHidden = chart.getAxisRight();
            chartYHidden.setDrawGridLines(false);
            chartYHidden.setDrawLabels(false);
            chartYHidden.setAxisLineColor(ContextCompat.getColor(context, R.color.curveAxisColor));
            YAxis chartY = chart.getAxisLeft();
            chartY.setDrawAxisLine(false);
            chartY.setTextSize(12);
            chartY.setTextColor(ContextCompat.getColor(context, R.color.curveLabelColor));
            chartY.setGridColor(ContextCompat.getColor(context, R.color.curveGridHorizontalColor));
        }

    }


    /**
     * Classe modèle pour la création d'une courbe
     * Définit l'affichage des lignes et points
     */
    public class SummaryLineDataSet extends LineDataSet {

        public SummaryLineDataSet(List<Entry> yVals, String label) {
            super(yVals, label);

            setColor(ContextCompat.getColor(context, R.color.curveLineColor));
            setCircleColor(ContextCompat.getColor(context, R.color.curvePointColor));
            setLineWidth(1f);
            setCircleRadius(3f);
            setDrawCircleHole(false);
            setDrawValues(false);
//            setValueTextColor(ContextCompat.getColor(getContext(), R.color.curveLabelColor));
//            setValueTextSize(9f);
            setCubicIntensity(0.1f);
            setDrawFilled(true);
            setDrawCircles(false);
            setDrawCubic(true);
            setFillColor(ContextCompat.getColor(context, R.color.curveFillColor));
            setFillAlpha(180);
        }

    }
}
