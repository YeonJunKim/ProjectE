package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONObject;

public class AuthenticationActivity extends AppCompatActivity {

    boolean isVerified = false;
    Button verifyButton;
    Button submitButton;
    EditText codeEditText;
    String previousActivity;
  
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.SUCCESS:
                    isVerified = true;
                    MySingletone.getInstance().ShowToastMessage("Verify Succeed!", getApplicationContext());
                    break;
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("Incorrect verification code", getApplicationContext());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        previousActivity = getIntent().getExtras().getString("name");

        verifyButton = (Button)findViewById(R.id.verifyButton);
        submitButton = (Button)findViewById(R.id.submitButton);
        codeEditText = findViewById(R.id.codeEditText);

        codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isVerified = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Verify button click
                if(isVerified == false){
                    if(codeEditText.getText().length() == 0) {  // Check if the code is empty
                        MySingletone.getInstance().ShowToastMessage("Code is Empty!", getApplicationContext());
                        return;
                    }

                    // UVC - REQ : User Verification Request
                    // <--------------------------------
                    if(previousActivity.contentEquals("findPassword"))
                        new VerficationTask(mHandler, getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE), StatusCode.FORGOT_PW_VERIFY)
                                .execute(getIntent().getStringExtra("email"), codeEditText.getText().toString());
                    else if(previousActivity.contentEquals("register"))
                        new VerficationTask(mHandler, getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE), StatusCode.REGISTER_VERIFY)
                                .execute(getIntent().getStringExtra("email"), codeEditText.getText().toString());
                    else if(previousActivity.contentEquals("idCancellation"))
                        new VerficationTask(mHandler, getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE), StatusCode.CANCEL_ID_VERIFY)
                                .execute(getIntent().getStringExtra("email"), codeEditText.getText().toString());
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                if(isVerified == false){
                    MySingletone.getInstance().ShowToastMessage("Click Verify First!", getApplicationContext());
                    return;
                }

                Intent intent;
                if(previousActivity.contentEquals("register")){
                    MySingletone.getInstance().ShowToastMessage("registration success!", getApplicationContext());

                    intent = new Intent(
                            getApplicationContext(),
                            LoginActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                }
                else if(previousActivity.contentEquals("idCancellation")){
                    MySingletone.getInstance().ShowToastMessage("id cancellation success!", getApplicationContext());
                    intent = new Intent(
                            getApplicationContext(),
                            LoginActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                }
                else if(previousActivity.contentEquals("findPassword")){
                    intent = new Intent(
                            getApplicationContext(),
                            ResetPasswordActivity.class);

                    startActivity(intent);
                }
            }
        });
    }
}
