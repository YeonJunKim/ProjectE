package com.example.yeonjun.uidesign;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

    SharedPreferences sp;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MySingletone.getInstance().HideProgressBar();
            switch (msg.what){
                case StatusCode.SUCCESS:
                    MySingletone.getInstance().ShowToastMessage("Login Success!", getApplicationContext());
                    Log.i("JADE-TOKEN ", sp.getString("token", null));
                    //Go to the first page of the app
                    Intent intent = new Intent(
                            getApplicationContext(),
                            MainActivity.class);
                    intent.putExtra("id", idEditText.getText().toString());
                    startActivity(intent);
                    break;
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("Login Failed!", getApplicationContext());
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
        idEditText.setText("ella123");
        pwEditText.setText("ella11");

        sp = getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE);

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
                MySingletone.getInstance().ShowProgressBar(LoginActivity.this);
                new SignInTask(mHandler, sp).execute(idEditText.getText().toString(), pwEditText.getText().toString());
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
