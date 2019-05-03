package com.example.yeonjun.uidesign;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class GPSCollecter extends Service {
    private LocationManager lm;
    private SharedPreferences.Editor editor;
    private LocationListener GPSListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        editor = getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE).edit();
        GPSListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lng = location.getLongitude();
                double lat = location.getLatitude();
                Log.i("JADE-GPS", "lng = " + lng + ", lat = " + lat);
                editor.putFloat(StatusCode.LONGITUDE, (float)lng);
                editor.putFloat(StatusCode.LATITUDE, (float)lat);
                editor.commit();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(GPSListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, GPSListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, GPSListener);
        return super.onStartCommand(intent, flags, startId);
    }
}
