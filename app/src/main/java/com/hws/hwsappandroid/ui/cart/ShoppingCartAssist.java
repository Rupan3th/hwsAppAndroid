package com.hws.hwsappandroid.ui.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hws.hwsappandroid.MainActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SwipeSectionItemModel;
import com.hws.hwsappandroid.model.SwipeSectionModel;
import com.hws.hwsappandroid.model.UserCartItem;
import com.hws.hwsappandroid.ui.CartSettlementActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.MyGlobals;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.hws.hwsappandroid.util.SectionRecyclerViewSwipeAdapter;
import com.hws.hwsappandroid.util.ShoppingCartListAdapter;

import java.io.File;
import java.util.ArrayList;

public class ShoppingCartAssist extends AppCompatActivity implements ItemClickListener {

    private RecyclerViewType recyclerViewType = RecyclerViewType.LINEAR_VERTICAL;

    private RecyclerView recyclerView, recommended_products;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";
    ShoppingCartModel model;
    ShoppingCartListAdapter adapter;
    private RecyclerViewAdapter mAdapter;
    LinearLayout cartIsEmpty,bottomCtr, selectLine;

    ArrayList<UserCartItem> mShoppingCart = new ArrayList<>();
    TextView totalPrice, total_num;
    Button toSettleBtn;

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

        setContentView(R.layout.fragment_shopping_cart);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(ShoppingCartModel.class);
        MyGlobals.getInstance().set_format_Total_price();
        MyGlobals.getInstance().set_format_Total_num();

        cartIsEmpty = findViewById(R.id.cart_is_empty);
        selectLine = findViewById(R.id.select_line);
        bottomCtr = findViewById(R.id.bottomCtr);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomCtr.getLayoutParams();
        layoutParams.setMargins( 0 , 0 , 0 , 0 ) ;

        ImageButton backBtn = findViewById(R.id.button_back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button gotoHome = findViewById(R.id.gotoHome);
        gotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity MA = (MainActivity) MainActivity.MActivity;
                Intent intent = MA.getIntent();
                finishAffinity();
                startActivity(intent);
//                BottomNavigationView bottomNavigationView = MA.findViewById(R.id.nav_view);
//                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        });

        totalPrice = findViewById(R.id.totalPrice);
        total_num = findViewById(R.id.total_num);

        setUpRecyclerView();
        populateRecyclerView();

        recommended_products = findViewById(R.id.recommended_products);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recommended_products.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(this, true, 2);
        mAdapter.setClickListener(this);
        recommended_products.setAdapter(mAdapter);

        model.loadRecommendData();
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });

        model.getSelectedGoodsNum().observe(this, goodsNum -> {
            MyGlobals.getInstance().set_Total_num(goodsNum);
            toSettleBtn.setText("去结算(" + goodsNum + ")");
        });

        model.getSelectedTotalPrice().observe(this, t_Price -> {
            MyGlobals.getInstance().set_Total_price(t_Price);
            totalPrice.setText(String.format("%.2f", t_Price));
        });

        CheckBox checkbox_all = findViewById(R.id.checkbox_all);
        model.isAllSelected().observe(this, allSelected -> {
            checkbox_all.setChecked(allSelected);
        });

        toSettleBtn = findViewById(R.id.toSettleBtn);
        toSettleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShoppingCartAssist.this, CartSettlementActivity.class);
//                i.putExtra("searchWord", s);
                startActivity(i);
            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.sectioned_recycler_swipe);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void populateRecyclerView() {
        adapter = new ShoppingCartListAdapter(this);
        adapter.setShoppingCartModel(model);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
//                    toolbar.setVisibility(View.INVISIBLE);
                } else if (!recyclerView.canScrollVertically(1)) {

                } else {
//                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        model.loadData();
        model.getMyCart().observe(this, mCarts -> {
            if(mCarts.size()==0){
                cartIsEmpty.setVisibility(View.VISIBLE);
                selectLine.setVisibility(View.GONE);
                bottomCtr.setVisibility(View.GONE);
            }else {
                bottomCtr.setVisibility(View.VISIBLE);
                selectLine.setVisibility(View.VISIBLE);
                cartIsEmpty.setVisibility(View.GONE);
            }

            adapter.setData(mCarts);
            mShoppingCart = mCarts;
            MyGlobals.getInstance().setMyShoppingCart(mShoppingCart);

            int total_goods = 0;
            for(int i=0; i<mCarts.size(); i++){
                total_goods = total_goods + mCarts.get(i).goods.size();
            }
            total_num.setText(" " + total_goods);
            MyGlobals.getInstance().setNotify_cart(total_goods);
        });

        CheckBox checkbox_all = findViewById(R.id.checkbox_all);
        checkbox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                float t_price = 0.0f;
//                int t_num = 0;
                ArrayList<String> pkIds = new ArrayList<>();
                if(checkbox_all.isChecked()){
                    for(int i=0; i<mShoppingCart.size(); i++){
                        mShoppingCart.get(i).selected = true;
                        for(int j=0; j<mShoppingCart.get(i).goods.size(); j++){
                            mShoppingCart.get(i).goods.get(j).selected = true;
//                            t_price =  t_price+ Float.parseFloat(mShoppingCart.get(i).goods.get(j).goodsPrice)*mShoppingCart.get(i).goods.get(j).goodsNum;
//                            t_num = t_num + mShoppingCart.get(i).goods.get(j).goodsNum;
                            pkIds.add(mShoppingCart.get(i).goods.get(j).pkId);
                        }
                    }
                }
                else {
                    for(int i=0; i<mShoppingCart.size(); i++){
                        mShoppingCart.get(i).selected = false;
                        for(int j=0; j<mShoppingCart.get(i).goods.size(); j++){
                            mShoppingCart.get(i).goods.get(j).selected = false;
                            pkIds.add(mShoppingCart.get(i).goods.get(j).pkId);
                        }
                    }
//                    t_price = 0.0f;
//                    t_num = 0;
                }
                adapter.setData(mShoppingCart);
                model.updateCheckStatus(pkIds, checkbox_all.isChecked());
//                MyGlobals.getInstance().set_Total_price(t_price);
//                MyGlobals.getInstance().set_Total_num(t_num);
//                totalPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));
//                toSettleBtn.setText("去结算(" + MyGlobals.getInstance().getTotal_num() + ")");
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(ShoppingCartAssist.this, ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;
        String good_sn = productInfo.goodsSn;

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