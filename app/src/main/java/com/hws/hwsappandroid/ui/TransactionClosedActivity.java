package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ConfirmDialogView;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.OrderGoodsListAdapter;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

public class TransactionClosedActivity extends AppCompatActivity implements ItemClickListener {

    public String orderId="";
    public WaitingPaymentModel model;

    public TextView client_Name, phone_Number, client_Address, shop_name, shippingFee, totalMoney, orderCode, order_time, client_info, payment_mode, payment_time, copyBtn;
    public LinearLayout shop_info;
    public Button add_to_cart;

    public RecyclerView itemRecyclerView, recommended_products;
    public OrderGoodsListAdapter adapter;
    public RecyclerViewAdapter GAdapter;

    public String shopId = "";

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

        setContentView(R.layout.activity_transaction_closed);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");


        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageButton service_center = findViewById(R.id.service_center);
        service_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SC = new Intent(TransactionClosedActivity.this, ServiceMessagesActivity.class);
                SC.putExtra("shopId", shopId);
                startActivity(SC);
            }
        });

        client_Name = findViewById(R.id.client_Name);
        phone_Number = findViewById(R.id.phone_Number);
        client_Address = findViewById(R.id.client_Address);
        shop_name = findViewById(R.id.shop_name);
        shippingFee = findViewById(R.id.shippingFee);
        totalMoney = findViewById(R.id.totalMoney);
        orderCode = findViewById(R.id.orderCode);
        order_time = findViewById(R.id.order_time);
        payment_mode = findViewById(R.id.payment_mode);
        payment_time = findViewById(R.id.payment_time);
        shop_info = findViewById(R.id.shop_info);
        copyBtn = findViewById(R.id.copyBtn);
        add_to_cart = findViewById(R.id.add_to_cart);
        itemRecyclerView = findViewById(R.id.order_complete_list);

        model = new ViewModelProvider(this).get(WaitingPaymentModel.class);
        model.loadData(orderId);
        model.getOrderDetail().observe(this, orderDetail -> {

            client_Name.setText(orderDetail.consignee);
            phone_Number.setText(orderDetail.phone.substring(0,3)+"****"+orderDetail.phone.substring(7));
            client_Address.setText(orderDetail.address);

            shopId = orderDetail.shopId;
            shop_name.setText(orderDetail.shopName);
            shippingFee.setText("￥" + orderDetail.shippingFee);
            totalMoney.setText("￥" + orderDetail.totalMoney);
            orderCode.setText(orderDetail.orderCode);
            order_time.setText(orderDetail.orderTime);
            if(orderDetail.payType == 1)
                payment_mode.setText(R.string.alipay_pay);
            else if(orderDetail.payType == 2)
                payment_mode.setText(R.string.wechat_pay);
            else if(orderDetail.payType == 3)
                payment_mode.setText(R.string.union_pay);
            else payment_mode.setText("其他");
            payment_time.setText(orderDetail.payTime);

            itemRecyclerView.setHasFixedSize(true);
            itemRecyclerView.setNestedScrollingEnabled(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            itemRecyclerView.setLayoutManager(linearLayoutManager);

            adapter = new OrderGoodsListAdapter(this,false);
            itemRecyclerView.setAdapter(adapter);
            adapter.setData(orderDetail);

        });

        shop_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SD = new Intent(TransactionClosedActivity.this, StoreDetailsActivity.class);
                SD.putExtra("shopId", shopId);
                startActivity(SD);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(TransactionClosedActivity.this, orderCode.getText().toString());
                Toast.makeText(TransactionClosedActivity.this, "copied "+ orderCode.getText().toString(), Toast.LENGTH_SHORT).show();
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

        NestedScrollView mainScrollView = findViewById(R.id.ScrollView);
        mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
//                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });


        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(this, ProductDetailActivity.class);
        String pkId = GAdapter.models.get(position).pkId;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

    private void showConfirmDialog() {
        final Dialog confirmDialog = new Dialog(this);
        View view = new ConfirmDialogView(this, 0);

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });

        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(view);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        confirmDialog.getWindow().setLayout(CommonUtils.getPixelValue(this, 316), ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmDialog.show();
//        confirmDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
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