package com.hws.hwsappandroid.ui;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ConfirmDialogView;
import com.hws.hwsappandroid.util.KindTipsView;
import com.walnutlabs.android.ProgressHUD;
import com.yingjie.addressselector.api.CYJAdSelector;
import com.yingjie.addressselector.api.OnSelectorListener;

public class EditShippingAddressActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    EditText consigneeName, phone_number, detailed_address;
    TextView toolbar_shipping_address, delete_button, address_p;
    ToggleButton toggleButton;
    RadioButton radio_mister, radio_lady;
    RadioGroup select_gender;
    boolean addNew = true;

    public String address;
    public String addressDefault;
    public String current_default_state;
    public String city;
    public String consignee = "";
    public String consigneeXb;
    public String country;
    public String district;
    public String gmtCreate;
    public String gmtModified;
    public String operatorId;
    public String phone;
    public String pkId;
    public String province;
    public String userId;

    shippingAdr shippingAddress = new shippingAdr();
    private EditShippingAddressModel model;

    private int mType;// 可能是新增地址，可能是编辑地址。
    private String selectedProvince;
    private String selectedCity;
    private String selectedArea;

    public boolean validate;
    String req_Activity = "";

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

        setContentView(R.layout.activity_edit_shipping_address);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(EditShippingAddressModel.class);

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        addressDefault = intent.getStringExtra("addressDefault");
        current_default_state = intent.getStringExtra("addressDefault");
        city = intent.getStringExtra("city");
        consignee = intent.getStringExtra("consignee");
        consigneeXb = intent.getStringExtra("consigneeXb");
        country = intent.getStringExtra("country");
        district = intent.getStringExtra("district");
        gmtCreate = intent.getStringExtra("gmtCreate");
        gmtModified = intent.getStringExtra("gmtModified");
        operatorId = intent.getStringExtra("operatorId");
        phone = intent.getStringExtra("phone");
        pkId = intent.getStringExtra("pkId");
        province = intent.getStringExtra("province");
        userId = intent.getStringExtra("userId");
        req_Activity = intent.getStringExtra("req_Activity");


        shippingAddress.address = address;
        shippingAddress.addressDefault = addressDefault;
        shippingAddress.city = city;
        shippingAddress.consignee = consignee;
        shippingAddress.consigneeXb = consigneeXb;
        shippingAddress.country = country;
        shippingAddress.district = district;
        shippingAddress.gmtCreate = gmtCreate;
        shippingAddress.gmtModified = gmtModified;
        shippingAddress.operatorId = operatorId;
        shippingAddress.phone = phone;
        shippingAddress.pkId = pkId;
        shippingAddress.province = province;
        shippingAddress.userId = userId;

        if((pkId != null) && (!pkId.equals(""))) addNew = false;

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar_shipping_address = findViewById(R.id.toolbar_shipping_address);
        delete_button = findViewById(R.id.delete_button);
        if(addNew) {
            toolbar_shipping_address.setText(getResources().getString(R.string.new_shipping_address));
            delete_button.setVisibility(View.GONE);
        }else{
            toolbar_shipping_address.setText(getResources().getString(R.string.edit_address));
            delete_button.setVisibility(View.VISIBLE);
        }

        consigneeName = findViewById(R.id.consigneeName);
        consigneeName.setText(consignee);
        phone_number = findViewById(R.id.phone_number);
        phone_number.setText(phone);
        address_p = findViewById(R.id.address);
        if(province!=null && city!=null && district!=null)
                address_p.setText(province + " " + city + " " + district);
        detailed_address = findViewById(R.id.detailed_address);
        detailed_address.setText(address);

        radio_mister = findViewById(R.id.radio_mister);
        radio_lady = findViewById(R.id.radio_mister);

        if(consigneeXb!=null){
            if(consigneeXb.equals("1"))
                radio_mister.setChecked(true);
            if(consigneeXb.equals("2"))
                radio_mister.setChecked(false);
        }
        else {
            consigneeXb = "1";
            radio_mister.setChecked(true);
        }

        select_gender = (RadioGroup) findViewById(R.id.select_gender);
        select_gender.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        toggleButton = findViewById(R.id.toggleButton);
        if(addressDefault!=null){
            if(addressDefault.equals("1")){
                toggleButton.setChecked(true);
            }else
                toggleButton.setChecked(false);
        }
        else {
            addressDefault = "0";
            toggleButton.setChecked(false);
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if(isChecked) {
                    SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                    String default_adr = pref.getString("default_adr","");
                    if(!default_adr.equals("")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(EditShippingAddressActivity.this);
                        alert.setTitle("默认地址已设置。\n" +
                                "您想将此地址设置为默认地址吗？.");

                        alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addressDefault = "1";
                                toggleButton.setChecked(true);
                            }
                        });
                        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addressDefault = "0";
                                toggleButton.setChecked(false);
                            }
                        });
                        alert.show();
                    }

                    addressDefault = "1";
                }
                else    addressDefault = "0";
            }
        });

//        ImageButton goto_Btn = findViewById(R.id.goto_Btn);
//        goto_Btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkType();
//                selector();
//            }
//        });
        LinearLayout select_province = findViewById(R.id.select_province);
        select_province.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkType();
                selector();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(0);
            }
        });

        Button add_address_btn = findViewById(R.id.add_address_btn);
        add_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate = true;
                validateCheck();
                if(!validate) return;
                shippingAddress.address = detailed_address.getText().toString();
                shippingAddress.addressDefault = addressDefault;
                shippingAddress.city = selectedCity;
                shippingAddress.consignee = consigneeName.getText().toString();
                shippingAddress.consigneeXb = consigneeXb;
                shippingAddress.country = country;
                shippingAddress.district = selectedArea;
                shippingAddress.phone = phone_number.getText().toString();
                shippingAddress.province = selectedProvince;
                shippingAddress.userId = userId;

                final ProgressHUD progressDialog = ProgressHUD.show(EditShippingAddressActivity.this, "", true, false, EditShippingAddressActivity.this);

                if(addNew){
                    model.saveData(shippingAddress);
                    model.getResult().observe(EditShippingAddressActivity.this, result -> {
                        if(result != null) {
                            shippingAddress.pkId = result;
                            progressDialog.dismiss();
                            if(addressDefault.equals("1")){
                                setDefault();
                            }else {
//                                Intent i = new Intent(EditShippingAddressActivity.this, ShippingAddressActivity.class);
                                setResult(RESULT_OK);
                                finish();
//                                startActivity(i);
                            }
                        }
                        CommonUtils.dismissProgress(progressDialog);
                    });


                }
                else {
                    shippingAddress.city = city;
                    if(selectedCity!=null)  shippingAddress.city = selectedCity;
                    shippingAddress.province = province;
                    if(selectedProvince!=null)  shippingAddress.province = selectedProvince;
                    shippingAddress.district = district;
                    if(selectedArea!=null)  shippingAddress.district = selectedArea;

                    model.updateData(shippingAddress);
                    model.getResult().observe(EditShippingAddressActivity.this, result -> {
                        if(result.equals("success"))  {
                            if(addressDefault.equals("1")){
                                setDefault();
                            }else{
                                if(current_default_state.equals("1")){
                                    SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("default_consignee", "");
                                    editor.putString("default_phone", "");
                                    editor.putString("default_adr", "");
                                    editor.commit();
                                }
                                CustomToast(getResources().getString(R.string.successfully_modified), true);
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                        CommonUtils.dismissProgress(progressDialog);
                    });


                }


            }
        });
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.radio_mister){
                consigneeXb = "1";
            }
            else if(i == R.id.radio_lady){
                consigneeXb = "2";
            }
        }
    };

    private void selector() {
        CYJAdSelector.showSelector(this, mType, selectedProvince, selectedCity, selectedArea, new OnSelectorListener() {
            @Override
            public void onSelector(String province, String city, String area) {
                // 选择完回调结果赋值给当前
                selectedProvince = province;
                selectedCity = city;
                selectedArea = area;

                address_p.setText(selectedProvince + " " + selectedCity + " " + selectedArea);
            }
        });
    }

    private void checkType() {
        if (TextUtils.isEmpty(selectedProvince) && TextUtils.isEmpty(selectedCity) && TextUtils.isEmpty(selectedArea)) {
            mType = 0;// add
        } else {
            mType = 1;// edit
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void setDefault(){
        model.setDefaultAddress(shippingAddress.pkId);
        model.getSetResult().observe(EditShippingAddressActivity.this, setResult -> {
            if (setResult.equals("default")) {
                SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("default_consignee", shippingAddress.consignee);
                editor.putString("default_phone", shippingAddress.phone);
                editor.putString("default_adr", shippingAddress.province + shippingAddress.city + shippingAddress.district + shippingAddress.address);
                editor.commit();

                CustomToast(getResources().getString(R.string.successfully_modified), true);
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    public void validateCheck(){
        if(consigneeName.getText().toString().equals("")){
            validate = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(EditShippingAddressActivity.this);
            builder.setMessage("请输入收货人姓名.");
            builder.create().show();
            return;
        }
        if(phone_number.getText().toString().equals("")){
            validate = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(EditShippingAddressActivity.this);
            builder.setMessage("请输入手机号码.");
            builder.create().show();
            return;
        }
        if(detailed_address.getText().toString().equals("")){
            validate = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(EditShippingAddressActivity.this);
            builder.setMessage("请输入详细地址.");
            builder.create().show();
            return;
        }
        if(address_p.getText().toString().equals("")){
            validate = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(EditShippingAddressActivity.this);
            builder.setMessage("请选择省市.");
            builder.create().show();
            return;
        }
    }

    private void showConfirmDialog(int confirmType) {
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
                model.delAddress(shippingAddress.pkId);
                model.getResult().observe(EditShippingAddressActivity.this, result -> {
                    if(result.equals("success")){
                        confirmDialog.dismiss();

                        if(shippingAddress.addressDefault.equals("1")){
                            SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("default_consignee", "");
                            editor.putString("default_phone", "");
                            editor.putString("default_adr", "");
                            editor.commit();
                        }
                        setResult(RESULT_OK);
                        finish();
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