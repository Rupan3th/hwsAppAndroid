package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.home.HomeRecyclerViewAdapter;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

public class StoreDetailsActivity extends AppCompatActivity implements ItemClickListener {
    private NestedScrollView mainScrollView;
    RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    StoreDetailsModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);

        model = new ViewModelProvider(this).get(StoreDetailsModel.class);

        ImageButton backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, true);
        recyclerView.setAdapter(mAdapter);

        model.loadData();
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });
        mAdapter.setClickListener(this);

        mainScrollView = findViewById(R.id.ScrollView);
        mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });


    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(this, ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String good_name = productInfo.goodsName;
        String good_sn = productInfo.goodsSn;
//        String imageName = courseModelArrayList.get(position-1).getCourse_name();
//        String productInfo = courseModelArrayList.get(position-1).getProductInfo();
//        String price = courseModelArrayList.get(position-1).getPrice();


//        detailImage.putExtra("imageName", imageName);

        startActivity(detailProduct);

    }
}