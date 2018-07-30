package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText currentPwEditText;
    EditText newPwEditText;
    EditText confirmPwEditText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPwEditText = findViewById(R.id.currentPwEditText);
        newPwEditText = findViewById(R.id.newPwEditText);
        confirmPwEditText = findViewById(R.id.confirmPwEditText);
        submitButton = findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                // 1. check if current pw line is empty
                if(currentPwEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("current pw is empty", getApplicationContext());
                    return;
                }

                // 2. check if new pw line is empty
                if(newPwEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("new pw is empty", getApplicationContext());
                    return;
                }

                // 3. check if confirm pw line is empty
                if(confirmPwEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("confirm pw is empty", getApplicationContext());
                    return;
                }

                // 4. check if current pw and new pw match
                if(currentPwEditText.getText().toString().contentEquals(newPwEditText.getText())){
                    MySingletone.getInstance().ShowToastMessage("new pw needs to be different!", getApplicationContext());
                    return;
                }

                // 5. check if new pw and confirm pw match
                if(newPwEditText.getText().toString().contentEquals(confirmPwEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("confirm password does not match!", getApplicationContext());
                    return;
                }


                // CUP-REQ : Change User Password Request
                // <------------------------------------------


                MySingletone.getInstance().ShowToastMessage("password changed!", getApplicationContext());
                // maybe re login because pw has changed?
                Intent intent = new Intent(
                        getApplicationContext(),
                        LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
