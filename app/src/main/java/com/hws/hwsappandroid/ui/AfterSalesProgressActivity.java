package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.squareup.picasso.Picasso;

public class AfterSalesProgressActivity extends AppCompatActivity {

    TextView shop_name, text_product_info, text_price, product_option, text_amount;
    ImageView image_product, Approved_state_box, Refund_completed_box;
    TextView Submit_apply_date, Approved_txt, Approved_state_date, Refund_completed_txt, Refund_completed_date, refund_amount, refund_method;
    LinearLayout shop_area;
    ConstraintLayout goodsCard;
    ImageButton goto_Btn;

    String shopId = "";
    String shopName = "";
    String goodsId = "";
    String goodsPic = "";
    String goodsName = "";
    String goodsPrice = "";
    String goodsSpec = "";
    String refundMoney = "";
    String refundApplyTime = "";
    String refundCompleteTime = "";
    int goodsNum, receivingStatus, refundApplyStatus, refundMoneyType;

    String[] refund_type = {"",};

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

        setContentView(R.layout.activity_after_sales_progress);

        Intent intent = getIntent();
        shopId = intent.getStringExtra("shopId");
        shopName = intent.getStringExtra("shopName");
        goodsId = intent.getStringExtra("goodsId");
        goodsPic = intent.getStringExtra("goodsPic");
        goodsName = intent.getStringExtra("goodsName");
        goodsPrice = intent.getStringExtra("goodsPrice");
        goodsSpec = intent.getStringExtra("goodsSpec");
        goodsNum = intent.getIntExtra("goodsNum", 1);
        receivingStatus = intent.getIntExtra("receivingStatus", 1);
        refundApplyStatus = intent.getIntExtra("refundApplyStatus", 1);
        refundMoney = intent.getStringExtra("refundMoney");
        refundMoneyType = intent.getIntExtra("refundMoneyType", 1);
        refundApplyTime = intent.getStringExtra("refundApplyTime");
        refundCompleteTime = intent.getStringExtra("refundCompleteTime");
        refund_type = getResources().getStringArray(R.array.refund_money_type);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shop_area = findViewById(R.id.shop_area);
        shop_name = findViewById(R.id.shop_name);
        goto_Btn = findViewById(R.id.goto_Btn);
        goodsCard = findViewById(R.id.goodsCard);
        text_product_info = findViewById(R.id.text_product_info);
        text_price = findViewById(R.id.text_price);
        product_option = findViewById(R.id.product_option);
        text_amount = findViewById(R.id.text_amount);
        image_product = findViewById(R.id.image_product);
        Approved_state_box = findViewById(R.id.Approved_state_box);
        Refund_completed_box = findViewById(R.id.Refund_completed_box);

        Submit_apply_date = findViewById(R.id.Submit_apply_date);
        Approved_txt = findViewById(R.id.Approved_txt);
        Approved_state_date = findViewById(R.id.Approved_state_date);
        Refund_completed_txt = findViewById(R.id.Refund_completed_txt);
        Refund_completed_date = findViewById(R.id.Refund_completed_date);
        refund_amount = findViewById(R.id.refund_amount);
        refund_method = findViewById(R.id.refund_method);

        shop_name.setText(shopName);
        Picasso.get()
                .load(goodsPic)
                .resize(600, 600)
                .into(image_product);
        text_product_info.setText(goodsName);
        text_price.setText(goodsPrice);
        product_option.setText(goodsSpec);
        text_amount.setText("x" + goodsNum);
        Submit_apply_date.setText(refundApplyTime.substring(0, refundApplyTime.indexOf(" ")));
        Approved_state_date.setText(refundApplyTime.substring(0, refundApplyTime.indexOf(" ")));
        if(!refundCompleteTime.equals("") ) {
            Refund_completed_date.setText(refundCompleteTime.substring(0, refundCompleteTime.indexOf(" ")));
            Refund_completed_txt.setTextColor(Color.parseColor("#1D2129"));
            Refund_completed_box.setImageResource(R.drawable.rectangle_hard_solid);
        }
        refund_amount.setText("￥" + refundMoney);
        if(refundMoneyType > 0)  refund_method.setText(refund_type[refundMoneyType - 1]);

        shop_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterSalesProgressActivity.this, StoreDetailsActivity.class);
                i.putExtra("shopId", shopId);
                if(!shopId.equals(""))
                    startActivity(i);
            }
        });

        goto_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfterSalesProgressActivity.this, StoreDetailsActivity.class);
                i.putExtra("shopId", shopId);
                if(!shopId.equals(""))
                    startActivity(i);
            }
        });

        goodsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailProduct = new Intent(AfterSalesProgressActivity.this, ProductDetailActivity.class);
                String pkId = goodsId;
                detailProduct.putExtra("pkId", pkId);
                startActivity(detailProduct);
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