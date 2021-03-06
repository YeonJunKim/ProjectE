package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

public class HeartFragment extends Fragment {
    TextView monitorHeart, monitorRR;
    ImageView ivHeartbeat, ivHeartbeat2;
    SharedPreferences sp;
    private static Updater heartUpdater;

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

        ivHeartbeat = view.findViewById(R.id.ivHeartbeat);
        Glide.with(this).asGif().load(R.raw.heartbeat).into(ivHeartbeat);

        ivHeartbeat2 = view.findViewById(R.id.ivHeartbeat2);
        Glide.with(this).asGif().load(R.raw.heartbeat3).into(ivHeartbeat2);

        monitorHeart = view.findViewById(R.id.monitorHeart);
        monitorRR = view.findViewById(R.id.monitorRR);
    }

    @Override
    public void onResume() {
        heartUpdater = new Updater(mHandler);
        new Timer().schedule(heartUpdater, 0, 1000);
        super.onResume();
    }

    @Override
    public void onPause() {
        heartUpdater.cancel();
        super.onPause();
    }
}
