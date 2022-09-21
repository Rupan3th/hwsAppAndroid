package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.KindTipsView;
import com.hws.hwsappandroid.util.ShippingAddressListAdapter;
import com.hws.hwsappandroid.util.SwipeController;
import com.hws.hwsappandroid.util.SwipeControllerActions;

import java.util.ArrayList;

public class ShippingAddressActivity extends AppCompatActivity implements ItemClickListener {

    public static Activity SAActivity;
    RecyclerView recyclerView;
    LinearLayout no_result_area;
    private ShippingAddressListAdapter mAdapter;
    private ShippingAddressModel model;
    public ArrayList<shippingAdr> shippingAddr;
    public shippingAdr Adr;
    SwipeController swipeController = null;

    String country = "中国";
    String userId = "";
    String req_Activity = "", activityPurpose;
    boolean isDel, setting_default, refresh;

    View decorView;

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

        setContentView(R.layout.activity_shipping_address);
        SAActivity = ShippingAddressActivity.this;

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();
        req_Activity = intent.getStringExtra("req_Activity");
        activityPurpose = intent.getStringExtra("purpose");

        shippingAddr = new ArrayList<>();
        Adr = new shippingAdr();
        model = new ViewModelProvider(this).get(ShippingAddressModel.class);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("account", "13190033097");
//        editor.putString("name", "用户3097");
//        editor.putString("token", "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUjI0NrQ0MDA2NrA0V9JRKi1OLfJMAQqbmSSlGJsbmxoaWSaapBmbJhmZpKWYWJoZpZoZGgIRUG1eZnK2X2JuKlD18ykrnnVsh5qRWFqSkV-UWZKZWgyUSiwoCAUaChTPKskkxuBaAAAAAP__.5L-46ckuNEKXpe9XGwPoD65caw3TsxfLzV0_VYjIrg4");
//        editor.commit();
        userId = pref.getString("name","");

        Button add_address_btn = findViewById(R.id.add_address_btn);
        add_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShippingAddressActivity.this, EditShippingAddressActivity.class);
                i.putExtra("country", country);
                i.putExtra("userId", userId);
                i.putExtra("addNew", true);

                startActivityForResult(i, 0);
            }
        });

        Button new_address = findViewById(R.id.new_address);
        new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShippingAddressActivity.this, EditShippingAddressActivity.class);
                i.putExtra("country", country);
                i.putExtra("userId", userId);
                i.putExtra("addNew", true);

                startActivityForResult(i, 0);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShippingAddressListAdapter(this, activityPurpose);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        no_result_area = findViewById(R.id.no_result_area);

        model.loadData();
        model.getAddress().observe(this, shippingAddress -> {
            shippingAddr = shippingAddress;

            if(shippingAddress.size() < 1)
                no_result_area.setVisibility(View.VISIBLE);
            mAdapter.setData(shippingAddress);
        });

//        swipeController = new SwipeController(new SwipeControllerActions() {
//            @Override
//            public void onRightClicked(int position) {
//                showConfirmDialog(0, position);
//            }
//        });
//
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(recyclerView);
//
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                swipeController.onDraw(c, "取消", 30, 40);
//            }
//        });
    }

    @Override
    public void onClick(View view, int position) {
        String activityPurpose = getIntent().getStringExtra("purpose");

        if (activityPurpose != null && activityPurpose.equals("choose")) {
            shippingAdr addr = shippingAddr.get(position);
            Intent i = new Intent();
            i.putExtra("address", addr.address);
            i.putExtra("addressDefault", addr.addressDefault);
            i.putExtra("city", addr.city);
            i.putExtra("consignee", addr.consignee);
            i.putExtra("consigneeXb", addr.consigneeXb);
            i.putExtra("country", addr.country);
            i.putExtra("district", addr.district);
            i.putExtra("gmtCreate", addr.gmtCreate);
            i.putExtra("gmtModified", addr.gmtModified);
            i.putExtra("operatorId", addr.operatorId);
            i.putExtra("userId", addr.userId);
            i.putExtra("phone", addr.phone);
            i.putExtra("pkId", addr.pkId);
            i.putExtra("province", addr.province);
            setResult(RESULT_OK, i);
            finish();
        } else {
//            setting_default = false;
//            Adr = mAdapter.getItem(position);
//            Adr.addressDefault = "1";
//            mAdapter.setDefaultAdr(position);
//            model.setDefaultAddress(Adr.pkId);
//            model.getResult().observe(this, result -> {
//                if(result.equals("success")){
//                    if(!setting_default) {
//                        CustomToast(getResources().getString(R.string.successfully_modified), true);
//                        setting_default = true;
//                    }
//                }
//            });
//
//            SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("default_consignee", Adr.consignee);
//            editor.putString("default_phone", Adr.phone);
//            editor.putString("default_adr", Adr.province + Adr.city + Adr.district + Adr.address);
//            editor.commit();
        }

//        Intent i = new Intent(ShippingAddressActivity.this, OrderDetailsActivity.class);
//        Good productInfo = mAdapter.getGoodInfo(position);
//        String good_name = productInfo.goodsName;
//        String good_sn = productInfo.goodsSn;
//
//        i.putExtra("good_sn", good_sn);
//        startActivity(i);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            refreshView();
        }
    }

    public void refreshView() {
        refresh = false;

        model.loadData();
        model.getAddress().observe(this, shippingAddress -> {
            shippingAddr = shippingAddress;

//            if(!refresh){
                SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                if(shippingAddress.size() < 1) {
                    no_result_area.setVisibility(View.VISIBLE);

//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putString("default_consignee", "");
//                    editor.putString("default_phone", "");
//                    editor.putString("default_adr", "");
//                    editor.commit();
                }
                else {
                    no_result_area.setVisibility(View.GONE);
                }
                mAdapter.setData(shippingAddress);
                refresh = true;
//            }

        });

    }

    private void showConfirmDialog(int confirmType, int position) {
        final Dialog confirmDialog = new Dialog(this);
        View view = new KindTipsView(this, confirmType);

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
                isDel = false;
                String default_Status = mAdapter.models.get(position).addressDefault;
                model.delAddress(mAdapter.models.get(position).pkId);
                model.getResult().observe(ShippingAddressActivity.this, result -> {
                    if(result.equals("success")){
                        confirmDialog.dismiss();
                        if(!isDel) {
//                            mAdapter.models.remove(position);
//                            mAdapter.notifyItemRemoved(position);
//                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                            shippingAddr.remove(position);
                            mAdapter.setData(shippingAddr);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                            if(shippingAddr.size() == 0 ){
                                no_result_area.setVisibility(View.VISIBLE);
                                SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("default_consignee", "");
                                editor.putString("default_phone", "");
                                editor.putString("default_adr", "");
                                editor.commit();
                            }

                            if(default_Status.equals("1")){
                                SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("default_consignee", "");
                                editor.putString("default_phone", "");
                                editor.putString("default_adr", "");
                                editor.commit();
                            }
                            isDel = true;
                        }

                    }
                });
            }
        });

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(view);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rect displayRectangle = new Rect();
        Window window = confirmDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        confirmDialog.getWindow().setLayout(CommonUtils.getPixelValue(this, 286), ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmDialog.show();
//        confirmDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void CustomToast(String text, boolean state){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.toast_layout_root));
        ImageView toast_icon = layout.findViewById(R.id.toast_icon);
        TextView textView = layout.findViewById(R.id.textBoard);
        textView.setText(text);

        Toast toastView = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toastView.setGravity(Gravity.CENTER, 0, 0);
        toastView.setView(layout);
        toastView.show();
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