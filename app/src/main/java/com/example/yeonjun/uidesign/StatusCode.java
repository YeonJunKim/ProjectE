package com.example.yeonjun.uidesign;

public interface StatusCode {
    public static final int FAILED = 11;
    public static final int SUCCESS = 12;
    public static final int DUPLICATE_ID = 13;
    public static final int NOT_DUPLICATE_ID = 14;
    public static final int DUPLICATE_EMAIL = 15;
    public static final int NOT_DUPLICATE_MAIL = 16;
    public static final int UPDATE = 17;

    public static final int REGISTER_VERIFY = 21;
    public static final int FORGOT_PW_VERIFY = 22;
    public static final int CANCEL_ID_VERIFY = 23;

    public static final int RECEIVE_SENSOR_LIST = 31;
    public static final int REGIST_SENSOR = 32;
    public static final int DEREGIST_SENSOR = 33;

    public static final int GET_HISTORICAL_AQI = 41;
    public static final int GET_HISTORICAL_HEART = 42;
    public static final int GET_HISTORICAL_APD = 43;
    public static final int GET_RECENT_AQI = 44;

    public static final int TRF_RT_AIR = 51;

    public static final String TYPE_ID = "id";
    public static final String TYPE_EMAIL = "email";

    public static final String TIMER_FORMAT = "%02d:%02d";

    public static final String LIST_SENSOR = "listSensor";
    public static final String HISTROICAL_AQI = "historicalAQI";
    public static final String HISTROICAL_HEART = "historicalHeart";
    public static final String HISTROICAL_APD = "historicalAPD";
    public static final String RT_AIR = "realtimeAir";
    public static final String RT_AQI = "realtimeAQI";
    public static final String RECENT_AQI = "recentAQI";

    public static final String LONGITUDE = "lng";
    public static final String LATITUDE = "lat";
    public static final String SSN = "ssn";
    public static final String TEMPERATURE = "temperature";

    public static final String HEART_RATE = "heartRate";
    public static final String RR_INTERVAL = "RRInterval";
}
