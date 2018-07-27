package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class FindPasswordActivity extends AppCompatActivity {

    EditText idEditText;
    EditText emailEditText;
    Button submitButton;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.SUCCESS:
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            AuthenticationActivity.class); // 다음 넘어갈 클래스 지정
                    intent.putExtra("name", "findPassword");
                    startActivity(intent); // 다음 화면으로 넘어간다
                    break;
                case StatusCode.FAILED:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        idEditText = findViewById(R.id.idEditText);
        emailEditText = findViewById(R.id.emailEditText);
        submitButton = (Button)findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                // 1. check if id line is empty
                if(idEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Id is empty", getApplicationContext());
                    return;
                }

                // 2. check if email line is empty
                if(emailEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Email is empty", getApplicationContext());
                    return;
                }

                // 3. check if the e-mail input is a e-mail format
                if(MySingletone.getInstance().isEmailValid(emailEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("Email is not valid", getApplicationContext());
                    return;
                }

                // ask server if the "id and email" exists and matches
                // <-----------------------------------
                try {
                    JSONObject data = new JSONObject();
                    data.put("id", idEditText.getText().toString());
                    data.put("email", emailEditText.getText().toString());
                    new HttpTransfer(mHandler).execute(getString(R.string.testURL), data.toString());
                }
                catch (Exception e){
                    Log.i("ERROR", e.toString());
                }
            }
        });
    }



}