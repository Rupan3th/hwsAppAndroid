package com.hws.hwsappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectLangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_select_lang);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        SharedPreferences.Editor editor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
        editor.putString("Lang", "");
        editor.apply();

        LinearLayout selectChinese = findViewById(R.id.select_chinese);
        LinearLayout selectKorean = findViewById(R.id.select_korean);
        LinearLayout selectEnglish = findViewById(R.id.select_english);

        selectChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectChinese.setBackgroundResource(R.drawable.select_lang);
                selectKorean.setBackground(null);
                selectEnglish.setBackground(null);
//                startActivity(new Intent(SelectLangActivity.this, MainActivity.class));
//                SelectLangActivity.this.finish();

                SharedPreferences.Editor editor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
                editor.putString("Lang", "");
                editor.apply();
            }
        });

        selectKorean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectKorean.setBackgroundResource(R.drawable.select_lang);
                selectChinese.setBackground(null);
                selectEnglish.setBackground(null);
//                startActivity(new Intent(SelectLangActivity.this, WeiXinChatActivity.class));
//                SelectLangActivity.this.finish();

                SharedPreferences.Editor editor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
                editor.putString("Lang", "ko");
                editor.apply();
            }
        });

        selectEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEnglish.setBackgroundResource(R.drawable.select_lang);
                selectKorean.setBackground(null);
                selectChinese.setBackground(null);
//                startActivity(new Intent(SelectLangActivity.this, VerifyPhoneActivity.class));
//                SelectLangActivity.this.finish();

                SharedPreferences.Editor editor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
                editor.putString("Lang", "en");
                editor.apply();
            }
        });

        Button goto_main = findViewById(R.id.goto_main);
        goto_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLangActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}