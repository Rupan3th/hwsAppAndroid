package com.hws.hwsappandroid.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.util.FlowLayout;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    String histories = "'털보네이터' 제임스 하든이 한 단계 더 진화했다. " +
            "31경기 연속 30+득점(역대 2위), 경기당 평균 36.0득점(역대 7위) 등 ";
    String[] search_histories = histories.split(" ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        EditText edit_text_home_collapsed = findViewById(R.id.edit_text_home_collapsed);

        Button btnCancel = findViewById(R.id.button_search_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setText("");
            }
        });

        FlowLayout search_history_list = findViewById(R.id.search_history_list);
        ArrayList<TextView> search_history_items = new ArrayList<TextView>();

        for (String s : search_histories) {
            TextView tv = new TextView(this);
            tv.setText(s);
            tv.setBackgroundResource(R.drawable.round_gray_solid);
            tv.setTextSize(15);
            tv.setIncludeFontPadding(false);
            tv.setPadding(50, 20, 50, 20);

            FlowLayout.MarginLayoutParams margin_params = new FlowLayout.MarginLayoutParams(
                    FlowLayout.MarginLayoutParams.WRAP_CONTENT, FlowLayout.MarginLayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(margin_params);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edit_text_home_collapsed.setText(s);
                    Intent i = new Intent(SearchActivity.this, ChooseCategoryActivity.class);
                    i.putExtra("searchWord", s);
                    startActivity(i);
                }
            });

            search_history_list.addView(tv);
            search_history_items.add(tv);

            MaterialButton btn_clear_history = findViewById(R.id.button_clear_history);
            btn_clear_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    search_history_list.removeAllViews();
                }
            });
        }
    }
}