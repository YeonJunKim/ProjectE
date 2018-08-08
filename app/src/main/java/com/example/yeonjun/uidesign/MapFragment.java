package com.example.yeonjun.uidesign;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mapView;
    GoogleMap map;

    private static final LatLng QI_LAT_LNG = new LatLng(32.882511,-117.234557);
    private static final LatLng THE_VILLAGE_LAT_LNG = new LatLng(32.888600,-117.241925);


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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline) {
            }
        });
        map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

                Marker m =  map.addMarker(new MarkerOptions()
                        .alpha(0.0f)
                        .infoWindowAnchor(.6f,1.0f)
                        .position(circle.getCenter())
                        .title("Air Quality")
                        .snippet(Integer.toString(circle.getFillColor())));

                m.showInfoWindow();
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(QI_LAT_LNG, 15));
        map.addMarker(new MarkerOptions().position(QI_LAT_LNG).title("Qualcomm Institute"));


        List<LatLng> sourcePoints = new ArrayList<>();
        List<LatLng> sourcePoints2 = new ArrayList<>();
        List<LatLng> sourcePoints3 = new ArrayList<>();
        List<LatLng> sourcePoints4 = new ArrayList<>();
        List<LatLng> sourcePoints5 = new ArrayList<>();
        List<LatLng> sourcePoints6 = new ArrayList<>();
        List<LatLng> sourcePoints7 = new ArrayList<>();
        List<LatLng> sourcePoints8 = new ArrayList<>();
        List<LatLng> sourcePoints9 = new ArrayList<>();


        sourcePoints.add(THE_VILLAGE_LAT_LNG);
        sourcePoints.add(new LatLng(32.888728,-117.238900));
        sourcePoints.add(new LatLng(32.883424,-117.238601));
        sourcePoints.add(QI_LAT_LNG);


        sourcePoints2.add(new LatLng(32.891800,-117.240244));
        sourcePoints2.add(new LatLng(32.889357,-117.242639));
        sourcePoints2.add(new LatLng(32.886261,-117.243809));
        sourcePoints2.add(new LatLng(32.878802,-117.243860));
        sourcePoints2.add(new LatLng(32.876336,-117.243738));


        sourcePoints3.add(new LatLng(32.880196,-117.218748));
        sourcePoints3.add(new LatLng(32.881150,-117.220853));
        sourcePoints3.add(new LatLng(32.882369,-117.223720));
        sourcePoints3.add(new LatLng(32.882401,-117.226386));
        sourcePoints3.add(new LatLng(32.880256,-117.231720));
        sourcePoints3.add(new LatLng(32.880360,-117.233001));
        sourcePoints3.add(new LatLng(32.880084,-117.233589));
        sourcePoints3.add(new LatLng(32.880160,-117.235792));


        sourcePoints4.add(new LatLng(32.885606,-117.223066));
        sourcePoints4.add(new LatLng(32.887028,-117.227034));
        sourcePoints4.add(new LatLng(32.888289,-117.231589));
        sourcePoints4.add(new LatLng(32.890609,-117.237156));
        sourcePoints4.add(new LatLng(32.891744,-117.240035));
        sourcePoints4.add(new LatLng(32.893661,-117.240906));


        sourcePoints5.add(new LatLng(32.874549,-117.228829));
        sourcePoints5.add(new LatLng(32.879565,-117.228335));
        sourcePoints5.add(new LatLng(32.883456,-117.227924));
        sourcePoints5.add(new LatLng(32.886766,-117.227624));
        sourcePoints5.add(new LatLng(32.891097,-117.227260));
        sourcePoints5.add(new LatLng(32.896076,-117.226022));

        sourcePoints6.add(new LatLng(32.881308,-117.229273));
        sourcePoints6.add(new LatLng(32.879012,-117.229108));
        sourcePoints6.add(new LatLng(32.876857,-117.229817));
        sourcePoints6.add(new LatLng(32.876804,-117.232966));
        sourcePoints6.add(new LatLng(32.877018,-117.238221));
        sourcePoints6.add(new LatLng(32.875036,-117.238747));

        sourcePoints7.add(new LatLng(32.879723,-117.239827));
        sourcePoints7.add(new LatLng(32.879662,-117.241875));
        sourcePoints7.add(new LatLng(32.879615,-117.243560));
        sourcePoints7.add(new LatLng(32.878409,-117.243620));
        sourcePoints7.add(new LatLng(32.877233,-117.243619));
        sourcePoints7.add(new LatLng(32.875765,-117.243033));
        sourcePoints7.add(new LatLng(32.873055,-117.243416));


        sourcePoints8.add(new LatLng(32.880246,-117.243817));
        sourcePoints8.add(new LatLng(32.879058,-117.243795));
        sourcePoints8.add(new LatLng(32.877116,-117.243757));
        sourcePoints8.add(new LatLng(32.875548,-117.243584));
        sourcePoints8.add(new LatLng(32.873639,-117.243586));
        sourcePoints8.add(new LatLng(32.871445,-117.243490));
        sourcePoints8.add(new LatLng(32.870204,-117.241696));


        sourcePoints9.add(new LatLng(32.874528,-117.231498));
        sourcePoints9.add(new LatLng(32.874572,-117.233645));
        sourcePoints9.add(new LatLng(32.873504,-117.234155));
        sourcePoints9.add(new LatLng(32.872390,-117.234966));
        sourcePoints9.add(new LatLng(32.872038,-117.236884));
        sourcePoints9.add(new LatLng(32.872352,-117.237728));
        sourcePoints9.add(new LatLng(32.870270,-117.238120));
        sourcePoints9.add(new LatLng(32.867734,-117.237677));
        sourcePoints9.add(new LatLng(32.865838,-117.236860));
        sourcePoints9.add(new LatLng(32.863690,-117.236187));

//        List<LatLng> snappedPoints = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints, null, snappedPoints);
//
//        List<LatLng> snappedPoints2 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints2, null, snappedPoints2);
//
//        List<LatLng> snappedPoints3 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints3, null, snappedPoints3);
//
//        List<LatLng> snappedPoints4 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints4, null, snappedPoints4);
//
//        List<LatLng> snappedPoints5 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints5, null, snappedPoints5);
//
//        List<LatLng> snappedPoints6 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints6, null, snappedPoints6);
//
//        List<LatLng> snappedPoints7 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints7, null, snappedPoints7);
//
//        List<LatLng> snappedPoints8 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints8, null, snappedPoints8);
//
//        List<LatLng> snappedPoints9 = new ArrayList<>();
//        new GetSnappedPointsAsyncTask().execute(sourcePoints9, null, snappedPoints9);
    }


    private String buildRequestUrl(List<LatLng> trackPoints) {
        StringBuilder url = new StringBuilder();
        url.append("https://roads.googleapis.com/v1/snapToRoads?path=");

        for (LatLng trackPoint : trackPoints) {
            url.append(String.format("%8.5f", trackPoint.latitude));
            url.append(",");
            url.append(String.format("%8.5f", trackPoint.longitude));
            url.append("|");
        }
        url.delete(url.length() - 1, url.length());
        url.append("&interpolate=true");
        url.append(String.format("&key=%s", "AIzaSyDQjxe74N3br9GwUtr_ipCkOHpAz3pqQCE"));

        return url.toString();
    }


    private class GetSnappedPointsAsyncTask extends AsyncTask<List<LatLng>, Void, List<LatLng>> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected List<LatLng> doInBackground(List<LatLng>... params) {

            List<LatLng> snappedPoints = new ArrayList<>();

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(buildRequestUrl(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder jsonStringBuilder = new StringBuilder();

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    jsonStringBuilder.append(line);
                    jsonStringBuilder.append("\n");
                }

                JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());
                JSONArray snappedPointsArr = jsonObject.getJSONArray("snappedPoints");

                for (int i = 0; i < snappedPointsArr.length(); i++) {
                    JSONObject snappedPointLocation = ((JSONObject) (snappedPointsArr.get(i))).getJSONObject("location");
                    double lattitude = snappedPointLocation.getDouble("latitude");
                    double longitude = snappedPointLocation.getDouble("longitude");
                    snappedPoints.add(new LatLng(lattitude, longitude));
                    Log.i("REALLY", String.valueOf(i));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return snappedPoints;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            super.onPostExecute(result);

            DrawLineOnMap(result);
        }
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        circleOptions.clickable(true);
        // Adding the circle to the GoogleMap
        map.addCircle(circleOptions);

    }


    void DrawLineOnMap(List<LatLng> _points) {
        boolean b = true;

        for(int i = 0; i < _points.size()-1; i++) {
//                polyLineOptions.add(result.get(i));

            Random r = new Random();
            int randNum = r.nextInt(255 - 0) + 0;
            int randNum2 = r.nextInt(2 - 0) + 0;
            int randNum3 = r.nextInt(100 - 0) + 0;

            if(randNum3 < 5)
                b = !b;

            if(b == false) {
//                map.addPolyline(new PolylineOptions()
//                        .add(_points.get(i), _points.get(i + 1))
//                        .width(40)
//                        .color(Color.rgb(255, randNum, 0)))
//                .setClickable(true);

                map.addCircle(new CircleOptions()
                        .center(_points.get(i))
                        .radius(20)
                        .strokeColor(Color.rgb(255, randNum, 0))
                        .fillColor(Color.rgb(255, randNum, 0)))
                        .setClickable(true);
            }
            else {
//                map.addPolyline(new PolylineOptions()
//                        .add(_points.get(i), _points.get(i + 1))
//                        .width(40)
//                        .color(Color.rgb(0, 255, 0)))
//                        .setClickable(true);

                map.addCircle(new CircleOptions()
                        .center(_points.get(i))
                        .radius(20)
                        .strokeColor(Color.rgb(0, 255, 0))
                        .fillColor(Color.rgb(0, 255, 0)))
                        .setClickable(true);
            }

        }
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
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
