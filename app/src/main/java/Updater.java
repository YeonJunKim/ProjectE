package com.example.yeonjun.uidesign;

import android.os.Handler;

import java.util.TimerTask;

public class Updater extends TimerTask {
    private Handler handler;

    protected Updater(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        handler.obtainMessage(StatusCode.UPDATE).sendToTarget();
    }
}
