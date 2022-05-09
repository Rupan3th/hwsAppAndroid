package com.hws.hwsappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SelectLangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lang);

        TextView selectChinese = findViewById(R.id.select_chinese);
        selectChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLangActivity.this, MainActivity.class));
                SelectLangActivity.this.finish();
            }
        });

        TextView selectKorean = findViewById(R.id.select_korean);
        selectKorean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLangActivity.this, MainActivity.class));
                SelectLangActivity.this.finish();
            }
        });
    }
}