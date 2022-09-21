package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.RefundModel;
import com.hws.hwsappandroid.util.CommonUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AfterSalesDetailActivity extends AppCompatActivity {

    private AfterSalesListModel model;
    public RefundModel RefundData;
    String pkId = "";

    LinearLayout shop_area;
    ConstraintLayout goodsCard;
    ImageView image_product, icon_clock;
    TextView toolbar_title, page_explain, shop_name, text_product_info, product_option, text_price, text_amount, orderAmount;
    TextView applicationType, ReasonForReturn, receiptStatus, ChargebackNumber, applicationTime, copyBtn;
    Button close_applyBtn, view_progressBtn;
    CardView product_info;
    ImageButton goto_Btn;

    String[] refund_reason = {"",};
    String[] refund_type = {"",};
    String[] receiving_status = {"",};
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

        setContentView(R.layout.activity_after_sales_detail);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        refund_reason = getResources().getStringArray(R.array.refund_reason);
        refund_type = getResources().getStringArray(R.array.refund_type);
        receiving_status = getResources().getStringArray(R.array.receiving_status);

        Intent intent = getIntent();
        pkId = intent.getStringExtra("pkId");

        model = new ViewModelProvider(this).get(AfterSalesListModel.class);

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
                Intent SC = new Intent(AfterSalesDetailActivity.this, ServiceMessagesActivity.class);
                SC.putExtra("shopId", RefundData.shopName);
                startActivity(SC);
            }
        });

        icon_clock = findViewById(R.id.icon_clock);
        toolbar_title = findViewById(R.id.toolbar_title);
        page_explain = findViewById(R.id.page_explain);
        product_info = findViewById(R.id.product_info);
        shop_area = findViewById(R.id.shop_area);
        goto_Btn = findViewById(R.id.goto_Btn);
        goodsCard = findViewById(R.id.goodsCard);
        image_product = findViewById(R.id.image_product);
        shop_name= findViewById(R.id.shop_name);
        text_product_info= findViewById(R.id.text_product_info);
        product_option= findViewById(R.id.product_option);
        text_price= findViewById(R.id.text_price);
        text_amount= findViewById(R.id.text_amount);
        orderAmount= findViewById(R.id.orderAmount);
        applicationType= findViewById(R.id.applicationType);
        ReasonForReturn= findViewById(R.id.ReasonForReturn);
        receiptStatus= findViewById(R.id.receiptStatus);
        ChargebackNumber= findViewById(R.id.ChargebackNumber);
        applicationTime= findViewById(R.id.applicationTime);
        copyBtn= findViewById(R.id.copyBtn);
        close_applyBtn= findViewById(R.id.close_applyBtn);
        view_progressBtn= findViewById(R.id.view_progressBtn);

        final ProgressHUD progressDialog = ProgressHUD.show(AfterSalesDetailActivity.this, "", true, false, null);

        model.loadData(pkId);
        model.getRefund().observe(this, refund -> {
            if(!refund.pkId.equals("")){
                RefundData = refund;

                if(RefundData.refundApplyStatus == 0) {
                    toolbar_title.setText(getResources().getString(R.string.after_sales_details));
                    icon_clock.setImageResource(R.drawable.icon_check);
                    page_explain.setText(getResources().getString(R.string.after_waiting_merchant_complete));
                }
                if(RefundData.refundApplyStatus == 1) {
                    toolbar_title.setText(getResources().getString(R.string.merchant_refuses));
                    icon_clock.setImageResource(R.drawable.icon_check);
                    page_explain.setText(getResources().getString(R.string.application_has_been_rejected));
                }
                if(RefundData.refundApplyStatus == 2) {
                    toolbar_title.setText(getResources().getString(R.string.completed));
                    icon_clock.setImageResource(R.drawable.icon_check);
                    page_explain.setText(getResources().getString(R.string.refund_has_been_sent_back));
                }

                String goodsId = RefundData.goodsId;
                shop_name.setText(RefundData.shopName);
                Picasso.get()
                        .load(RefundData.goodsPic)
                        .resize(600, 600)
                        .into(image_product);
                text_product_info.setText(RefundData.goodsName);
                product_option.setText(RefundData.goodsSpec);
                text_price.setText(RefundData.goodsPrice);
                text_amount.setText("x" + RefundData.goodsNum);
                orderAmount.setText("￥" + RefundData.refundMoney);

                applicationType.setText(refund_type[RefundData.refundMoneyType]);
                ReasonForReturn.setText(refund_reason[RefundData.refundReason]);
                receiptStatus.setText(receiving_status[RefundData.receivingStatus]);

                ChargebackNumber.setText(RefundData.refundCode);
                applicationTime.setText(RefundData.refundApplyTime);

                progressDialog.dismiss();
            }
            CommonUtils.dismissProgress(progressDialog);
        });

        shop_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterSalesDetailActivity.this, StoreDetailsActivity.class);
                i.putExtra("shopId", RefundData.shopId);
                if(!RefundData.shopId.equals(""))
                    startActivity(i);
            }
        });

        goto_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterSalesDetailActivity.this, StoreDetailsActivity.class);
                i.putExtra("shopId", RefundData.shopId);
                if(!RefundData.shopId.equals(""))
                    startActivity(i);
            }
        });

        goodsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailProduct = new Intent(AfterSalesDetailActivity.this, ProductDetailActivity.class);
                String pkId = RefundData.goodsId;
                detailProduct.putExtra("pkId", pkId);
                startActivity(detailProduct);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(AfterSalesDetailActivity.this, ChargebackNumber.getText().toString());
                Toast.makeText(AfterSalesDetailActivity.this, "copied "+ ChargebackNumber.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        view_progressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent progressView = new Intent(AfterSalesDetailActivity.this, AfterSalesProgressActivity.class);
                progressView.putExtra("shopId", RefundData.shopId);
                progressView.putExtra("shopName", RefundData.shopName);
                progressView.putExtra("goodsId", RefundData.goodsId);
                progressView.putExtra("goodsPic", RefundData.goodsPic);
                progressView.putExtra("goodsName", RefundData.goodsName);
                progressView.putExtra("goodsPrice", RefundData.goodsPrice);
                progressView.putExtra("goodsSpec", RefundData.goodsSpec);
                progressView.putExtra("goodsNum", RefundData.goodsNum);
                progressView.putExtra("receivingStatus", RefundData.receivingStatus);
                progressView.putExtra("refundApplyStatus", RefundData.refundApplyStatus);
                progressView.putExtra("refundMoney", RefundData.refundMoney);
                progressView.putExtra("refundMoneyType", RefundData.refundMoneyType);
                progressView.putExtra("refundApplyTime", RefundData.refundApplyTime);
                progressView.putExtra("refundCompleteTime", RefundData.refundCompleteTime);
                startActivity(progressView);
            }
        });

        close_applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent progressView = new Intent(AfterSalesDetailActivity.this, TransactionClosedActivity.class);
                progressView.putExtra("orderId", RefundData.orderId);
                startActivity(progressView);
                finish();
            }
        });
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