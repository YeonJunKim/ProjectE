package com.example.yeonjun.uidesign;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
        Message msg = handler.obtainMessage(result);
        handler.sendMessage(msg);
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
                path += "verify/";
            else if(typeVerification == StatusCode.FORGOT_PW_VERIFY)
                path += "verifyforforgottenpw/";

            URL url = new URL(path + strings[0] + "/" + strings[1]);
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
    public CancellationTask(Handler handler) {
        super(handler);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try{
            URL url = new URL("http://192.241.221.155:8081/api/email/");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

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


