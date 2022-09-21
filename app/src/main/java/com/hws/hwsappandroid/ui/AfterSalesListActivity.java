package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.RefundModel;
import com.hws.hwsappandroid.util.AfterSalesListAdapter;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.hws.hwsappandroid.util.SwipeController;

import java.util.ArrayList;

public class AfterSalesListActivity extends AppCompatActivity implements ItemClickListener {

    RecyclerView recyclerView;
    private AfterSalesListAdapter mAdapter;
    private AfterSalesListModel model;
    ImageView no_related_order;
    public ArrayList<RefundModel> RefundList = new ArrayList<>();

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

        setContentView(R.layout.activity_after_sales_list);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(AfterSalesListModel.class);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(5);
                finish();
            }
        });

        no_related_order = findViewById(R.id.no_related_order);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AfterSalesListAdapter(this);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        model.loadData();
        model.getRefundList().observe(this, refundList -> {
            if(refundList.size() > 0 )
            {
                RefundList = refundList;
                no_related_order.setVisibility(View.GONE);
            }
            mAdapter.setData(refundList);
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
    }

    @Override
    public void onClick(View view, int position) {
//        Intent i = new Intent(AfterSalesListActivity.this, ProductDetailActivity.class);
        Intent i = new Intent(AfterSalesListActivity.this, AfterSalesDetailActivity.class);
        String pkId = RefundList.get(position).pkId;
//        String pkId = RefundList.get(position).goodsId;

        i.putExtra("pkId", pkId);
        startActivity(i);

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