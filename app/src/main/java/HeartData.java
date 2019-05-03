package com.example.yeonjun.uidesign;

import org.json.JSONObject;

public class HeartData {
    private Integer heartRate;
    private Double rrInterval;
    private String time;

    public HeartData(JSONObject obj) {
        try {
            heartRate = obj.getInt("heart_rate");
            rrInterval = obj.getDouble("rr_interval");
            time = obj.getString("timestamp");
        } catch (Exception e){

        }
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Double getRrInterval() {
        return rrInterval;
    }

    public void setRrInterval(Double rrInterval) {
        this.rrInterval = rrInterval;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
