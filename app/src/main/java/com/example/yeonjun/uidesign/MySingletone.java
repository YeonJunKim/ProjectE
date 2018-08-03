package com.example.yeonjun.uidesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class MySingletone {
    private static final MySingletone ourInstance = new MySingletone();

    ProgressDialog dialog;

    public static MySingletone getInstance() {
        return ourInstance;
    }

    private MySingletone() {
    }

    public void ShowProgressBar (Context _context) {
        dialog = new ProgressDialog(_context);
        // make the progress bar cancelable
        dialog.setCancelable(true);
        // set a message text
        dialog.setMessage("Please wait..");
        // show it
        dialog.show();
    }

    public void HideProgressBar(){
        dialog.hide();
    }

    // 화면에 Toast 메세지 띄우는 함수
    public void ShowToastMessage(CharSequence message, Context _context) {
        Context context = _context;
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.show();
    }

    // 이메일이 유효한 이메일 형식인지 확인하는 함수
    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // string이 알파벳과 숫자로만 이루어져 있는지 확인하는 함수
    public boolean isOnlyLetterAndDigit(CharSequence source) {
        for (int i = 0; i < source.length(); i++) {
            char currentChar = source.charAt(i);
            if (!Character.isLetterOrDigit(currentChar)) {
                return false;
            }
        }
        return true;
    }

    // string이 알파벳으로만 이루어져 있는지
    public boolean isOnlyLetters(CharSequence source) {
        for (int i = 0; i < source.length(); i++) {
            char currentChar = source.charAt(i);
            if (!Character.isLetter(currentChar)) {
                return false;
            }
        }
        return true;
    }

    // string에 스페이스바가 없는지
    public boolean isNoSpaceBar(CharSequence source) {
        for (int i = 0; i < source.length(); i++) {
            char currentChar = source.charAt(i);
            if (Character.isSpaceChar(currentChar)) {
                return false;
            }
        }
        return true;
    }
}
