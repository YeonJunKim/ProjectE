package com.example.yeonjun.uidesign;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTransfer extends AsyncTask<String, String, String>{
    private Context context;
    private Handler handler;

    public HttpTransfer(Context context, Handler handler) {
        super();
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... messages) {
        try {
            URL url = new URL(messages[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");

            OutputStream os = conn.getOutputStream();
            Log.i("OUTPUT", messages[1]);
            os.write(messages[1].getBytes("UTF-8"));
            os.flush();
            os.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String response;
                StringBuilder result = new StringBuilder();
                while((response = buffer.readLine()) != null) {
                    result.append(response);
                    Log.i("INPUT", messages.toString());
                }
                return result.toString();
            }
        }
        catch (Exception e){
            Log.d("ERROR : ", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
       handler.sendMessage(Message.obtain(handler,1));
    }
}