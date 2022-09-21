package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hws.hwsappandroid.R;

public class KeypadActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    public EditText mCurEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setKeypad() {
        RelativeLayout btnNum0 = (RelativeLayout) findViewById(R.id.btn_num0);
        btnNum0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_0);
            }
        });
        RelativeLayout btnNum1 = (RelativeLayout) findViewById(R.id.btn_num1);
        btnNum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_1);
            }
        });
        RelativeLayout btnNum2 = (RelativeLayout) findViewById(R.id.btn_num2);
        btnNum2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_2);
            }
        });
        RelativeLayout btnNum3 = (RelativeLayout) findViewById(R.id.btn_num3);
        btnNum3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_3);
            }
        });
        RelativeLayout btnNum4 = (RelativeLayout) findViewById(R.id.btn_num4);
        btnNum4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_4);
            }
        });
        RelativeLayout btnNum5 = (RelativeLayout) findViewById(R.id.btn_num5);
        btnNum5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_5);
            }
        });
        RelativeLayout btnNum6 = (RelativeLayout) findViewById(R.id.btn_num6);
        btnNum6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_6);
            }
        });
        RelativeLayout btnNum7 = (RelativeLayout) findViewById(R.id.btn_num7);
        btnNum7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_7);
            }
        });
        RelativeLayout btnNum8 = (RelativeLayout) findViewById(R.id.btn_num8);
        btnNum8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_8);
            }
        });
        RelativeLayout btnNum9 = (RelativeLayout) findViewById(R.id.btn_num9);
        btnNum9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_9);
            }
        });
        RelativeLayout btnNumBackspace = (RelativeLayout) findViewById(R.id.btn_backspace);
        btnNumBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputKey(KeyEvent.KEYCODE_DEL);
            }
        });
    }

    public void inputKey(int keyCode) {
        if (mCurEditText == null) {
            return;
        }
        mCurEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
