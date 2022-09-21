package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.home.HomeRecyclerViewAdapter;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.squareup.picasso.Picasso;

public class StoreDetailsActivity extends AppCompatActivity implements ItemClickListener {
    private NestedScrollView mainScrollView;
    RecyclerView recyclerView;
    ImageButton contact_shop;
    private RecyclerViewAdapter mAdapter;
    StoreDetailsModel model;
    private String shopId = "";
    private int pageNum = 1;
    private int NextpageNum = 2;
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

        setContentView(R.layout.activity_store_details);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();
        shopId = intent.getStringExtra("shopId");

        model = new ViewModelProvider(this).get(StoreDetailsModel.class);

        ImageButton backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        contact_shop = findViewById(R.id.contact_shop);

        recyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, true, 2);
        recyclerView.setAdapter(mAdapter);

        model.loadData(shopId);
        model.getShopInfo().observe(this, shop -> {
            TextView storeName = findViewById(R.id.storeName);
            storeName.setText(shop.shopName);
            ImageView storeImg = findViewById(R.id.storeImg);
            Picasso.get()
                    .load(shop.shopLogoPic).fit().centerCrop()
                    .into(storeImg);
            TextView storeLocation = findViewById(R.id.storeLocation);
            String prov = shop.province;
            String city = shop.city;
            if(prov.substring(prov.length() - 1).equals("省")) prov = prov.substring(0, prov.length()-1);
            if(city.substring(city.length() - 1).equals("市")) city = city.substring(0, city.length()-1);
            storeLocation.setText(prov + city);

            contact_shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), MultiEmotionActivity.class);
                    i.putExtra("shopId", shop.pkId);
                    i.putExtra("shopName", shop.shopName);
                    i.putExtra("shopLogoPic", shop.shopLogoPic);
                    i.putExtra("bizClientId", shop.bizClientId);
                    i.putExtra("operatorId", shop.operatorId);
                    startActivity(i);
                }
            });
        });

        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
            pageNum = goods.size()/20 ;
        });
        mAdapter.setClickListener(this);

        mainScrollView = findViewById(R.id.ScrollView);
        mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    if(NextpageNum-pageNum <2){
                        model.loadMoreGoods(shopId, NextpageNum);
                        NextpageNum ++;
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(this, ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;
        String good_price = productInfo.price;

        detailProduct.putExtra("pkId", pkId);

        startActivity(detailProduct);

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