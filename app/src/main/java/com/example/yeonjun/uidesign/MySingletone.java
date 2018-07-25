package com.example.yeonjun.uidesign;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MySingletone {
    private static final MySingletone ourInstance = new MySingletone();

    public static MySingletone getInstance() {
        return ourInstance;
    }

    private MySingletone() {
    }

    // 화면에 Toast 메세지 띄우는 함수
    public void ShowToastMessage(CharSequence message, Context _context)
    {
        Context context = _context;
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        toast.show();
    }

    // 이메일이 유효한 이메일 형식인지 확인하는 함수
    public boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
