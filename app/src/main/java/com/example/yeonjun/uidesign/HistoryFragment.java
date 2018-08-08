package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class HistoryFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private static final String TAG = "HistoryFragment";
    private LineChart mChart;
    Spinner objectSpinner;
    Spinner dateSpinner;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.SUCCESS:
                    try {
                        JSONObject response = new JSONObject(sp.getString(StatusCode.HISTROICAL_AQI, null));
                        JSONArray array = response.getJSONArray("data");
                        for(int i = 0; i < array.length(); i++)
                            aqiArrayList.add(new AQI(array.getJSONObject(i)));
                    } catch (Exception e){

                    }
                    break;
                case StatusCode.FAILED:
                    break;
            }
        }
    };
    ArrayList<AQI> aqiArrayList = new ArrayList<AQI>();

    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = getActivity().getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE);

        objectSpinner = view.findViewById(R.id.objectSpinner);
        dateSpinner = view.findViewById(R.id.dateSpinner);
        String[] objectItems = new String[]{"Heart-rate", "RR-interval",  "CO", "CO2", "NO2", "SO2", "O3", "Temperature"};
        String[] dateItems = new String[]{"1 DAY", "1 WEEK", "1 MONTH"};

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(objectSpinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, objectItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objectSpinner.setAdapter(adapter);
        objectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MySingletone.getInstance().ShowToastMessage(Integer.toString(position), getContext());
                switch (position) {
                    case 0: // Heart
                        break;
                    case 1: // CO

                        break;
                    case 2: // CO2

                        break;
                    case 3: // NO2

                        break;
                    case 4: // SO2

                        break;
                    case 5: // O3

                        break;
                    case 6: // PM2.5

                        break;
                    case 7: // PM10

                        break;
                    case 8: // Temperature

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, dateItems);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter2);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MySingletone.getInstance().ShowToastMessage(Integer.toString(position), getContext());
                switch (position) {
                    case 0: // 1 DAY
                        new HistoricalAQITask(mHandler, sp).execute();
                        break;
                    case 1: // 1 WEEK
                        new HistoricalAQITask(mHandler, sp).execute();
                        break;
                    case 2: // 1 MONTH
                        new HistoricalAQITask(mHandler, sp).execute();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        mChart = (LineChart)view.findViewById(R.id.lineChart);
        mChart.setOnChartGestureListener(HistoryFragment.this);
        mChart.setOnChartValueSelectedListener(HistoryFragment.this);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);

//        LimitLine upper_limit = new LimitLine(65f, "Danger");
//        upper_limit.setLineWidth(4f);
//        upper_limit.enableDashedLine(10f, 10f, 0f);
//        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        upper_limit.setTextSize(15);
//
//        LimitLine lower_limit = new LimitLine(35f, "Too Low");
//        lower_limit.setLineWidth(4f);
//        lower_limit.enableDashedLine(10f, 10f, 0f);
//        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        lower_limit.setTextSize(15);
//
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0);
        leftAxis.setTextSize(15);
        //leftAxis.enableGridDashedLine(10f,10f,0f);
        //leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 60));
        yValues.add(new Entry(1, 40));
        yValues.add(new Entry(2, 30));
        yValues.add(new Entry(3, 70));
        yValues.add(new Entry(4, 50));
        yValues.add(new Entry(5, 70));
        yValues.add(new Entry(6, 50));


        LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
        set1.setFillAlpha(110);
        set1.setColor(Color.RED);
        set1.setLineWidth(3);
        set1.setValueTextSize(10);
        set1.setValueTextColor(Color.BLACK);
        set1.setDrawHorizontalHighlightIndicator(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        mChart.setData(data);

        String[] values = new String[] {"07:11", "07:34", "08:12", "08:34", "09:01", "09:22", "10:22"};

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1);
        xAxis.setTextSize(15);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter{
        private  String[] mValues;
        public MyXAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i(TAG, "onCharGestureStart: X: " + me.getX() + "Y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i(TAG, "onCharGestureStart: " + lastPerformedGesture);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i(TAG, "onChartLongPressed: ");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i(TAG, "onChartDoubleTapped: ");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i(TAG, "onChartSingleTapped: ");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i(TAG, "onChartFling: veloX: " + velocityX + "veloY" + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i(TAG, "onChartScale: scaleX: " + scaleX + "scaleY" + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i(TAG, "onChartTranslate: dX: " + dX + "dY" + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i(TAG, "onValueSelected: " + e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i(TAG, "onNothingSelected: ");
    }
}
