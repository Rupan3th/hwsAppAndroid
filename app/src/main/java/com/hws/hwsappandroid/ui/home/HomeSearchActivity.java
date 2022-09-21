package com.hws.hwsappandroid.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.ActivityHomeSearchBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.util.DbOpenHelper;
import com.hws.hwsappandroid.util.FlowLayout;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class HomeSearchActivity extends AppCompatActivity implements ItemClickListener {
    private DbOpenHelper mDbOpenHelper;
    ArrayList<String> search_history = new ArrayList<>();
    String edit_key_word;

    private ActivityHomeSearchBinding binding;

    ConstraintLayout search_layout;
    RecyclerView recyclerView;
    LinearLayout not_found;
    LinearLayout searchbar_home, keyword_area;
    EditText edit_text_home_collapsed;
    TextView keyword;
    ImageButton btn_display_mode;
    Button button_search, btnCancel;

    private RecyclerViewAdapter mAdapter;
    ArrayList<Good> searchGoods = new ArrayList<>();
    LinearLayout rootView;
    boolean displayMode = true;
    private int goods_size = 0;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    HomeViewModel model;

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

        binding = ActivityHomeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        model = new ViewModelProvider(this).get(HomeViewModel.class);
        search_layout = binding.searchLayout;
        searchbar_home = binding.searchbarHome;
        btn_display_mode = binding.btnDisplayMode;
        button_search = binding.buttonSearch;
        btnCancel = binding.buttonSearchCancel;

        search_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        getDatabase();

        ImageButton btnBack = binding.buttonBack;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout history_ctr = binding.historyCtr;
        FlowLayout search_history_list = binding.searchHistoryList;

        keyword_area = binding.keywordArea;
        keyword = binding.keyword;
        edit_text_home_collapsed = binding.editTextHomeCollapsed;
        not_found = binding.notFound;

        recyclerView = binding.recyclerView;
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, true, 1);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        Button closeKeyword = binding.closeKeyword;
        closeKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.setText("");

                history_ctr.setVisibility(View.VISIBLE);
                search_history_list.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);

                btn_display_mode.setVisibility(View.GONE);
                button_search.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                search_layout.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.setText("");

                history_ctr.setVisibility(View.VISIBLE);
                search_history_list.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);

            }
        });

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
                    edit_text_home_collapsed.setVisibility(View.INVISIBLE);
                    edit_key_word = s;
                    keyword.setText(s);
                    keyword_area.setVisibility(View.VISIBLE);
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(edit_text_home_collapsed.getWindowToken(), 0);

                    history_ctr.setVisibility(View.GONE);
                    search_history_list.setVisibility(View.GONE);

                    ///Search result Display
                    model.loadSearchGoods(s);
                    model.getGoods().observe(HomeSearchActivity.this, goods -> {
                        goods_size = goods.size();
                        if(goods.size() > 0){
                            if(!displayMode) search_layout.setBackgroundColor(getResources().getColor(R.color.windowBackground));
                            mAdapter.setData(goods);
                            recyclerView.setVisibility(View.VISIBLE);
                            not_found.setVisibility(View.GONE);
                            btn_display_mode.setVisibility(View.VISIBLE);
                            button_search.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            not_found.setVisibility(View.VISIBLE);
                            btn_display_mode.setVisibility(View.GONE);
                            button_search.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.VISIBLE);
                        }
                    });

                    mDbOpenHelper.deleteColumnSearchHistory(s);
                    mDbOpenHelper.insertSearchHistory(s);
                }
            });

            search_history_list.addView(tv);
            search_history_items.add(tv);
        }

        MaterialButton btn_clear_history = binding.buttonClearHistory;
        btn_clear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_history_list.removeAllViews();
                search_history.clear();
                mDbOpenHelper.deleteAllSearchHistory();
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });

        edit_text_home_collapsed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //Perform your Actions here.
                    doSearch();
                    handled = true;
                }
                return handled;
            }
        });

        searchbar_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.requestFocus();
                edit_text_home_collapsed.setText(edit_key_word);
                edit_text_home_collapsed.setSelection(edit_text_home_collapsed.getText().length());
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.showSoftInput(edit_text_home_collapsed, InputMethodManager.SHOW_IMPLICIT);

                btn_display_mode.setVisibility(View.GONE);
                button_search.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
            }
        });

        btn_display_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(displayMode && goods_size > 1) {
                    btn_display_mode.setImageResource(R.mipmap.btn_display_sgl);
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    search_layout.setBackgroundColor(getResources().getColor(R.color.windowBackground));
                    mAdapter = new RecyclerViewAdapter(getApplicationContext(), true, 2);
                    recyclerView.setAdapter(mAdapter);
                    displayMode = false;
                }else {
                    btn_display_mode.setImageResource(R.mipmap.btn_display_dbl);
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    search_layout.setBackgroundColor(getResources().getColor(R.color.white));
                    mAdapter = new RecyclerViewAdapter(getApplicationContext(), true, 1);
                    recyclerView.setAdapter(mAdapter);
                    displayMode = true;
                }
                mAdapter.setClickListener(HomeSearchActivity.this);
                model.getGoods().observe(HomeSearchActivity.this, goods -> {
                    mAdapter.setData(goods);
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        edit_text_home_collapsed.requestFocus();
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.showSoftInput(edit_text_home_collapsed, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(HomeSearchActivity.this, ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;
        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

    void doSearch () {
        String s = edit_text_home_collapsed.getText().toString();
        if(!s.equals("") && !s.equals(" ")){
            edit_text_home_collapsed.setText("");
            edit_text_home_collapsed.setVisibility(View.INVISIBLE);
            keyword.setText(s);
            edit_key_word = s;
            keyword_area.setVisibility(View.VISIBLE);
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(edit_text_home_collapsed.getWindowToken(), 0);

            binding.historyCtr.setVisibility(View.GONE);
            binding.searchHistoryList.setVisibility(View.GONE);

            ///Search result Display
            model.loadSearchGoods(s);
            model.getGoods().observe(HomeSearchActivity.this, goods -> {
                goods_size = goods.size();
                if(goods.size() > 0){
                    if(!displayMode) search_layout.setBackgroundColor(getResources().getColor(R.color.windowBackground));
                    mAdapter.setData(goods);
                    recyclerView.setVisibility(View.VISIBLE);
                    not_found.setVisibility(View.GONE);
                    btn_display_mode.setVisibility(View.VISIBLE);
                    button_search.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                }
                else{
                    recyclerView.setVisibility(View.GONE);
                    not_found.setVisibility(View.VISIBLE);
                    btn_display_mode.setVisibility(View.GONE);
                    button_search.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                }
            });
            //add history into DB
            boolean newKey = true;
            for (int i=0; i<search_history.size(); i++) {
                if(s.equals(search_history.get(i))) newKey = false;
            }
            if(newKey){
                mDbOpenHelper.insertSearchHistory(s);
            }
        }
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