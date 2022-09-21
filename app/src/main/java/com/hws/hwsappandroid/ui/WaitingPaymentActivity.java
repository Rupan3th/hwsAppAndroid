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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ConfirmDialogView;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.OrderGoodsListAdapter;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class WaitingPaymentActivity extends AppCompatActivity implements ItemClickListener {
    public static Activity WPActivity;

    public String orderId="", orderCode;
    public WaitingPaymentModel model;
    public TextView payment_required, payment_required_point, remaining_time,client_Name, phone_Number, client_Address;
    public TextView shop_name, shippingFee, totalMoney, tvOrderCode, order_time, copyBtn;
    public ImageButton goto_edit_adr;
    public LinearLayout shop_info;
    public Button cancel_order, to_pay;
    public RecyclerView itemRecyclerView, recommended_products;
    public OrderGoodsListAdapter adapter;
    public RecyclerViewAdapter GAdapter;
    public String shopId = "";

    private Timer timer;
    private TimerTask timerTask;
    private int mIntLeftTime;
    final Handler handler = new Handler();
    private String mStrLeftTime, mPrice;
    public int Valid_day = 1;

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

        setContentView(R.layout.activity_waiting_payment);
        WPActivity = WaitingPaymentActivity.this;

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
                Intent SC = new Intent(WaitingPaymentActivity.this, ServiceMessagesActivity.class);
                SC.putExtra("shopId", shopId);
                startActivity(SC);
            }
        });

        payment_required = findViewById(R.id.payment_required);
        payment_required_point = findViewById(R.id.payment_required_point);
        remaining_time = findViewById(R.id.remaining_time);
        client_Name = findViewById(R.id.client_Name);
        phone_Number = findViewById(R.id.phone_Number);
        client_Address = findViewById(R.id.client_Address);
        shop_name = findViewById(R.id.shop_name);
        shippingFee = findViewById(R.id.shippingFee);
        totalMoney = findViewById(R.id.totalMoney);
        tvOrderCode = findViewById(R.id.orderCode);
        order_time = findViewById(R.id.order_time);
        itemRecyclerView = findViewById(R.id.order_goods_list);

        goto_edit_adr = findViewById(R.id.goto_edit_adr);
        shop_info = findViewById(R.id.shop_info);
        copyBtn = findViewById(R.id.copyBtn);

        cancel_order = findViewById(R.id.cancel_order);
        to_pay = findViewById(R.id.to_pay);

        model = new ViewModelProvider(this).get(WaitingPaymentModel.class);
        model.loadData(orderId);
        model.getOrderDetail().observe(this, orderDetail -> {

            mPrice = orderDetail.totalMoney;
            int idx = mPrice.indexOf(".");
            payment_required.setText(mPrice.substring(0, idx)+".");
            payment_required_point.setText(mPrice.substring(idx+1));

            client_Name.setText(orderDetail.consignee);
            phone_Number.setText(orderDetail.phone.substring(0,3)+"****"+orderDetail.phone.substring(7));
            client_Address.setText(orderDetail.address);

            shopId = orderDetail.shopId;
            shop_name.setText(orderDetail.shopName);
            shippingFee.setText("￥" + orderDetail.shippingFee);
            totalMoney.setText("￥" + orderDetail.totalMoney);
            tvOrderCode.setText(orderDetail.orderCode);
            orderCode = orderDetail.orderCode;
            order_time.setText(orderDetail.orderTime);

            itemRecyclerView.setHasFixedSize(true);
            itemRecyclerView.setNestedScrollingEnabled(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            itemRecyclerView.setLayoutManager(linearLayoutManager);

            adapter = new OrderGoodsListAdapter(this,false);
            itemRecyclerView.setAdapter(adapter);
            adapter.setData(orderDetail);

            SimpleDateFormat dateSet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = dateSet.format(new Date());
            try{
                Date startDate = dateSet.parse(date);
                Date endDate = dateSet.parse(orderDetail.orderTime);
                long secondOfEnd = endDate.getTime()/1000 + Valid_day*24*60*60;
                long secondOfStart = startDate.getTime()/1000;
                mIntLeftTime = (int) (secondOfEnd - secondOfStart);

            }catch (Exception e){}

            if(mIntLeftTime > 0) startTimer();
            else{
                remaining_time.setVisibility(View.INVISIBLE);
            }

        });

        shop_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pd = new Intent(WaitingPaymentActivity.this, StoreDetailsActivity.class);
                pd.putExtra("shopId", shopId);
                startActivity(pd);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(WaitingPaymentActivity.this, tvOrderCode.getText().toString());
                Toast.makeText(WaitingPaymentActivity.this, "copied "+ tvOrderCode.getText().toString(), Toast.LENGTH_SHORT).show();
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

        goto_edit_adr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WaitingPaymentActivity.this, ShippingAddressActivity.class);
                startActivityForResult(i, 0);
            }
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

        to_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WaitingPaymentActivity.this, MerchantCashierActivity.class);
                i.putExtra("orderId", orderId);
                i.putExtra("orderCode", orderCode);
                i.putExtra("totalPrice", mPrice);
                view.getContext().startActivity(i);
            }
        });

        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(0);
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

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 1000ms
        timer.schedule(timerTask, 0, 1000); //
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
//            mTxtLeftTime.setText("");
        }
    }

    public void initializeTimerTask() {
//        mIntLeftTime = 180;
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        mIntLeftTime--;
                        mStrLeftTime = CommonUtils.getLeftHour(WaitingPaymentActivity.this, mIntLeftTime);
                        remaining_time.setText(mStrLeftTime);
                        if (mIntLeftTime == 0) {
                            stopTimerTask();
                        }
                    }
                });
            }
        };
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

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            refreshView();
        }
    }

    public void refreshView() {
        SharedPreferences pref = getSharedPreferences("user_info", MODE_PRIVATE);
        String consignee = pref.getString("default_consignee", "");
        String default_phone = pref.getString("default_phone", "");
        String default_adr = pref.getString("default_adr", "");

        client_Name.setText(consignee);
        phone_Number.setText(default_phone.substring(0,3)+"****"+default_phone.substring(7));
        client_Address.setText(default_adr);
    }

    private void showConfirmDialog(int confirmType) {
        final Dialog confirmDialog = new Dialog(this);
        View view = new ConfirmDialogView(this, confirmType);

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
                CancelOrder(orderId);
            }
        });

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(view);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rect displayRectangle = new Rect();
        Window window = confirmDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        confirmDialog.getWindow().setLayout(CommonUtils.getPixelValue(this, 316), ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmDialog.show();
//        confirmDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void CancelOrder(String orderId){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("orderId", orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/cancelOrder" ;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                setResult(0);
                                finish();

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", "" + statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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