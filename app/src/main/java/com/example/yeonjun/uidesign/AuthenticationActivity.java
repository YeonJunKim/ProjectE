package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthenticationActivity extends AppCompatActivity {

    boolean isVerified = false;
    Button verifyButton;
    Button submitButton;
    EditText codeEditText;

    CharSequence previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        previousActivity = getIntent().getExtras().getString("name");

        verifyButton = (Button)findViewById(R.id.verifyButton);
        submitButton = (Button)findViewById(R.id.submitButton);
        codeEditText = findViewById(R.id.codeEditText);

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

                    isVerified = true;
                    MySingletone.getInstance().ShowToastMessage("Verify Succeed!", getApplicationContext());

                    return;
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
                if(previousActivity.toString().contentEquals("register")){
                    intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            LoginActivity.class); // 다음 넘어갈 클래스 지정

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent); // 다음 화면으로 넘어간다
                }
                else if(previousActivity.toString().contentEquals("findPassword")){
                    intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            ResetPasswordActivity.class); // 다음 넘어갈 클래스 지정

                    startActivity(intent); // 다음 화면으로 넘어간다
                }
            }
        });
    }
}
