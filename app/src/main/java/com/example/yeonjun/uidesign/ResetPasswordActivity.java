package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    Button submitButton;
    EditText pwEditText;
    EditText pwConfirmEditText;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.SUCCESS:
                    MySingletone.getInstance().ShowToastMessage("password changed!", getApplicationContext());
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재 화면의 제어권자
                            LoginActivity.class); // 다음 넘어갈 클래스 지정
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        setContentView(R.layout.activity_reset_password);

        submitButton = (Button)findViewById(R.id.submitButton);
        pwEditText = findViewById(R.id.pwEditText);
        pwConfirmEditText = findViewById(R.id.pwConfirmEditText);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                if(pwEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("password is empty!", getApplicationContext());
                    return;
                }

                if(pwConfirmEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("confirm password is empty!", getApplicationContext());
                    return;
                }

                if(pwEditText.getText().toString().contentEquals(pwConfirmEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("confirm password does not match!", getApplicationContext());
                    return;
                }

                // ask server if the code is correct
                // <--------------------------------
                try {
                    JSONObject data = new JSONObject();
                    data.put("password", pwEditText.getText().toString());
                    new HttpTransfer(mHandler).execute(getString(R.string.testURL), data.toString());
                }
                catch (Exception e){
                    Log.i("ERROR", e.toString());
                }
            }
        });
    }
}
