package com.example.yeonjun.uidesign;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tasks extends AsyncTask<String, Void, Integer> {
    protected Handler handler;
    protected SharedPreferences sp;

    public Tasks(Handler handler){
        this.handler = handler;
    }

    public Tasks(Handler handler, SharedPreferences sp) {
        this.handler = handler;
        this.sp = sp;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result != null) {
            Message msg = handler.obtainMessage(result);
            handler.sendMessage(msg);
        }
    }
}

class SignInTask extends Tasks{
    public SignInTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            URL url = new URL("http://192.241.221.155:8081/api/user/login");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("id", strings[0]);
            data.put("pw", strings[1]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String reader = null;
                StringBuilder sb = new StringBuilder();
                while((reader = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", reader);
                    sb.append(reader);
                }

                JSONObject response = new JSONObject(sb.toString());
                if (response.getBoolean("correct")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", response.getString("token"));
                    editor.commit();
                    return StatusCode.SUCCESS;
                }
                else
                    return StatusCode.FAILED;
            }
        } catch (Exception e){
            Log.i("JADE-ERROR ", e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class CheckDuplicationTask extends Tasks{
    public CheckDuplicationTask(Handler handler) {
        super(handler);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            String type = strings[0] + "/";
            String value = strings[1];
            URL url = new URL("http://192.241.221.155:8081/api/user/check/" + type + value);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    sb.append(input);
                    Log.i("JADE-DUP-INPUT", input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if (!response.getBoolean("duplicate")){
                    return StatusCode.NOT_DUPLICATE_ID;
                }
                else
                    return StatusCode.DUPLICATE_ID;
            }

        } catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class SignUpTask extends Tasks{
    public SignUpTask(Handler handler) {
        super(handler);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            URL url = new URL("http://192.241.221.155:8081/api/email/send");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("id", strings[0]);
            data.put("password", strings[1]);
            data.put("email", strings[2]);
            data.put("fname", strings[3]);
            data.put("lname", strings[4]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String reader = null;
                StringBuilder sb = new StringBuilder();
                while((reader = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", reader);
                    sb.append(reader);
                }

                JSONObject response = new JSONObject(sb.toString());
                if (response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }
        } catch (Exception e){
            Log.i("JADE-ERROR ", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class VerficationTask extends Tasks{
    private int typeVerification;
    public VerficationTask(Handler handler, SharedPreferences sp, int type){
        super(handler, sp);
        this.typeVerification = type;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            String path = "http://192.241.221.155:8081/api/email/";
            if(typeVerification == StatusCode.REGISTER_VERIFY)
                path = path + "verify/" + strings[0] + "/" + strings[1];
            else if(typeVerification == StatusCode.FORGOT_PW_VERIFY)
                path = path + "verifyforforgottenpw/" + strings[0] + "/" + strings[1];
            else if(typeVerification == StatusCode.CANCEL_ID_VERIFY)
                path = path + "verifyforidcancellation/" + strings[0] + "/" + strings[1]
                        + "/" + sp.getString("token", null);

            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");


            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")) {
                    if (typeVerification == StatusCode.FORGOT_PW_VERIFY) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("token", response.getString("token"));
                        editor.commit();
                    }
                    else if(typeVerification == StatusCode.CANCEL_ID_VERIFY){
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("token");
                        editor.commit();
                    }
                    return StatusCode.SUCCESS;
                }
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class ChangePasswordTask extends Tasks{
    public ChangePasswordTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            String token = sp.getString("token", null);
            URL url = new URL("http://192.241.221.155:8081/api/user/changepw/" + token);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("oldPW", strings[0]);
            data.put("newPW", strings[1]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String reader = null;
                StringBuilder sb = new StringBuilder();
                while((reader = buffer.readLine()) != null) {
                    Log.i("JADE-INPUT", reader);
                    sb.append(reader);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }

        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class FindPasswordTask extends Tasks{
    public FindPasswordTask(Handler handler) {
        super(handler);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/email/sendforforgottenpw");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("id", strings[0]);
            data.put("email", strings[1]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    sb.append(input);
                    Log.i("JADE-INPUT", input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }

        } catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class ResetPasswordTask extends Tasks{
    public ResetPasswordTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/user/changepwforforgottenpw/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("newPW", strings[0]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    sb.append(input);
                    Log.i("JADE-INPUT", input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("token");
                    editor.commit();
                    return StatusCode.SUCCESS;
                }
                else
                    return StatusCode.FAILED;
            }
        } catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class CancellationTask extends Tasks{
    public CancellationTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/email/sendforidcancellation/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("email", strings[0]);
            data.put("password", strings[1]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }

        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class SensorRegisterTask extends Tasks{
    public SensorRegisterTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/sensor/insert/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("mac", strings[0]);
            data.put("name", strings[1]);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(StatusCode.SSN, response.getInt("ssn"));
                    editor.apply();
                    return StatusCode.REGIST_SENSOR;
                }
                else if(response.getString("message").equals("Duplicate MAC address")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(StatusCode.SSN, response.getInt("ssn"));
                    editor.apply();
                    return StatusCode.REGIST_SENSOR;
                }
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class SensorDeregisterTask extends Tasks{
    public SensorDeregisterTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/sensor/delete/"
                    +strings[0] + "/" + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("DELETE");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.DEREGIST_SENSOR;
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class SensorListTask extends Tasks{
    public SensorListTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/sensor/list/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(StatusCode.LIST_SENSOR, sb.toString());
                    editor.commit();
                    return StatusCode.RECEIVE_SENSOR_LIST;
                }
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class HistoricalAQITask extends Tasks{
    public HistoricalAQITask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/data/aqi/history/"
            +strings[0]+"/"+strings[1]+"/"+strings[2]+"/"+strings[3]+"/"+ sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-DUMMY", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(StatusCode.HISTROICAL_AQI, sb.toString());
                    editor.commit();
                    return StatusCode.GET_HISTORICAL_AQI;
                }
                else
                    return StatusCode.FAILED;
            }
        } catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class HeartDataTransferTask extends Tasks{
    public HeartDataTransferTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/data/heart/insert/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("heart_rate", Integer.valueOf(strings[0]));
            data.put("rr_interval", Float.valueOf(strings[1]));
            data.put("timestamp", MySingletone.getInstance().getTimestamp());
            data.put("lat", sp.getFloat("lat", 0));
            data.put("lng", sp.getFloat("lng", 0));

            Log.i("JADE-HEART_TRF", data.toString());

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class HistoricalHeartDataTask extends Tasks{
    public HistoricalHeartDataTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/data/heart/history/"
                    +strings[0]+"/"+strings[1]+"/"+strings[2]+"/"+strings[3]+"/" + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-HISTORICAL-HEART", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(StatusCode.HISTROICAL_HEART, sb.toString());
                    editor.commit();
                    return StatusCode.GET_HISTORICAL_HEART;
                }
                else
                    return StatusCode.FAILED;
            }
        } catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class HistoricalAPDTask extends Tasks{
    public HistoricalAPDTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/data/rawair/history/"
                    +strings[0]+"/"+strings[1]+"/"+strings[2]+"/"+strings[3]+"/"+ sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-HISTORICAL-APD", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(StatusCode.HISTROICAL_APD, sb.toString());
                    editor.commit();
                    return StatusCode.GET_HISTORICAL_APD;
                }
                else
                    return StatusCode.FAILED;
            }
        } catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class APDTransferTask extends Tasks{
    public APDTransferTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/data/rawair/insert/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("CO", Float.valueOf(strings[0]));
            data.put("NO2", Float.valueOf(strings[1]));
            data.put("SO2", Float.valueOf(strings[2]));
            data.put("O3", Float.valueOf(strings[3]));
            data.put("temperature", Float.valueOf(strings[4]));
            data.put("timestamp", MySingletone.getInstance().getTimestamp());
            data.put("lat", sp.getFloat(StatusCode.LATITUDE, 0));
            data.put("lng", sp.getFloat(StatusCode.LONGITUDE, 0));
            data.put("SSN",sp.getInt(StatusCode.SSN, -1));

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}

class AQITransferTask extends Tasks{
    public AQITransferTask(Handler handler, SharedPreferences sp) {
        super(handler, sp);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/data/aqi/insert/"
                    + sp.getString("token", null));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject data = new JSONObject();
            data.put("CO", Float.valueOf(strings[0]));
            data.put("NO2", Float.valueOf(strings[1]));
            data.put("SO2", Float.valueOf(strings[2]));
            data.put("O3", Float.valueOf(strings[3]));
            data.put("temperature", Float.valueOf(strings[4]));
            data.put("timestamp", MySingletone.getInstance().getTimestamp());
            data.put("lat", sp.getFloat(StatusCode.LATITUDE, 0));
            data.put("lng", sp.getFloat(StatusCode.LONGITUDE, 0));
            data.put("SSN",sp.getInt(StatusCode.SSN, -1));

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String input = null;
                StringBuilder sb = new StringBuilder();
                while((input = buffer.readLine()) != null){
                    Log.i("JADE-INPUT", input);
                    sb.append(input);
                }

                JSONObject response = new JSONObject(sb.toString());
                if(response.getBoolean("success"))
                    return StatusCode.SUCCESS;
                else
                    return StatusCode.FAILED;
            }
        }catch (Exception e){
            Log.i("JADE-ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
}