package com.example.yeonjun.uidesign;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    Button findPwButton;
    EditText idEditText;
    EditText pwEditText;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    MySingletone.getInstance().ShowToastMessage("Success", getApplicationContext());
                    break;
                default:
                    MySingletone.getInstance().ShowToastMessage("Error", getApplicationContext());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        findPwButton = (Button)findViewById(R.id.findPwButton);
        idEditText = findViewById(R.id.idEditText);
        pwEditText = findViewById(R.id.pwEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Login button click

                if(idEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("Id is empty", getApplicationContext());
                    return;
                }

                if(pwEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("Password is empty", getApplicationContext());
                    return;
                }


                // check id, password with the server
                // <-----------------------------------------
                try {
                    JSONObject data = new JSONObject();
                    data.put("id", pwEditText.getText().toString());
                    data.put("password", pwEditText.getText().toString());
                    new HttpTransfer(mHandler).execute(getString(R.string.testURL),data.toString());
                }
                catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }


                MySingletone.getInstance().ShowToastMessage("Login Success!", getApplicationContext());
                //Go to the first page of the app
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Register button click
                Intent intent = new Intent(
                        getApplicationContext(),
                        RegisterActivity.class);
                startActivity(intent);
            }
        });

        findPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Forgot Password? button click
                Intent intent = new Intent(
                        getApplicationContext(),
                        FindPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
