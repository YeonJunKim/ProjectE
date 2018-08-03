package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    Button submitButton;
    EditText pwEditText;
    EditText confirmPwEditText;

    boolean pwValid = false;
    boolean confirmPwValid = false;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MySingletone.getInstance().HideProgressBar();
            switch (msg.what){
                case StatusCode.SUCCESS:
                    MySingletone.getInstance().ShowToastMessage("password changed!", getApplicationContext());
                    Intent intent = new Intent(
                            getApplicationContext(),
                            LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("Error", getApplicationContext());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        submitButton = (Button)findViewById(R.id.submitButton);
        pwEditText = findViewById(R.id.pwEditText);
        confirmPwEditText = findViewById(R.id.pwConfirmEditText);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                if(!pwValid || !confirmPwValid){
                    MySingletone.getInstance().ShowToastMessage("password is not valid", getApplicationContext());
                    return;
                }

                // ask server if the code is correct
                // <--------------------------------
                MySingletone.getInstance().ShowProgressBar(ResetPasswordActivity.this);
                new ResetPasswordTask(mHandler, getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE))
                        .execute(pwEditText.getText().toString());

            }
        });


        pwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                pwValid = false;
                if(!MySingletone.getInstance().isNoSpaceBar(pwEditText.getText())) {
                    pwEditText.setError("spaces not allowed");
                }
                else if(pwEditText.getText().length() < 6) {
                    pwEditText.setError("at least 6 letters");
                }
                else if(pwEditText.getText().length() > 16) {
                    pwEditText.setError("too long");
                }
                else {
                    pwEditText.setError(null);
                    pwValid = true;
                }

                confirmPwValid = false;
                if(confirmPwEditText.getText().toString().contentEquals(pwEditText.getText()) == false) {
                    confirmPwEditText.setError("password is different");
                }
                else {
                    confirmPwEditText.setError(null);
                    confirmPwValid = true;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
            }
        });

        confirmPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                confirmPwValid = false;
                if(confirmPwEditText.getText().toString().contentEquals(pwEditText.getText()) == false) {
                    confirmPwEditText.setError("password is different");
                }
                else {
                    confirmPwEditText.setError(null);
                    confirmPwValid = true;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
            }
        });
    }
}


