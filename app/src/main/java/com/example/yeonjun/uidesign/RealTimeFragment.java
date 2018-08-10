package com.example.yeonjun.uidesign;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;


public class RealTimeFragment extends Fragment {

    ProgressBar aqiProgressBar;
    ProgressBar coProgressBar;
    ProgressBar o3ProgressBar;
    ProgressBar no2ProgressBar;
    ProgressBar so2ProgressBar;
    TextView aqiNumberTextView;
    TextView coNumberTextView;
    TextView o3NumberTextView;
    TextView no2NumberTextView;
    TextView so2NumberTextView;
    Handler handler = new Handler();

    float aqi = 32;
    float o3 = 22;
    float no2 = 11;
    float so2 = 3;
    float co = 44;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_real_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aqiProgressBar = view.findViewById(R.id.aqiProgressBar);
        coProgressBar = view.findViewById(R.id.coProgressBar);
        o3ProgressBar = view.findViewById(R.id.o3ProgressBar);
        no2ProgressBar = view.findViewById(R.id.no2ProgressBar);
        so2ProgressBar = view.findViewById(R.id.so2ProgressBar);

        aqiNumberTextView = view.findViewById(R.id.aqiNumberTextView);
        coNumberTextView = view.findViewById(R.id.coNumberTextView);
        o3NumberTextView = view.findViewById(R.id.o3NumberTextView);
        no2NumberTextView = view.findViewById(R.id.no2NumberTextView);
        so2NumberTextView = view.findViewById(R.id.so2NumberTextView);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {


                            // TODO Auto-generated method stub
                            Faaaaaaaaaaaaaaaake(aqiProgressBar, aqiNumberTextView, GetFakeChange(aqi, 0, 500));
                            Faaaaaaaaaaaaaaaake(coProgressBar, coNumberTextView, GetFakeChange(co, 0, 500));
                            Faaaaaaaaaaaaaaaake(o3ProgressBar, o3NumberTextView, GetFakeChange(o3, 0, 500));
                            Faaaaaaaaaaaaaaaake(no2ProgressBar, no2NumberTextView, GetFakeChange(no2, 0, 500));
                            Faaaaaaaaaaaaaaaake(so2ProgressBar, so2NumberTextView, GetFakeChange(so2, 0, 500));
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(1000); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void Faaaaaaaaaaaaaaaake(ProgressBar bar, TextView text, float value) {

        bar.setProgress(100);
        text.setText(Integer.toString((int)value));

        String color;
        if(value > 300)
            color = "#7E0023";
        else if(value > 200)
            color = "#99004C";
        else if(value > 150)
            color = "#FF0000";
        else if(value > 100)
            color = "#FF7E00";
        else if(value > 50)
            color = "#FFFF00";
        else
            color = "#00E400";

        bar.getProgressDrawable().setColorFilter(Color.parseColor(color), android.graphics.PorterDuff.Mode.SRC_IN);
    }


    float GetFakeChange(float originalValue, int min, int max) {
        int sign = 1;
        int changeAmount = 0;

        Random r = new Random();
        int randNum;
        randNum = r.nextInt(100 - 0) + 0;
        if(randNum < 50) {
            sign = -1;
        }

        changeAmount = r.nextInt(3 - 0) + 0;

        float changedValue = originalValue;
        changedValue += changeAmount * sign;
        if(changedValue < min)
            changedValue = min;
        if(changedValue > max) {
            changedValue = max;
        }

        return  changedValue;
    }

}
