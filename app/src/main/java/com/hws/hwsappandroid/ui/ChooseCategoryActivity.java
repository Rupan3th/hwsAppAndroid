package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hws.hwsappandroid.R;

public class ChooseCategoryActivity extends AppCompatActivity {

    boolean displayMode = true;
    private String searchWord="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        Intent intent = getIntent();
        searchWord = intent.getStringExtra("searchWord");
        EditText edit_text_home_collapsed = findViewById(R.id.edit_text_home_collapsed);
        edit_text_home_collapsed.setText(searchWord);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton display_mode = findViewById(R.id.btn_display_mode);
        display_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(displayMode) {
                    display_mode.setImageResource(R.drawable.btn_display_sgl);
                    displayMode = false;
                }else {
                    display_mode.setImageResource(R.drawable.btn_display_dbl);
                    displayMode = true;
                }

            }
        });
    }
}