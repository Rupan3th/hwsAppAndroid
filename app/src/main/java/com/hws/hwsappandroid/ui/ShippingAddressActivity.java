package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

public class ShippingAddressActivity extends AppCompatActivity implements ItemClickListener {

    RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private ShippingAddressModel model;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);
        model = new ViewModelProvider(this).get(ShippingAddressModel.class);
        model.loadData();

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(staggeredGridLayoutManager);
//        mAdapter = new RecyclerViewAdapter(this, true, 3);
//        mAdapter.setClickListener(this);
//        recyclerView.setAdapter(mAdapter);

        LinearLayout no_result_area = findViewById(R.id.no_result_area);
        no_result_area.setVisibility(View.VISIBLE);

        model.getAddress().observe(this, shippingAddress -> {
//            mAdapter.setDataAdr(shippingAddress);
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent i = new Intent(ShippingAddressActivity.this, OrderDetailsActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
//        String good_name = productInfo.goodsName;
//        String good_sn = productInfo.goodsSn;
//
//        i.putExtra("good_sn", good_sn);
        startActivity(i);

    }
}