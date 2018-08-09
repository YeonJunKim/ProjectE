package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class HeartFragment extends Fragment {
    TextView monitorHeart, monitorRR;
    SharedPreferences sp;
    private static HeartUpdater heartUpdater;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.UPDATE:
                    monitorHeart.setText(String.valueOf(sp.getInt(StatusCode.HEART_RATE, 0)));
                    monitorRR.setText(String.valueOf(sp.getInt(StatusCode.RR_INTERVAL, 0)));
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE);

        monitorHeart = view.findViewById(R.id.monitorHeart);
        monitorRR = view.findViewById(R.id.monitorRR);
    }

    @Override
    public void onResume() {
        heartUpdater = new HeartUpdater(mHandler);
        new Timer().schedule(heartUpdater, 0, 1000);
        super.onResume();
    }

    @Override
    public void onPause() {
        heartUpdater.cancel();
        super.onPause();
    }

    private class HeartUpdater extends TimerTask{
        private Handler handler;
        protected HeartUpdater(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.obtainMessage(StatusCode.UPDATE).sendToTarget();
        }
    }
}
