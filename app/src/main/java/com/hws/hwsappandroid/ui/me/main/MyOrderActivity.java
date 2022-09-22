package com.hws.hwsappandroid.ui.me.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.ActivityMyOrderBinding;
import com.hws.hwsappandroid.util.SoftKeyboard;

public class MyOrderActivity extends AppCompatActivity {

    private ActivityMyOrderBinding binding;
    View decorView;
    private static final int[] TAB_TITLES = new int[]{R.string.all, R.string.wait_pay, R.string.wait_send, R.string.wait_get, R.string.completed};
    ViewPager viewPager;
    int tabIndex;
    SectionsPagerAdapter sectionsPagerAdapter;

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

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        binding = ActivityMyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        OrderViewModel.setKeyword("");
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        for(int i=0; i<tabs.getTabCount(); i++){
            TabLayout.Tab tab = tabs.getTabAt(i);
            tab.setCustomView(createCustomTabView(getResources().getString(TAB_TITLES[i]), 15, R.color.text_soft));
            if(i==0){
                setTabTextSize(tab, 16, R.color.purple_500, true);
                setTabImage(tab, View.VISIBLE);
            }
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabTextSize(tab, 16, R.color.purple_500, true);
                setTabImage(tab, View.VISIBLE);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabTextSize(tab, 15, R.color.text_soft, false);
                setTabImage(tab, View.INVISIBLE);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(5);
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tabIndex = extras.getInt("tab");
            viewPager.setCurrentItem(tabIndex);
//            sectionsPagerAdapter.getItem(tabIndex);
        }

        EditText edit_text_search = binding.editTextSearch;
        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = edit_text_search.getText().toString();
                    sectionsPagerAdapter.updateData(keyword);

                    return true;
                }
                return false;
            }
        });
    }
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//    }

    private View createCustomTabView(String tabText, int tabSizeSp, int textColor){

        View tabCustomView = getLayoutInflater().inflate(R.layout.order_tab_customview, null);
        TextView tabTextView = tabCustomView.findViewById(R.id.tabTV);
        tabTextView.setText(tabText);
        tabTextView.setTextSize(tabSizeSp);
        tabTextView.setTextColor(getResources().getColor(textColor));
        return tabCustomView;
    }

    private void setTabTextSize(TabLayout.Tab tab, int tabSizeSp, int textColor, boolean style){
        View tabCustomView = tab.getCustomView();
        if (tabCustomView == null) {
            int index = tab.getPosition();
            tab.setCustomView(createCustomTabView(getResources().getString(TAB_TITLES[index]), 15, R.color.text_soft));
            tabCustomView = tab.getCustomView();
        }
        TextView tabTextView = tabCustomView.findViewById(R.id.tabTV);

        tabTextView.setTextSize(tabSizeSp);
        tabTextView.setTextColor(getResources().getColor(textColor));
        if(style) tabTextView.setTypeface(Typeface.DEFAULT_BOLD);
        else tabTextView.setTypeface(Typeface.DEFAULT);
    }

    private void setTabImage(TabLayout.Tab tab, int visible){
        View tabCustomView = tab.getCustomView();
        if (tabCustomView == null) {
            int index = tab.getPosition();
            tab.setCustomView(createCustomTabView(getResources().getString(TAB_TITLES[index]), 15, R.color.text_soft));
            tabCustomView = tab.getCustomView();
        }
        ImageView select_ring = tabCustomView.findViewById(R.id.select_ring);
        select_ring.setVisibility(visible);
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

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            sectionsPagerAdapter.updateData("");
        }
    }
}