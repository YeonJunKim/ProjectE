package com.example.yeonjun.uidesign;

import org.json.JSONArray;
import org.json.JSONObject;

public class AQI {
    private double CO, NO2, SO2, O3, temp;
    private String time;

    public AQI(JSONObject obj) {
        try {
            CO = obj.getDouble("CO");
            NO2 = obj.getDouble("NO2");
            SO2 = obj.getDouble("SO2");
            O3 = obj.getDouble("O3");
            temp = obj.getDouble("temperature");
            time = obj.getString("timestamp");
        } catch (Exception e){

        }
    }

    public Double getCO() {
        return CO;
    }

    public void setCO(Double CO) {
        this.CO = CO;
    }

    public Double getNO2() {
        return NO2;
    }

    public void setNO2(Double NO2) {
        this.NO2 = NO2;
    }

    public Double getSO2() {
        return SO2;
    }

    public void setSO2(Double SO2) {
        this.SO2 = SO2;
    }

    public Double getO3() {
        return O3;
    }

    public void setO3(Double o3) {
        O3 = o3;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
