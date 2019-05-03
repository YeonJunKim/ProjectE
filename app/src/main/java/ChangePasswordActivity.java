package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText currentPwEditText;
    EditText newPwEditText;
    EditText confirmPwEditText;
    Button submitButton;

    boolean newPwValid = false;
    boolean confirmPwValid = false;



    SharedPreferences sp;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MySingletone.getInstance().HideProgressBar();
            switch (msg.what){
                case StatusCode.SUCCESS:
                    MySingletone.getInstance().ShowToastMessage("password changed!", getApplicationContext());
                    // maybe re login because pw has changed?
                    Intent intent = new Intent(
                            getApplicationContext(),
                            LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("incorrect old password", getApplicationContext());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPwEditText = findViewById(R.id.currentPwEditText);
        newPwEditText = findViewById(R.id.newPwEditText);
        confirmPwEditText = findViewById(R.id.confirmPwEditText);
        submitButton = findViewById(R.id.submitButton);

        sp = getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                if(!newPwValid || !confirmPwValid) {
                    MySingletone.getInstance().ShowToastMessage("new password is not valid", getApplicationContext());
                    return;
                }


                // CUP-REQ : Change User Password Request
                // <------------------------------------------
                MySingletone.getInstance().ShowProgressBar(ChangePasswordActivity.this);
                new ChangePasswordTask(mHandler, sp).execute(currentPwEditText.getText().toString(),
                                                             newPwEditText.getText().toString()     );

            }
        });



        currentPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                newPwValid = false;
                if(currentPwEditText.getText().toString().contentEquals(newPwEditText.getText())) {
                    newPwEditText.setError("new password should be different");
                }
                else {
                    newPwEditText.setError(null);
                    newPwValid = true;
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


        newPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                newPwValid = false;
                if(!MySingletone.getInstance().isNoSpaceBar(newPwEditText.getText())) {
                    newPwEditText.setError("spaces not allowed");
                }
                else if(newPwEditText.getText().length() < 6) {
                    newPwEditText.setError("at least 6 letters");
                }
                else if(newPwEditText.getText().length() > 16) {
                    newPwEditText.setError("too long");
                }
                else if(currentPwEditText.getText().toString().contentEquals(newPwEditText.getText())) {
                    newPwEditText.setError("new password should be different");
                }
                else {
                    newPwEditText.setError(null);
                    newPwValid = true;
                }

                confirmPwValid = false;
                if(confirmPwEditText.getText().toString().contentEquals(newPwEditText.getText()) == false) {
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
                if(confirmPwEditText.getText().toString().contentEquals(newPwEditText.getText()) == false) {
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
