package com.example.yeonjun.uidesign;

import org.json.JSONObject;

public class HeartData {
    private float heartRate, rrInterval;
    private String time;

    public HeartData(JSONObject obj) {
        try {
            heartRate = (float)obj.getDouble("heart_rate");
            rrInterval = (float)obj.getDouble("rr_interval");
            time = obj.getString("timestamp");
        } catch (Exception e){

        }
    }

    public float getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(float heartRate) {
        this.heartRate = heartRate;
    }

    public float getRrInterval() {
        return rrInterval;
    }

    public void setRrInterval(float rrInterval) {
        this.rrInterval = rrInterval;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
