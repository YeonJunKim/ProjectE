package com.example.yeonjun.uidesign;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import java.util.ArrayList;
import java.util.Random;



public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mapView;
    GoogleMap map;
    Handler handler = new Handler();
    boolean isOnDestroy = false;

    ArrayList<AQICircle> aqiCircles = new ArrayList<>();
    SharedPreferences sp;


    private static final LatLng QI_LAT_LNG = new LatLng(32.882511,-117.234557);
    private static final LatLng THE_VILLAGE_LAT_LNG = new LatLng(32.888600,-117.241925);
    private static final LatLng LA_JOLLA_SHORES = new LatLng(32.858997,-117.255630);
    private static final LatLng RALPHS = new LatLng(32.866441,-117.231880);
    private static final LatLng SOMEWHERE = new LatLng(32.867962,-117.243413);
    private static final LatLng BLACKS_BEACH = new LatLng(32.888286,-117.252890);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) getView().findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        sp = getActivity().getSharedPreferences(getString(R.string.sh_pref), Context.MODE_PRIVATE);

        AQICircle a0 = new AQICircle();
        a0.setPos(QI_LAT_LNG);
        a0.SetValues(0, 25, 25, 25, 25, 25, 25);
        AQICircle a1 = new AQICircle();
        a1.setPos(THE_VILLAGE_LAT_LNG);
        a1.SetValues(1, 60, 60, 60, 60, 60, 25);
        AQICircle a2 = new AQICircle();
        a2.setPos(SOMEWHERE);
        a2.SetValues(2, 150,150, 150, 150, 150, 25);
        AQICircle a3 = new AQICircle();
        a3.setPos(RALPHS);
        a3.SetValues(3, 250, 250, 250, 250, 250, 25);
        AQICircle a4 = new AQICircle();
        a4.setPos(LA_JOLLA_SHORES);
        a4.SetValues(4, 40, 40, 40, 40, 40, 25);
        AQICircle a5 = new AQICircle();
        a5.setPos(BLACKS_BEACH);
        a5.SetValues(5, 35, 35, 35, 35, 35, 25);

        AQICircle a6 = new AQICircle();
        LatLng pos = new LatLng(sp.getFloat(StatusCode.LATITUDE, 0), sp.getFloat(StatusCode.LONGITUDE, 0));
        a6.setPos(pos);
        a6.SetValues(6, 25, 25, 25, 25, 25, 25);

        aqiCircles.add(a0);
        aqiCircles.add(a1);
        aqiCircles.add(a2);
        aqiCircles.add(a3);
        aqiCircles.add(a4);
        aqiCircles.add(a5);
        aqiCircles.add(a6);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

                for(int i = 0; i < aqiCircles.size(); i++) {
                    aqiCircles.get(i).setShowOnMap(false);
                    if (aqiCircles.get(i).getPos().toString().contentEquals(circle.getCenter().toString())) {
                        aqiCircles.get(i).setShowOnMap(true);
                    }
                }
                UpdateInfoWindows();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i = 0; i < aqiCircles.size(); i++) {
                    aqiCircles.get(i).setShowOnMap(false);
                    if (aqiCircles.get(i).getPos().toString().contentEquals(marker.getPosition().toString())) {
                        aqiCircles.get(i).setShowOnMap(true);
                    }
                }
                UpdateInfoWindows();
                return true;
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(QI_LAT_LNG, 15));
        //map.addMarker(new MarkerOptions().position(QI_LAT_LNG).title("Qualcomm Institute"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (!isOnDestroy) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Faaaaaaaaaaaaaaaake();
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(5000); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }



    private void drawCircle(LatLng point, int color){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circle = new CircleOptions();

        // Specifying the center of the circle
        circle.center(point);

        // Radius of the circle
        circle.radius(200);

        // Border color of the circle
        circle.strokeColor(Color.BLACK);

        // Fill color of the circle
        circle.fillColor(color);

        // Border width of the circle
        circle.strokeWidth(2);

        circle.clickable(true);

        // Adding the circle to the GoogleMap
        map.addCircle(circle);
    }


    void Faaaaaaaaaaaaaaaake() {
        map.clear();

        for(int i = 0; i < aqiCircles.size(); i++) {

            aqiCircles.get(i).setAqi(GetFakeChange(aqiCircles.get(i).getAqi(), 0, 500));
            aqiCircles.get(i).setCo(GetFakeChange(aqiCircles.get(i).getCo(), 0, 500));
            aqiCircles.get(i).setNo2(GetFakeChange(aqiCircles.get(i).getNo2(), 0, 500));
            aqiCircles.get(i).setO3(GetFakeChange(aqiCircles.get(i).getO3(), 0, 500));
            aqiCircles.get(i).setSo2(GetFakeChange(aqiCircles.get(i).getSo2(), 0, 500));
            aqiCircles.get(i).setTemp(GetFakeChange(aqiCircles.get(i).getTemp(), 26, 32));

            drawCircle(aqiCircles.get(i).getPos(), GetAQIColor(aqiCircles.get(i).getAqi()));
        }

        UpdateInfoWindows();
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


    int GetAQIColor(float aqi) {
        int color;

        if(aqi > 300)
            color = Color.argb(100, 126, 0, 35);
        else if(aqi > 200)
            color = Color.argb(100, 153, 0, 76);
        else if(aqi > 150)
            color = Color.argb(100, 255, 0, 0);
        else if(aqi > 100)
            color = Color.argb(100, 255, 126, 0);
        else if(aqi > 50)
            color = Color.argb(100, 255, 255, 0);
        else
            color = Color.argb(100, 0, 228, 0);

        return color;
    }


    void UpdateInfoWindows() {
        for(int i = 0; i < aqiCircles.size(); i++) {
            AQICircle aqiCircle = aqiCircles.get(i);
            String text =  Float.toString(aqiCircle.getAqi());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(aqiCircle.getPos())
                    .draggable(false)
                    .icon(createPureTextIcon(text));
            map.addMarker(markerOptions);

            if(aqiCircle.getShowOnMap() == true) {

                String ssn = Integer.toString(aqiCircle.getSsn());
                String aqi = Float.toString(aqiCircle.getAqi());
                String co = Float.toString(aqiCircle.getCo());
                String o3 = Float.toString(aqiCircle.getO3());
                String no2 = Float.toString(aqiCircle.getNo2());
                String so2 = Float.toString(aqiCircle.getSo2());
                String temp = Float.toString(aqiCircle.getTemp());
                Marker m =  map.addMarker(new MarkerOptions()
                        .alpha(0.0f)
                        .infoWindowAnchor(.6f,1.0f)
                        .position(aqiCircle.getPos())
                        .title("Sensor Info")
                        .snippet("Sensor Num: " + ssn + "\n" +
                                "Air Quality: " + aqi + "\n" +
                                "CO: " + co + "\n" +
                                "O3: " + o3 + "\n" +
                                "NO2: " + no2 + "\n" +
                                "SO2: " + so2 + "\n" +
                                "Temp: " + temp + "℃" + "\n"));

                m.showInfoWindow();
            }
        }
    }


    public BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint(); // Adapt to your needs
        textPaint.setTextSize(50);

        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) (textHeight);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        // For development only:
        // Set a background in order to see the
        // full size and positioning of the bitmap.
        // Remove that for a fully transparent icon.
        canvas.drawColor(Color.alpha(0));

        canvas.drawText(text, 0, 0, textPaint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOnDestroy = true;
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}


class AQICircle {

    LatLng pos;
    boolean showOnMap = false;
    CircleOptions circle;
    int ssn;
    float aqi;
    float co;
    float o3;
    float no2;
    float so2;
    float temp;


    public CircleOptions getCircle() {
        return circle;
    }

    public void setCircle(CircleOptions circle) {

        this.circle = circle;
    }

    public void SetValues(int _ssn, float _aqi, float _co, float _o3, float _no2, float _so2, float _temp) {
        ssn = _ssn;
        aqi = _aqi;
        co = _co;
        o3 = _o3;
        no2 = _no2;
        so2 = _so2;
        temp = _temp;
    }

    public LatLng getPos() {
        return pos;
    }

    public boolean getShowOnMap() {
        return showOnMap;
    }

    public int getSsn() {
        return ssn;
    }

    public float getAqi() {
        return aqi;
    }

    public float getCo() {
        return co;
    }

    public float getO3() {
        return o3;
    }

    public float getNo2() {
        return no2;
    }

    public float getSo2() {
        return so2;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setPos(LatLng pos) {
        this.pos = pos;
    }

    public void setShowOnMap(boolean showOnMap) {
        this.showOnMap = showOnMap;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public void setAqi(float aqi) {
        this.aqi = aqi;
    }

    public void setCo(float co) {
        this.co = co;
    }

    public void setO3(float o3) {
        this.o3 = o3;
    }

    public void setNo2(float no2) {
        this.no2 = no2;
    }

    public void setSo2(float so2) {
        this.so2 = so2;
    }
}