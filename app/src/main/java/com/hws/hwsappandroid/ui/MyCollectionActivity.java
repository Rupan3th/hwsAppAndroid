package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.MyCollectionListAdapter;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.hws.hwsappandroid.util.SwipeController;
import com.hws.hwsappandroid.util.SwipeControllerActions;

public class MyCollectionActivity extends AppCompatActivity implements ItemClickListener {

    RecyclerView recyclerView, recommended_products;
    private MyCollectionListAdapter mAdapter;
    private RecyclerViewAdapter GAdapter;
    private MyCollectionModel model;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    SwipeController swipeController = null;
    boolean isDel;

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

        setContentView(R.layout.activity_my_collection);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(MyCollectionModel.class);

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
        mAdapter = new MyCollectionListAdapter(this);
//        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        RelativeLayout no_favorites = findViewById(R.id.no_favorites);
        model.loadData();
        model.getFavoriteGoods().observe(this, favoriteGoods -> {
            if(favoriteGoods.size()>0) {
                mAdapter.setData(favoriteGoods);
                no_favorites.setVisibility(View.GONE);
            }
            else no_favorites.setVisibility(View.VISIBLE);
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {

                } else if (!recyclerView.canScrollVertically(1)) {
//                    model.loadMoreGoods();
                } else {

                }
            }
        });

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                isDel = false;
                model.delFavorite(mAdapter.models.get(position).goodsId);
                model.getResult().observe(MyCollectionActivity.this, result -> {
                    if(result.equals("success")){
                        if(!isDel) {
                            mAdapter.models.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                            isDel = true;
                            if(mAdapter.models.size() == 0)
                                no_favorites.setVisibility(View.VISIBLE);
                        }

                    };
                });
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c, "取消收藏", 0, 26);
            }
        });

        ////Goods
        recommended_products = findViewById(R.id.recommended_products);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recommended_products.setLayoutManager(staggeredGridLayoutManager);
        GAdapter = new RecyclerViewAdapter(this, true, 2);
        GAdapter.setClickListener(this);
        recommended_products.setAdapter(GAdapter);

        model.loadRecommendData();
        model.getGoods().observe(this, goods -> {
            GAdapter.setData(goods);
        });

        NestedScrollView mainScrollView = findViewById(R.id.mainScrollView);
        mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
//                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(MyCollectionActivity.this, ProductDetailActivity.class);
        String pkId = GAdapter.models.get(position).pkId;

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