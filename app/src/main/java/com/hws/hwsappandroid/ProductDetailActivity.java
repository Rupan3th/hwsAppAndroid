package com.hws.hwsappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hws.hwsappandroid.components.carouselview.CarouselView;
import com.hws.hwsappandroid.components.carouselview.ViewListener;
import com.hws.hwsappandroid.model.CourseModel;
import com.hws.hwsappandroid.ui.cart.ShoppingCartAssist;
import com.hws.hwsappandroid.ui.home.HomeRecyclerViewAdapter;

import com.hws.hwsappandroid.util.FlowLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    private String imageName="";
    private NestedScrollView mainScrollView;
    private LinearLayout toolbar_pdt_detail;
    RecyclerView recyclerView;
    private HomeRecyclerViewAdapter mAdapter;

    String product_option = "'털보네이터' 제임스 하든이 한 단계 더 진화했다. " +
            "31경기 연속 30+득점(역대 2위), 경기당 평균 36.0득점(역대 7위) 등 ";
    String[] product_options = product_option.split(" ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        imageName = intent.getStringExtra("imageName");

        ProductDetailModel model = new ViewModelProvider(this).get(ProductDetailModel.class);

        toolbar_pdt_detail = findViewById(R.id.toolbar_pdt_detail);
        mainScrollView = findViewById(R.id.ScrollView);
        mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
//                    Log.i(TAG, "Scroll DOWN");
                }
                if (scrollY < oldScrollY) {
//                    Log.i(TAG, "Scroll UP");
                }
                if (scrollY == 0) {
                    toolbar_pdt_detail.setVisibility(View.INVISIBLE);
                }
                if (scrollY >= 50) {
                    toolbar_pdt_detail.setVisibility(View.VISIBLE);
                }
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
//                    model.loadMoreGoods();
                }
            }
        });

        ImageButton backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ShoppingCartAssist.class);
                startActivity(i);
            }
        });

        Button addToCartBtn = findViewById(R.id.addToCartBtn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        // carousel
        CarouselView carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(0);
        model.loadData();
        model.getBanners().observe(this, banners -> {
            carouselView.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(int position) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    Picasso.get().load(banners.get(position).bannerPic).fit().centerCrop()
                            .into(imageView);
                    return imageView;
                }
            });
            carouselView.setPageCount(banners.size());
        });

        recyclerView = findViewById(R.id.imageRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new HomeRecyclerViewAdapter(getApplicationContext(), true);
        recyclerView.setAdapter(mAdapter);
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });

    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_buy_now);
        FlowLayout layout = bottomSheetDialog.findViewById(R.id.select_product_option);
        ArrayList<TextView> selectOptionList = new ArrayList<TextView>();

        for (String s : product_options) {
            TextView tv = new TextView(this);
            tv.setText(s);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundResource(R.drawable.round_gray_solid);
            tv.setTextSize(15);
            tv.setIncludeFontPadding(false);
            tv.setPadding(50, 20, 50, 20);

            FlowLayout.MarginLayoutParams margin_params = new FlowLayout.MarginLayoutParams(
                    FlowLayout.MarginLayoutParams.WRAP_CONTENT, FlowLayout.MarginLayoutParams.WRAP_CONTENT);
            margin_params.setMargins(50,50,50,50);
            tv.setLayoutParams(margin_params);

            layout.addView(tv);
            selectOptionList.add(tv);
        }

        for(int i=0; i<selectOptionList.size(); i++)
        {
            TextView itemTv = selectOptionList.get(i);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int j=0; j<selectOptionList.size(); j++)
                    {
                        TextView remindTv = selectOptionList.get(j);
                        remindTv.setTextColor(Color.BLACK);
                        remindTv.setBackgroundResource(R.drawable.round_gray_solid);
                    }
                    itemTv.setTextColor(Color.RED);
                    itemTv.setBackgroundResource(R.drawable.round_gray_red_border);
                }
            });
        }

        bottomSheetDialog.show();
    }

}