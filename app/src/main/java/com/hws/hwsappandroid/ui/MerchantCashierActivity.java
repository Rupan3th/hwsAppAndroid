package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.OrderInfoVO;
import com.hws.hwsappandroid.model.SubmitOrder;
import com.hws.hwsappandroid.model.aoListItem;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.model.submitOrderGoodsInfo;
import com.hws.hwsappandroid.model.submitOrderInfo;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.MyGlobals;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.walnutlabs.android.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;



import cz.msebera.android.httpclient.Header;

public class MerchantCashierActivity extends AppCompatActivity implements DialogInterface.OnCancelListener, IWXAPIEventHandler {

    private OrderInfoVO orderInfoVO = new OrderInfoVO();
    private BuyNowModel model;
    String orderId, totalPrice = "", cartOrderCode, orderCode;
    int pay_type = 1;

    String packageValue, appId, sign, partnerId, prepayId, nonceStr, timeStamp;
    String preOrderNo;
    View decorView;

    ImageButton btnBack;

    private IWXAPI api;

    private static final int SDK_PAY_FLAG = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
//            Result result = new Result((String) msg.obj);
//            Toast.makeText(MerchantCashierActivity.this, result.getResult(),
//                    Toast.LENGTH_LONG).show();
        };
    };

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

        setContentView(R.layout.activity_merchant_cashier);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        orderCode = intent.getStringExtra("orderCode");
        if(orderCode == null) orderCode = "";
        totalPrice = intent.getStringExtra("totalPrice");

        model = new ViewModelProvider(this).get(BuyNowModel.class);
        orderInfoVO = MyGlobals.getInstance().getOrderInfoVO();

        btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView paid_amount = findViewById(R.id.paid_amount);
        TextView paid_dec = findViewById(R.id.paid_dec);
        Button immediatePayBtn = findViewById(R.id.immediatePayBtn);

        if(totalPrice.equals(""))
            try {
                int idx = orderInfoVO.totalMoney.indexOf(".");
                paid_amount.setText(orderInfoVO.totalMoney.substring(0, idx)+".");
                paid_dec.setText(orderInfoVO.totalMoney.substring(idx+1));
                immediatePayBtn.setText(getResources().getString(R.string.immediate_payment) + " ¥" + orderInfoVO.totalMoney);
            }catch (NullPointerException e){}
        else {
            int idx = totalPrice.indexOf(".");
            paid_amount.setText(totalPrice.substring(0, idx)+".");
            paid_dec.setText(totalPrice.substring(idx+1));
            immediatePayBtn.setText(getResources().getString(R.string.immediate_payment) + " ¥" + totalPrice);
        }

        CheckBox radio_wechat = findViewById(R.id.radio_wechat);
        CheckBox radio_alipay = findViewById(R.id.radio_alipay);
        CheckBox radio_UnionPay = findViewById(R.id.radio_UnionPay);
        radio_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_wechat.isChecked()){
                    radio_alipay.setChecked(false);
                    radio_UnionPay.setChecked(false);
                    try {
                        orderInfoVO.payType = 2;
                    }catch (Exception e){}
                    pay_type = 2;
                }
            }
        });
        radio_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_alipay.isChecked()){
                    radio_wechat.setChecked(false);
                    radio_UnionPay.setChecked(false);
                    try {
                        orderInfoVO.payType = 1;
                    }catch (Exception e){}
                    pay_type = 1;
                }
            }
        });
        radio_UnionPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_UnionPay.isChecked()){
                    radio_alipay.setChecked(false);
                    radio_wechat.setChecked(false);
                    orderInfoVO.payType = 1;
                }
            }
        });

        immediatePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!orderCode.equals("")){
                    if(pay_type == 1)  ALIToPay(orderCode, pay_type);
                    else if(pay_type == 2)  WXToPay(orderCode, pay_type);
                }

                model.saveOrder(orderInfoVO);
                model.getResult().observe(MerchantCashierActivity.this, result -> {
                    if(!result.equals("")){
                        cartOrderCode = result;

                        if(pay_type == 1)  ALIToPay(cartOrderCode, pay_type);
                        else if(pay_type == 2)  WXToPay(cartOrderCode, pay_type);
                    }

                });


            }
        });


    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//    }

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

    public void ALIToPay(String cartOrderCode, int type){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    if(!orderCode.equals("")){
                        jsonParams.put("orderCode", cartOrderCode);
                    }else {
                        jsonParams.put("cartOrderCode", cartOrderCode);
                    }
                    jsonParams.put("payType", type);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/orderToPayForApp";
                final ProgressHUD progressDialog = ProgressHUD.show(MerchantCashierActivity.this, "", true, false, MerchantCashierActivity.this);

                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject json = response.getJSONObject("data");

                                preOrderNo = json.optString("preOrderNo", "");

                                progressDialog.dismiss();
                                openAlipay();
                            } else {
                                Log.d("Home request", response.toString());
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void WXToPay(String cartOrderCode, int type){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    if(!orderCode.equals("")){
                        jsonParams.put("orderCode", cartOrderCode);
                    }else {
                        jsonParams.put("cartOrderCode", cartOrderCode);
                    }
                    jsonParams.put("payType", type);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/orderToPayForApp";
                final ProgressHUD progressDialog = ProgressHUD.show(MerchantCashierActivity.this, "", true, false, MerchantCashierActivity.this);

                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject json = response.getJSONObject("data");

                                packageValue = json.optString("package", "");
                                appId = json.optString("appid", "");
                                sign = json.optString("sign", "");
                                partnerId = json.optString("partnerid", "");
                                prepayId = json.optString("prepayid", "");
                                nonceStr = json.optString("noncestr", "");
                                timeStamp = String.valueOf(json.optInt("timestamp", 1));

                                progressDialog.dismiss();
                                openWechatPay();
                            } else {
                                Log.d("Home request", response.toString());
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void openWechatPay(){
        IWXAPI api = WXAPIFactory.createWXAPI(this, appId);
        api.registerApp(appId);

        if (api.isWXAppInstalled()) {
            PayReq request = new PayReq();
            request.appId = appId;
            request.nonceStr = nonceStr;
            request.packageValue = "Sign=WXPay";
            request.partnerId = partnerId;
            request.prepayId = prepayId;
            request.timeStamp = timeStamp;
            request.sign = sign;
            api.sendReq(request);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        //Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        Toast.makeText(MerchantCashierActivity.this, resp.errCode, Toast.LENGTH_SHORT).show();
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("微信支付结果" +String.valueOf(resp.errCode));
            builder.show();
        }
    }


    public void openAlipay(){
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(MerchantCashierActivity.this);
                Map<String,String> result = alipay.payV2(preOrderNo,true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }



}