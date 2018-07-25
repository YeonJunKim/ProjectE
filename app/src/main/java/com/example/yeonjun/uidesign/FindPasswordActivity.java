package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPasswordActivity extends AppCompatActivity {

    EditText idEditText;
    EditText emailEditText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        idEditText = findViewById(R.id.idEditText);
        emailEditText = findViewById(R.id.emailEditText);
        submitButton = (Button)findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. check if its empty
                if(idEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Id is empty", getApplicationContext());
                    return;
                }

                if(emailEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Email is empty", getApplicationContext());
                    return;
                }

                // 2. check if the e-mail input is a e-mail format
                if(MySingletone.getInstance().isEmailValid(emailEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("Email is not valid", getApplicationContext());
                    return;
                }

                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        AuthenticationActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
    }



}