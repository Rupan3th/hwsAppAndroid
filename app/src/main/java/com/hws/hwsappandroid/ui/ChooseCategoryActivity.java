package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.lookout.LookoutViewModel;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

public class ChooseCategoryActivity extends AppCompatActivity implements ItemClickListener {

    boolean displayMode = true;
    private String searchWord="";
    RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private ChooseCategoryModel model;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        model = new ViewModelProvider(this).get(ChooseCategoryModel.class);
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

        recyclerView = findViewById(R.id.recyclerView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, true, 1);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        model.loadData();
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {

                } else if (!recyclerView.canScrollVertically(1)) {
                    model.loadMoreGoods();
                } else {

                }
            }
        });

        ImageButton display_mode = findViewById(R.id.btn_display_mode);
        display_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(displayMode) {
                    display_mode.setImageResource(R.drawable.btn_display_sgl);
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    mAdapter = new RecyclerViewAdapter(getApplicationContext(), true, 2);
                    recyclerView.setAdapter(mAdapter);
                    displayMode = false;
                }else {
                    display_mode.setImageResource(R.drawable.btn_display_dbl);
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    mAdapter = new RecyclerViewAdapter(getApplicationContext(), true, 1);
                    recyclerView.setAdapter(mAdapter);
                    displayMode = true;
                }
                mAdapter.setClickListener(ChooseCategoryActivity.this);
                model.getGoods().observe(ChooseCategoryActivity.this, goods -> {
                    mAdapter.setData(goods);
                });
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(ChooseCategoryActivity.this, ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String good_name = productInfo.goodsName;
        String good_sn = productInfo.goodsSn;

        detailProduct.putExtra("good_sn", good_sn);
        startActivity(detailProduct);

    }
}