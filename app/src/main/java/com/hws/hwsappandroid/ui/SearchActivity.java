package com.hws.hwsappandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.util.DbOpenHelper;
import com.hws.hwsappandroid.util.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper;
    ArrayList<String> search_history = new ArrayList<>();

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

        setContentView(R.layout.activity_search);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        getDatabase();

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout keyword_area = findViewById(R.id.keyword_area);
        TextView keyword = findViewById(R.id.keyword);
        EditText edit_text_home_collapsed = findViewById(R.id.edit_text_home_collapsed);

        Button closeKeyword = findViewById(R.id.closeKeyword);
        closeKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.setText("");
            }
        });

        Button btnCancel = findViewById(R.id.button_search_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setText("");
                finish();
            }
        });

        FlowLayout search_history_list = findViewById(R.id.search_history_list);
        ArrayList<TextView> search_history_items = new ArrayList<TextView>();

        for (int i=0; i<search_history.size(); i++) {
            String s = search_history.get(i);
            TextView tv = new TextView(this);
            tv.setText(s);
            tv.setBackgroundResource(R.drawable.round_gray_solid_36);
            tv.setTextColor(Color.parseColor("#FF222222"));
            tv.setTextSize(12);
            tv.setIncludeFontPadding(false);
            tv.setPadding(50, 16, 50, 16);

            FlowLayout.MarginLayoutParams margin_params = new FlowLayout.MarginLayoutParams(
                    FlowLayout.MarginLayoutParams.WRAP_CONTENT, FlowLayout.MarginLayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(margin_params);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    edit_text_home_collapsed.setText(s);
//                    edit_text_home_collapsed.setVisibility(View.INVISIBLE);
//                    keyword.setText(s);
//                    keyword_area.setVisibility(View.VISIBLE);
                    Intent i = new Intent(SearchActivity.this, ChooseCategoryActivity.class);
                    i.putExtra("searchWord", s);
                    startActivity(i);

                    mDbOpenHelper.deleteColumnSearchHistory(s);
                    mDbOpenHelper.insertSearchHistory(s);
                }
            });

            search_history_list.addView(tv);
            search_history_items.add(tv);

        }

        MaterialButton btn_clear_history = findViewById(R.id.button_clear_history);
        btn_clear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_history_list.removeAllViews();
                search_history.clear();
                mDbOpenHelper.deleteAllSearchHistory();
            }
        });

        ImageButton button_search = findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = edit_text_home_collapsed.getText().toString();
                if(!s.equals("") && !s.equals(" ")){
                    edit_text_home_collapsed.setText("");
                    //add history into DB
                    boolean newKey = true;
                    for (int i=0; i<search_history.size(); i++) {
                        if(s.equals(search_history.get(i))) newKey = false;
                    }
                    if(newKey){
                        mDbOpenHelper.insertSearchHistory(s);
                    }

                    Intent i = new Intent(SearchActivity.this, ChooseCategoryActivity.class);
                    i.putExtra("searchWord", s);
                    startActivity(i);
                }
            }
        });
    }

    public void getDatabase(){
        Cursor iCursor = mDbOpenHelper.getSearchHistory();
        while(iCursor.moveToNext()){
            String keyword = iCursor.getString(1);
            search_history.add(keyword);
        }
        Collections.reverse(search_history);
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