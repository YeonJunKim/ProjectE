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
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTransfer extends AsyncTask<JSONObject, String, String>{
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
    protected String doInBackground(JSONObject... messages) {
        try {
            URL url = new URL("http://10.0.2.2/test.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "x-www-form-urlencoded");

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(messages.toString());
            dos.flush();
            dos.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String response;
                StringBuilder result = new StringBuilder();
                while((response = buffer.readLine()) != null)
                    result.append(response);

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
