package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    EditText idEditText;
    EditText fnameEditText;
    EditText lnameEditText;
    EditText emailEditText;
    EditText pwEditText;
    EditText pwConfirmEditText;

    Button verifyButton;
    Button submitButton;

    boolean isVerified = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case StatusCode.SUCCESS:
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AuthenticationActivity.class);
                    intent.putExtra("name", "register");
                    startActivity(intent);
                    break;
                case StatusCode.FAILED:
                    break;
                case StatusCode.NOT_DUPLICATE_ID:
                    isVerified = true;
                    MySingletone.getInstance().ShowToastMessage("Verify Succeed!", getApplicationContext());
                    break;
                case StatusCode.DUPLICATE_ID:
                    isVerified = false;
                    MySingletone.getInstance().ShowToastMessage("Verify Failed!", getApplicationContext());
                    break;
                case StatusCode.NOT_DUPLICATE_MAIL:
                    break;
                case StatusCode.DUPLICATE_EMAIL:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idEditText = findViewById(R.id.idEditText);
        fnameEditText = findViewById(R.id.fnameEditText);
        lnameEditText = findViewById(R.id.lnameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        pwEditText = findViewById(R.id.pwEditText);
        pwConfirmEditText = findViewById(R.id.pwConfirmEditText);

        verifyButton = findViewById(R.id.verifyButton);
        submitButton = findViewById(R.id.submitButton);


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Verify button click
                if(isVerified == false){
                    if(idEditText.getText().length() == 0) {  // Check if the id is empty
                        MySingletone.getInstance().ShowToastMessage("id is Empty!", getApplicationContext());
                        return;
                    }

                    // SGU - DRQ : id duplication Check Request
                    // <--------------------------------
                    new CheckDuplicationTask(mHandler).execute(StatusCode.TYPE_ID, idEditText.getText().toString());


                    return;
                }
            }
        });

        idEditText.addTextChangedListener(new TextWatcher() {
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


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                // 1. check if id is verified
                if(isVerified == false) {
                    MySingletone.getInstance().ShowToastMessage("click verify first!", getApplicationContext());
                    return;
                }

                // 2. check fname is empty
                if(fnameEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("first name is empty", getApplicationContext());
                    return;
                }

                // 3. check lname is empty
                if(lnameEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("last name is empty", getApplicationContext());
                    return;
                }

                // 4. check if email line is empty
                if(emailEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Email is empty", getApplicationContext());
                    return;
                }

                // 5. check if the e-mail input is a e-mail format
                if(MySingletone.getInstance().isEmailValid(emailEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("Email is not valid", getApplicationContext());
                    return;
                }

                // 6. check password is empty
                if(pwEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("password is empty!", getApplicationContext());
                    return;
                }

                // 7. check confirm password is empty
                if(pwConfirmEditText.getText().length() == 0){
                    MySingletone.getInstance().ShowToastMessage("confirm password is empty!", getApplicationContext());
                    return;
                }

                // 8. check password and confirm password match
                if(pwEditText.getText().toString().contentEquals(pwConfirmEditText.getText()) == false){
                    MySingletone.getInstance().ShowToastMessage("confirm password does not match!", getApplicationContext());
                    return;
                }


                // SGU - REG : Sign-up Request
                // <--------------------------------
                new SignUpTask(mHandler).execute(idEditText.getText().toString(),
                                                 pwEditText.getText().toString(),
                                                 emailEditText.getText().toString(),
                                                 fnameEditText.getText().toString(),
                                                 lnameEditText.getText().toString());

            }
        });

    }
}
