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

public class HttpTransfer extends AsyncTask<String, String, Integer>{
    private Handler handler;
    private URL url;

    private static HttpTransfer httpTransfer = new HttpTransfer();

    private HttpTransfer() {    }

    public static HttpTransfer getHttpTransfer(){
        return httpTransfer;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... messages) {
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
                return StatusCode.SUCCESS;
            }
        }
        catch (Exception e){
            Log.d("ERROR : ", e.toString());
        }
        return StatusCode.FAILED;
    }

    @Override
    protected void onPostExecute(Integer statusCode) {
       handler.sendMessage(Message.obtain(handler, StatusCode.SUCCESS));
    }

    public void signIn(Handler handler, String path, String id, String pw){
        final Handler mHandler = handler;
        final String addr = path + id + "/" + pw;
        Thread th = new Thread(){
            @Override
            public void run() {
                try {
                    url = new URL(addr);
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("GET");
                    huc.connect();

                    if(huc.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream is = huc.getInputStream();
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                        String input;
                        StringBuilder sb = new StringBuilder();
                        while((input = buffer.readLine()) != null){
                            sb.append(input);
                            Log.i("RESPONSE", input);
                        }
                        JSONObject jobj = new JSONObject(sb.toString());
                        Log.i("RESPONSE : correct", jobj.get("correct").toString());
                        if(jobj.get("correct").toString().equals("true")){
                            mHandler.sendMessage(Message.obtain(mHandler, StatusCode.SUCCESS));
                        }
                        else{
                            mHandler.sendMessage(Message.obtain(mHandler, StatusCode.FAILED));
                        }
                    }
                } catch (Exception e){
                    Log.i("Error", e.toString());
                }
            }
        };
        th.run();
    }

}
