package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IdCancellationActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText pwEditText;
    Button submitButton;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MySingletone.getInstance().HideProgressBar();
            switch (msg.what){
                case StatusCode.SUCCESS:
                    MySingletone.getInstance().ShowToastMessage("Check your email", getApplicationContext());
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AuthenticationActivity.class);
                    intent.putExtra("name", "idCancellation");
                    intent.putExtra("email", emailEditText.getText().toString());
                    startActivity(intent);
                    break;
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("email or password not match", getApplicationContext());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_cancellation);

        emailEditText = findViewById(R.id.emailEditText);
        pwEditText = findViewById(R.id.pwEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                // 1. check if email line is empty
                if(emailEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Email is empty", getApplicationContext());
                    return;
                }

                // 2. check if the e-mail input is a e-mail format
                if(MySingletone.getInstance().isEmailValid(emailEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("Email is not valid", getApplicationContext());
                    return;
                }

                // 3. check password is empty
                if(pwEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("password is empty!", getApplicationContext());
                    return;
                }


                // IDC - REG : ID Cancellation Request
                // <--------------------------------------
                MySingletone.getInstance().ShowProgressBar(IdCancellationActivity.this);
                new CancellationTask(mHandler, getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE))
                        .execute(emailEditText.getText().toString(), pwEditText.getText().toString());
            }
        });
    }
}
