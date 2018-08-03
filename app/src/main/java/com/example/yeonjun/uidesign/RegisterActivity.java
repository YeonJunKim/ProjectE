package com.example.yeonjun.uidesign;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.nio.file.FileAlreadyExistsException;

public class RegisterActivity extends AppCompatActivity {

    EditText idEditText;
    EditText fnameEditText;
    EditText lnameEditText;
    EditText emailEditText;
    EditText pwEditText;
    EditText confirmPwEditText;

    Button verifyButton;
    Button submitButton;

    boolean idVerified = false; // is id duplicate check done?
    boolean idValid = false;    // is id in id format? (no special letters or space)
    boolean emailValid = false;    // is email in email format?
    boolean pwValid = false;
    boolean confirmPwValid = false;
    boolean fnameValid = false;
    boolean lnameValid = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MySingletone.getInstance().HideProgressBar();
            switch (msg.what){
                case StatusCode.SUCCESS:
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AuthenticationActivity.class);
                    intent.putExtra("name", "register");
                    intent.putExtra("email", emailEditText.getText().toString());
                    startActivity(intent);
                    break;
                case StatusCode.FAILED:
                    MySingletone.getInstance().ShowToastMessage("E-mail already registered!", getApplicationContext());
                    break;
                case StatusCode.NOT_DUPLICATE_ID:
                    idVerified = true;
                    MySingletone.getInstance().ShowToastMessage("Verify Succeed!", getApplicationContext());
                    break;
                case StatusCode.DUPLICATE_ID:
                    idVerified = false;
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
        confirmPwEditText = findViewById(R.id.pwConfirmEditText);

        verifyButton = findViewById(R.id.verifyButton);
        submitButton = findViewById(R.id.submitButton);


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Verify button click
                if(!idVerified){
                    if(!idValid) {
                        MySingletone.getInstance().ShowToastMessage("id is not valid", getApplicationContext());
                        return;
                    }


                    // SGU - DRQ : id duplication Check Request
                    // <--------------------------------
                    MySingletone.getInstance().ShowProgressBar(RegisterActivity.this);
                    new CheckDuplicationTask(mHandler).execute(StatusCode.TYPE_ID, idEditText.getText().toString());


                    return;
                }
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // on Submit button click
                if(!idValid){
                    MySingletone.getInstance().ShowToastMessage("id is not valid", getApplicationContext());
                    return;
                }

                if(!emailValid){
                    MySingletone.getInstance().ShowToastMessage("email is not valid", getApplicationContext());
                    return;
                }

                if(!fnameValid || !lnameValid){
                    MySingletone.getInstance().ShowToastMessage("name is not valid", getApplicationContext());
                    return;
                }

                if(!pwValid || !confirmPwValid){
                    MySingletone.getInstance().ShowToastMessage("password is not valid", getApplicationContext());
                    return;
                }

                if(emailEditText.getText().length() == 0) {
                    MySingletone.getInstance().ShowToastMessage("Email is empty", getApplicationContext());
                    return;
                }

                if(!idVerified) {
                    MySingletone.getInstance().ShowToastMessage("click verify first!", getApplicationContext());
                    return;
                }



                // SGU - REG : Sign-up Request
                // <-----------------------------------------

                // <--------------------------------
                MySingletone.getInstance().ShowProgressBar(RegisterActivity.this);
                new SignUpTask(mHandler).execute(idEditText.getText().toString(),
                                                 pwEditText.getText().toString(),
                                                 emailEditText.getText().toString(),
                                                 fnameEditText.getText().toString(),
                                                 lnameEditText.getText().toString());

            }
        });


        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                idValid = false;
                idVerified = false;
                if(!MySingletone.getInstance().isNoSpaceBar(idEditText.getText())) {
                    idEditText.setError("spaces not allowed");
                }
                else if (!MySingletone.getInstance().isOnlyLetterAndDigit(idEditText.getText())) {
                    idEditText.setError("letters and numbers only");
                }
                else if(idEditText.getText().length() < 6) {
                    idEditText.setError("at least 6 letters");
                }
                else if(idEditText.getText().length() > 16) {
                    idEditText.setError("too long");
                }
                else {
                    idEditText.setError(null);
                    idValid = true;
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


        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                emailValid = false;
                if(MySingletone.getInstance().isEmailValid(emailEditText.getText()) == false) {  // Check if the id is empty
                    emailEditText.setError("not a email format");
                }
                else {
                    emailEditText.setError(null);
                    emailValid = true;
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


        fnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                fnameValid = false;
                if(!MySingletone.getInstance().isNoSpaceBar(fnameEditText.getText())) {
                    fnameEditText.setError("spaces not allowed");
                }
                else if (!MySingletone.getInstance().isOnlyLetters(fnameEditText.getText())) {
                    fnameEditText.setError("letters only");
                }
                else if(fnameEditText.getText().length() < 1) {
                    fnameEditText.setError("at least 1 letter");
                }
                else if(fnameEditText.getText().length() > 16) {
                    fnameEditText.setError("too long");
                }
                else {
                    fnameEditText.setError(null);
                    fnameValid = true;
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

        lnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                lnameValid = false;
                if(!MySingletone.getInstance().isNoSpaceBar(lnameEditText.getText())) {
                    lnameEditText.setError("spaces not allowed");
                }
                else if (!MySingletone.getInstance().isOnlyLetters(lnameEditText.getText())) {
                    lnameEditText.setError("letters only");
                }
                else if(lnameEditText.getText().length() < 1) {
                    lnameEditText.setError("at least 1 letter");
                }
                else if(lnameEditText.getText().length() > 16) {
                    lnameEditText.setError("too long");
                }
                else {
                    lnameEditText.setError(null);
                    lnameValid = true;
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
