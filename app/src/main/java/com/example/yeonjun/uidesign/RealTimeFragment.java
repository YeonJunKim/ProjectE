package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class RealTimeFragment extends Fragment {

    ProgressBar coProgressBar;
    ProgressBar o3ProgressBar;
    ProgressBar no2ProgressBar;
    ProgressBar so2ProgressBar;
    TextView tempNumberTextView;
    TextView coNumberTextView;
    TextView o3NumberTextView;
    TextView no2NumberTextView;
    TextView so2NumberTextView;

    private static AirUpdater airUpdater;
    SharedPreferences sp;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.UPDATE:
                    if(sp.getInt(StatusCode.SSN, -1) != -1) {
                        try {
                            JSONObject data = new JSONObject(sp.getString(StatusCode.RT_AQI, null));
                            coNumberTextView.setText(String.format("%.0f", data.getDouble("CO")));
                            o3NumberTextView.setText(String.format("%.0f", data.getDouble("O3")));
                            so2NumberTextView.setText(String.format("%.0f", data.getDouble("SO2")));
                            no2NumberTextView.setText(String.format("%.0f", data.getDouble("NO2")));
                            //tempNumberTextView.setText(String.format("%.0f", data.getDouble("TEMP")));
                            coProgressBar.getProgressDrawable().setColorFilter(GetAQIColor(data.getDouble("CO")), android.graphics.PorterDuff.Mode.SRC_IN);
                            so2ProgressBar.getProgressDrawable().setColorFilter(GetAQIColor(data.getDouble("SO2")), android.graphics.PorterDuff.Mode.SRC_IN);
                            o3ProgressBar.getProgressDrawable().setColorFilter(GetAQIColor(data.getDouble("O3")), android.graphics.PorterDuff.Mode.SRC_IN);
                            no2ProgressBar.getProgressDrawable().setColorFilter(GetAQIColor(data.getDouble("NO2")), android.graphics.PorterDuff.Mode.SRC_IN);
                        } catch (Exception e) {
                            Log.i("JADE-APD-UPDATE-ERROR", e.toString());
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_real_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = getActivity().getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE);

        coProgressBar = view.findViewById(R.id.coProgressBar);
        o3ProgressBar = view.findViewById(R.id.o3ProgressBar);
        no2ProgressBar = view.findViewById(R.id.no2ProgressBar);
        so2ProgressBar = view.findViewById(R.id.so2ProgressBar);

        tempNumberTextView = view.findViewById(R.id.tempNumberTextView);
        coNumberTextView = view.findViewById(R.id.coNumberTextView);
        o3NumberTextView = view.findViewById(R.id.o3NumberTextView);
        no2NumberTextView = view.findViewById(R.id.no2NumberTextView);
        so2NumberTextView = view.findViewById(R.id.so2NumberTextView);
    }

    @Override
    public void onResume() {
        airUpdater = new AirUpdater(handler);
        new Timer().schedule(airUpdater, 0, 5000);
        super.onResume();
    }

    @Override
    public void onPause() {
        airUpdater.cancel();
        super.onPause();
    }

    private class AirUpdater extends TimerTask {
        private Handler handler;

        protected AirUpdater(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.obtainMessage(StatusCode.UPDATE).sendToTarget();
        }
    }


    int GetAQIColor(Double aqi) {
        Log.d("ddddddddddd", Double.toString(aqi));
        int color;

        if(aqi > 300)
            color = Color.argb(200, 126, 0, 35);
        else if(aqi > 200)
            color = Color.argb(200, 153, 0, 76);
        else if(aqi > 150)
            color = Color.argb(200, 255, 0, 0);
        else if(aqi > 100)
            color = Color.argb(200, 255, 126, 0);
        else if(aqi > 50)
            color = Color.argb(200, 255, 255, 0);
        else
            color = Color.argb(200, 0, 228, 0);

        return color;
    }
}
