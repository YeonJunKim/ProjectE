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
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HistoryFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    private static final String TAG = "HistoryFragment";
    private LineChart mChart;
    Spinner objectSpinner;
    Spinner dateSpinner;

    String selectedObject = "Drowsiness";
    String selectedDateTerm = "DAY";
    ArrayList<String> xValues = new ArrayList<>();  // 귀찮아서 맴버변수로 뺏음

    TextView yAxisText;
    TextView xAxisText;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mChart.clear();
            aqiArrayList.clear();
            switch (msg.what){
                case StatusCode.GET_HISTORICAL_AQI:
                    try {
                        JSONObject response = new JSONObject(sp.getString(StatusCode.HISTROICAL_AQI, null));
                        JSONArray array = response.getJSONArray("data");
                        for(int i = 0; i < array.length(); i++)
                            aqiArrayList.add(new AQI(array.getJSONObject(i)));
                    } catch (Exception e){
                        Log.i("JADE-ERROR", e.toString());
                    }
                    break;
                case StatusCode.GET_HISTORICAL_HEART:
                    try {
                        JSONObject response = new JSONObject(sp.getString(StatusCode.HISTROICAL_AQI, null));
                        JSONArray array = response.getJSONArray("data");
                        for(int i = 0; i < array.length(); i++)
                            aqiArrayList.add(new AQI(array.getJSONObject(i)));
                    } catch (Exception e){
                        Log.i("JADE-ERROR", e.toString());
                    }
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("failed data load", getContext());
                    break;
            }
            UpdateChart();
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

        yAxisText = view.findViewById(R.id.yAxisTextView);
        xAxisText = view.findViewById(R.id.xAxisTextView);

        mChart = view.findViewById(R.id.lineChart);
        mChart.setOnChartGestureListener(HistoryFragment.this);
        mChart.setOnChartValueSelectedListener(HistoryFragment.this);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);

        objectSpinner = view.findViewById(R.id.objectSpinner);
        dateSpinner = view.findViewById(R.id.dateSpinner);
        String[] objectItems = new String[]{"Drowsiness", "Heart-rate", "RR-interval",  "CO", "CO2", "NO2", "SO2", "O3", "Temperature"};
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
                switch (position) {
                    case 0: // Drowsiness
                        selectedObject = "Drowsiness";
                        break;
                    case 1: // Heart-rate
                        selectedObject = "Heart-rate";
                        break;
                    case 2: // RR-interval
                        selectedObject = "RR-interval";
                        break;
                    case 3: // CO
                        selectedObject = "CO";
                        break;
                    case 4: // CO2
                        selectedObject = "CO2";
                        break;
                    case 5: // NO2
                        selectedObject = "NO2";
                        break;
                    case 6: // SO2
                        selectedObject = "SO2";
                        break;
                    case 7: // O3
                        selectedObject = "O3";
                        break;
                    case 8: // Temperature
                        selectedObject = "Temperature";
                        break;
                }

                ArrayList<String> dateList = CalculateDate(selectedDateTerm);
                new HistoricalAQITask(mHandler, sp).execute(dateList.get(0), dateList.get(1), dateList.get(2), dateList.get(3));
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
                ArrayList<String> dateList;
                switch (position) {
                    case 0: // 1 DAY
                        selectedDateTerm = "DAY";
                        break;
                    case 1: // 1 WEEK
                        selectedDateTerm = "WEEK";
                        break;
                    case 2: // 1 MONTH
                        selectedDateTerm = "MONTH";
                        break;
                }

                dateList = CalculateDate(selectedDateTerm);
                new HistoricalAQITask(mHandler, sp).execute(dateList.get(0), dateList.get(1), dateList.get(2), dateList.get(3));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter{
        private  String[] mValues;
        public MyXAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(value > mValues.length - 1)
                return "";
            return mValues[(int)value];
        }
    }



    ArrayList<String> CalculateDate(String when) {

        ArrayList<String> dateList = new ArrayList<String>();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        String startDate;
        String currentDate = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
        String currentTime = Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second);

        if(when.contentEquals("WEEK")) {
            day = day - 7;
        }
        else if (when.contentEquals("MONTH")) {
            month--;
        }
        else {  // 1 DAY
            day = day - 1;
        }

        if(day <= 0) {
            day = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH - day);
            month--;
        }
        if(month <= 0) {
            month = 12;
            year--;
        }
        startDate = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
        dateList.add(startDate);
        dateList.add(currentTime);
        dateList.add(currentDate);
        dateList.add(currentTime);

        return dateList;
    }


    void UpdateChart() {

        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<Float> values;
        xValues.clear();

        if(selectedDateTerm.contentEquals("MONTH")) {
            values = GetMonthAverageData();
            xAxisText.setText("Date");
        } else if(selectedDateTerm.contentEquals("WEEK")) {
            values = GetWeekAverageData();
            xAxisText.setText("Date");
        } else {
            values = GetDayAverageData();
            xAxisText.setText("Time");
        }

        for(int i = 0; i < values.size(); i++) {
            yValues.add(new Entry(i, values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(yValues, selectedObject);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setHighlightEnabled(true);
        dataSet.setLineWidth(10);
        dataSet.setCircleRadius(6);
        dataSet.setCircleHoleRadius(3);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setHighLightColor(Color.RED);
        dataSet.setValueTextSize(12);

        LineData data = new LineData(dataSet);
        mChart.setData(data);


            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines();
            leftAxis.setTextSize(15);

            if(selectedObject.contentEquals("Drowsiness")){
                leftAxis.setAxisMaximum(100f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("%");
            }
            else if(selectedObject.contentEquals("Heart-rate")) {
                leftAxis.setAxisMaximum(200f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("Rate");
            }
            else if(selectedObject.contentEquals("RR-interval")) {
                leftAxis.setAxisMaximum(500f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("RR");
            }
            else if(selectedObject.contentEquals("CO")) {
                leftAxis.setAxisMaximum(500f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("AQI");
            }
            else if(selectedObject.contentEquals("CO2")) {
                leftAxis.setAxisMaximum(500f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("AQI");
            }
            else if(selectedObject.contentEquals("NO2")) {
                leftAxis.setAxisMaximum(500f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("AQI");
            }
            else if(selectedObject.contentEquals("SO2")) {
                leftAxis.setAxisMaximum(500f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("AQI");
            }
            else if(selectedObject.contentEquals("O3")) {
                leftAxis.setAxisMaximum(500f);
                leftAxis.setAxisMinimum(0);
                yAxisText.setText("AQI");
            }
            else if(selectedObject.contentEquals("Temperature")) {
                leftAxis.setAxisMaximum(40f);
                leftAxis.setAxisMinimum(-20f);
                yAxisText.setText("℃");
            }


        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xValues.toArray(new String[0])));
        xAxis.setGranularity(1);
        xAxis.setTextSize(15);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mChart.invalidate();
    }



    ArrayList<Float> GetWeekAverageData() {

        ArrayList<String> dividedDates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int devideNum = 7;

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        String time = Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second);

        for(int i = 0; i < devideNum; i++) {
            if(i != 0)
                cal.add(Calendar.DATE, -1); // dividedDates의 0번째 인덱스에 현재 날짜를 넣기 위해

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String date;
            if(month < 10 && day < 10){
                date = Integer.toString(year) + "-0" + Integer.toString(month) + "-0" + Integer.toString(day) + "T" + time;
            }
            else if(month < 10) {
                date = Integer.toString(year) + "-0" + Integer.toString(month) + "-" + Integer.toString(day) + "T" + time;
            }
            else if(day < 10) {
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-0" + Integer.toString(day) + "T" + time;
            }
            else {
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day) + "T" + time;
            }

            dividedDates.add(date);

            String temp = date.substring(5, 10);
            temp = temp.replace('-', '/');
            xValues.add(0, temp);
        }


        int sliceCount = 0;
        int count = 0;
        float valueSum = 0;
        ArrayList<Float> values = new ArrayList<>();
        for(int i = 0; i < aqiArrayList.size(); i++) {

            if(aqiArrayList.get(i).getTime().compareTo(dividedDates.get(devideNum - 1 - sliceCount)) < 0){
                valueSum += GetCurrentObjectValue(i);
                count++;
            }
            else {
                values.add(valueSum / count);
                valueSum = GetCurrentObjectValue(i);
                sliceCount++;
                count = 1;
            }
        }
        values.add(valueSum / count);

        for(int i = 0; i < devideNum - sliceCount - 1; i++) {
            values.add((float)0/0);
        }


        return values;
    }


    ArrayList<Float> GetMonthAverageData() {

        ArrayList<String> dividedDates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int devideNum = 7;

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        String time = Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second);

        for(int i = 0; i < devideNum; i++) {
            if(i != 0)
                cal.add(Calendar.DATE, -1 * 5); // dividedDates의 0번째 인덱스에 현재 날짜를 넣기 위해

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String date;
            if(month < 10 && day < 10){
                date = Integer.toString(year) + "-0" + Integer.toString(month) + "-0" + Integer.toString(day) + "T" + time;
            }
            else if(month < 10) {
                date = Integer.toString(year) + "-0" + Integer.toString(month) + "-" + Integer.toString(day) + "T" + time;
            }
            else if(day < 10) {
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-0" + Integer.toString(day) + "T" + time;
            }
            else {
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day) + "T" + time;
            }

            dividedDates.add(date);

            String temp = date.substring(5, 10);
            temp = temp.replace('-', '/');
            xValues.add(0, temp);
        }


        int sliceCount = 0;
        int count = 0;
        float valueSum = 0;
        ArrayList<Float> values = new ArrayList<>();
        for(int i = 0; i < aqiArrayList.size(); i++) {
            if(aqiArrayList.get(i).getTime().compareTo(dividedDates.get(devideNum - 1 - sliceCount)) < 0){
                valueSum += GetCurrentObjectValue(i);
                count++;
            }
            else {
                Log.d("count", Integer.toString(count));
                Log.d("valueSum", Float.toString(valueSum));
                values.add(valueSum / count);
                valueSum = GetCurrentObjectValue(i);
                sliceCount++;
                count = 1;
            }
        }
        values.add(valueSum / count);

        for(int i = 0; i < devideNum - sliceCount - 1; i++) {
            values.add((float)0/0);
        }


        return values;
    }



    ArrayList<Float> GetDayAverageData() {
        ArrayList<String> dividedDates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int devideNum = 7;



        for(int i = 0; i < devideNum; i++) {
            if(i != 0)
                cal.add(Calendar.HOUR, -4); // dividedDates의 0번째 인덱스에 현재 날짜를 넣기 위해

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // in java month starts from 0 not from 1 so for december 11+1 = 12
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);


            String time;
            if(hour < 10 && minute < 10){
                time = "0" + Integer.toString(hour) + ":0" + Integer.toString(minute) + ":" + Integer.toString(second);
            }
            else if(minute < 10) {
                time = Integer.toString(hour) + ":0" + Integer.toString(minute) + ":" + Integer.toString(second);
            }
            else if(hour < 10) {
                time = "0" + Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second);
            }
            else {
                time = Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second);
            }


            String date;
            if(month < 10 && day < 10){
                date = Integer.toString(year) + "-0" + Integer.toString(month) + "-0" + Integer.toString(day) + "T" + time;
            }
            else if(month < 10) {
                date = Integer.toString(year) + "-0" + Integer.toString(month) + "-" + Integer.toString(day) + "T" + time;
            }
            else if(day < 10) {
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-0" + Integer.toString(day) + "T" + time;
            }
            else {
                date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day) + "T" + time;
            }


            dividedDates.add(date);

            String temp = date.substring(11, 16);
            xValues.add(0, temp);
        }


        int sliceCount = 0;
        int count = 0;
        float valueSum = 0;
        ArrayList<Float> values = new ArrayList<>();
        for(int i = 0; i < aqiArrayList.size(); i++) {
            if(aqiArrayList.get(i).getTime().compareTo(dividedDates.get(devideNum - 1 - sliceCount)) < 0){
                valueSum += GetCurrentObjectValue(i);
                count++;
            }
            else {
                values.add(valueSum / count);
                valueSum = GetCurrentObjectValue(i);
                sliceCount++;
                count = 1;
            }
        }
        values.add(valueSum / count);

        for(int i = 0; i < devideNum - sliceCount - 1; i++) {
            values.add((float)0/0);
        }


        return values;
    }





    float GetCurrentObjectValue(int index) {
        if(selectedObject.contentEquals("Drowsiness")){
            //
        }
        else if(selectedObject.contentEquals("Heart-rate")) {
            //
        }
        else if(selectedObject.contentEquals("RR-interval")) {
            //
        }
        else if(selectedObject.contentEquals("CO")) {
            return aqiArrayList.get(index).getCO().floatValue();
        }
        else if(selectedObject.contentEquals("CO2")) {
            //
        }
        else if(selectedObject.contentEquals("NO2")) {
            return aqiArrayList.get(index).getNO2().floatValue();
        }
        else if(selectedObject.contentEquals("SO2")) {
            return aqiArrayList.get(index).getSO2().floatValue();
        }
        else if(selectedObject.contentEquals("O3")) {
            return aqiArrayList.get(index).getO3().floatValue();
        }
        else if(selectedObject.contentEquals("Temperature")) {
            return aqiArrayList.get(index).getTemp().floatValue();
        }

        return 0;
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

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

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
