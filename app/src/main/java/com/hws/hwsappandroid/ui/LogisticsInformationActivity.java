package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.LogisticsModel;
import com.hws.hwsappandroid.util.LogisticCardListAdapter;
import com.hws.hwsappandroid.util.logisticListAdapter;
import com.squareup.picasso.Picasso;

public class LogisticsInformationActivity extends AppCompatActivity {

    public LogisticsInformationModel model;
    public String orderId="";
    public LogisticsModel mLogisticsInfo = new LogisticsModel();

    ImageView expressLogo, image_product;
    TextView expressName, expressCode, delivery_status, product_option, product_num;
    Button callBtn;
    LinearLayout show_detail;
    RecyclerView logistic_card_list;
    LogisticCardListAdapter logisticsCardListAdapter;

    private int[] deliveryStatus = {R.string.shipped, R.string.in_transit, R.string.dispatching, R.string.received };

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

        setContentView(R.layout.activity_logistics_information);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        expressLogo = findViewById(R.id.expressLogo);
        expressName = findViewById(R.id.expressName);
        expressCode = findViewById(R.id.expressCode);

        logistic_card_list = findViewById(R.id.logistic_card_list);
        logistic_card_list.setLayoutManager(new LinearLayoutManager(this));
        logisticsCardListAdapter = new LogisticCardListAdapter(this);
        logistic_card_list.setAdapter(logisticsCardListAdapter);

        image_product = findViewById(R.id.image_product);
        product_option = findViewById(R.id.product_option);
        product_num = findViewById(R.id.product_num);
        delivery_status = findViewById(R.id.delivery_status);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        delivery_status.setText(deliveryStatus[0]);

        model = new ViewModelProvider(this).get(LogisticsInformationModel.class);
        model.loadData(orderId);
        model.getLogisticsInfo().observe(this, logisticsInfo -> {
            mLogisticsInfo = logisticsInfo;
            if(mLogisticsInfo.deliveryResultList.size() > 0) {
                logisticsCardListAdapter.setData(mLogisticsInfo.deliveryResultList);

                try {
                    Picasso.get()
                            .load(mLogisticsInfo.deliveryResultList.get(0).goodsInfoVO.get(0).goodsPic)
                            .resize(500, 500)
                            .into(image_product);

                    if (mLogisticsInfo.deliveryResultList.size() > 1)
                        product_num.setText("共" + mLogisticsInfo.deliveryResultList.size() + "件");
                    else product_num.setVisibility(View.GONE);

                    product_option.setText(mLogisticsInfo.deliveryResultList.get(0).goodsInfoVO.get(0).goodsName);
                    int i = Integer.parseInt(mLogisticsInfo.deliveryResultList.get(0).deliverystatus);
                    delivery_status.setText(deliveryStatus[i-1]);

                }catch (Exception e){}

            }

        });

//        callBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15526747101"));
//                startActivity(intent);
//            }
//        });

//        show_detail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showLogisticsDialog();
//            }
//        });
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

    private void showLogisticsDialog() {
        final Dialog logisticsDialog = new Dialog(this);
        logisticsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logisticsDialog.setContentView(R.layout.logistics_info_dialog);

        ImageButton button_close = (ImageButton) logisticsDialog.findViewById(R.id.button_close);
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logisticsDialog.dismiss();
            }
        });

        TextView logistic_code = (TextView) logisticsDialog.findViewById(R.id.logistic_code);

        Button copyBtn = (Button) logisticsDialog.findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(LogisticsInformationActivity.this, logistic_code.getText().toString());
                Toast.makeText(LogisticsInformationActivity.this, "copied "+ logistic_code.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Button callBtn = (Button) logisticsDialog.findViewById(R.id.callBtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15526747101"));
                startActivity(intent);
            }
        });

        logisticsDialog.show();
        logisticsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logisticsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logisticsDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        logisticsDialog.getWindow().setGravity(Gravity.BOTTOM);

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