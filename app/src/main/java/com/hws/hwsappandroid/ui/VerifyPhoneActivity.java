package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.MainActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.ui.me.MeFragment;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.JWebSocketClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class VerifyPhoneActivity extends AppCompatActivity implements DialogInterface.OnCancelListener{

    private Context mContext;
    private Resources res;

    private EditText mEditPhone, mEditVerifyCode;
    private TextView phone_validate, mTxtLeftTime, get_verify_code, agree_privacy, user_register_agreement, privacy_policy;
    private ImageButton del_phone_num;
    private Button btn_login;
    private LinearLayout agree;
    private CheckBox radio_agree_privacy;
    View decorView;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    private String mStrVerifyCode ="";
    private boolean isVerified = false;
    private boolean isLogin = false;

    private int mIntLeftTime = 60;
    private String mStrLeftTime;
    String strPhone = "";

    private String account;
    private String avatarPic;
    private String name;
    private String pkId;
    private String  token;
    private String  refreshToken;
    private String  roleName;

    SharedPreferences pref;


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

        setContentView(R.layout.activity_verify_phone);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mContext = VerifyPhoneActivity.this;
        res = mContext.getResources();


//        setKeypad();

//        ImageView spinner = findViewById(R.id.splashSpinner);//
//        spinner.post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                AnimationDrawable spinnerAnim = (AnimationDrawable) spinner.getBackground();
//                if (!spinnerAnim.isRunning())
//                {
//                    spinnerAnim.start();
//                }
//            }
//        });


        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEditPhone = (EditText) findViewById(R.id.edit_phone_number);
        del_phone_num = findViewById(R.id.del_phone_num);
        phone_validate = findViewById(R.id.phone_validate);
        mEditVerifyCode = (EditText) findViewById(R.id.edit_verify_code);
        mTxtLeftTime = findViewById(R.id.txt_left_time);
        get_verify_code = findViewById(R.id.get_verify_code);
        agree = findViewById(R.id.agree);
        radio_agree_privacy = findViewById(R.id.radio_agree_privacy);
        agree_privacy = findViewById(R.id.agree_privacy);
        user_register_agreement = findViewById(R.id.user_register_agreement);
        privacy_policy = findViewById(R.id.privacy_policy);
        btn_login = findViewById(R.id.btn_login);

        mEditPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    mEditPhone.setHint(R.string.please_input_phone);
                }else{
                    mEditPhone.setHint(R.string.please_input_mobile);
                }
            }
        });
        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!mEditPhone.getText().toString().equals("")) {
                    del_phone_num.setVisibility(View.VISIBLE);
                    strPhone = mEditPhone.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        del_phone_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditPhone.setText("");
                strPhone = "";
                del_phone_num.setVisibility(View.INVISIBLE);
                isVerified = false;
                btn_login.setBackgroundResource(R.drawable.login_btn_disable);
            }
        });

        get_verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMobileNO(strPhone)) {
                    mTxtLeftTime.setVisibility(View.VISIBLE);
                    get_verify_code.setVisibility(View.INVISIBLE);
                    phone_validate.setVisibility(View.INVISIBLE);
                    mEditVerifyCode.setText("");
                    mStrVerifyCode = "";
                    getVerifyCode(strPhone);
                }else {
                    phone_validate.setVisibility(View.VISIBLE);
//                            Toast.makeText(mContext, "请输入有效的手机号码！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        mEditVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mStrVerifyCode = mEditVerifyCode.getText().toString();
                if (!mStrVerifyCode.equals("")) {
//                    stopTimerTask();
                    if(radio_agree_privacy.isChecked() && isVerified)
                        btn_login.setBackgroundResource(R.drawable.login_btn);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        radio_agree_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_agree_privacy.isChecked() && !mEditVerifyCode.getText().toString().equals("") && isMobileNO(strPhone) && isVerified) {
                    btn_login.setBackgroundResource(R.drawable.login_btn);

                }
                else btn_login.setBackgroundResource(R.drawable.login_btn_disable);
            }
        });
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_agree_privacy.isChecked()) radio_agree_privacy.setChecked(false);
                else radio_agree_privacy.setChecked(true);

                if(radio_agree_privacy.isChecked() && !mEditVerifyCode.getText().toString().equals("") && isMobileNO(strPhone) && isVerified) {
                    btn_login.setBackgroundResource(R.drawable.login_btn);

                }
                else btn_login.setBackgroundResource(R.drawable.login_btn_disable);
            }
        });

        user_register_agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VerifyPhoneActivity.this, UserAgreementActivity.class);
                startActivity(i);
            }
        });
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VerifyPhoneActivity.this, PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStrVerifyCode = mEditVerifyCode.getText().toString();
                if(isVerified && radio_agree_privacy.isChecked())
                    smsLogin(strPhone, mStrVerifyCode);
            }
        });
    }

    private void getVerifyCode(String strPhone) {
//        final ProgressHUD progressDialog = ProgressHUD.show(mContext, res.getString(R.string.processing), true, false, VerifyPhoneActivity.this);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("phone", strPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (isModify) {
//            if (newPhoneSuccess) {
//                params.put("action", "register");
//            }
//        }
        String url = "/sendSms";
        APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                progressDialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        isVerified = true;
                        startTimer();
                        CustomToast("验证码已发送", true);
                    } else {
                        mTxtLeftTime.setVisibility(View.INVISIBLE);
                        get_verify_code.setVisibility(View.VISIBLE);
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        CustomToast("验证码已发送", false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                progressDialog.dismiss();
                mTxtLeftTime.setVisibility(View.INVISIBLE);
                get_verify_code.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                progressDialog.dismiss();
                mTxtLeftTime.setVisibility(View.INVISIBLE);
                get_verify_code.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void smsLogin(String strPhone, String strVerifyCode){
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("phone", strPhone);
            jsonParams.put("smscode", strVerifyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "/sms/login";
        APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                progressDialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        CustomToast("Login success", true);
                        JSONObject obj = response.getJSONObject("data");
                        account = obj.getString("account");
//                        avatarPic = obj.getString("avatarPic");
                        name = obj.getString("name");
                        pkId = obj.getString("pkId");//"pkId": "1953a72830a648c483c568f9d7d284b2",
                        refreshToken = obj.getString("refreshToken");
                        token = obj.getString("token");
                        roleName = obj.getString("roleName");

                        pref = getSharedPreferences("user_info",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("account", account);
//                        editor.putString("avatar_img", avatarPic);
                        editor.putString("name", name);
                        editor.putString("pkId", pkId);
                        editor.putString("refreshToken", refreshToken);
                        editor.putString("token", token);
                        editor.putString("roleName", roleName);
                        editor.putString("phone_num", strPhone);
                        editor.commit();

                        APIManager.setContext(getApplicationContext());
                        APIManager.setAuthToken(token);

                        MainActivity MA =  (MainActivity) MainActivity.MActivity;
                        MA.reload_badge();

                        isLogin = true;
                        setResult(5);
//                        RefreshMe();
                        // try to run websocket
                        JWebSocketClient.getInstance(getApplicationContext());

                        finish();

                    } else {
                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        btn_login.setBackgroundResource(R.drawable.login_btn_disable);
                        isVerified = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                progressDialog.dismiss();
                btn_login.setBackgroundResource(R.drawable.login_btn_disable);
                isVerified = false;
                Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                progressDialog.dismiss();
                btn_login.setBackgroundResource(R.drawable.login_btn_disable);
                isVerified = false;
                Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
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
            mTxtLeftTime.setText("");
            mTxtLeftTime.setVisibility(View.INVISIBLE);
            get_verify_code.setVisibility(View.VISIBLE);
        }
    }

    public void initializeTimerTask() {
        mIntLeftTime = 60;
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        mIntLeftTime--;
                        mStrLeftTime = CommonUtils.getLeftTime(mContext, mIntLeftTime);
                        mTxtLeftTime.setText(mStrLeftTime);
                        if (mIntLeftTime == 0) {
                            stopTimerTask();
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][3456789]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public void CustomToast(String text, boolean state){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_board, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView textView = layout.findViewById(R.id.textBoard);
        textView.setText(text);
        if(state){
            textView.setBackgroundResource(R.mipmap.toast_success);
        }else {
            textView.setBackgroundResource(R.mipmap.toast_fail);
        }

        Toast toastView = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toastView.setGravity(Gravity.CENTER, 0, 0);
        toastView.setView(layout);
        toastView.show();
    }

    public void RefreshMe(){
        try {
//            FragmentManager manager = getSupportFragmentManager();
//            MeFragment meInfo = (MeFragment) manager.findFragmentById(R.id.meTab);
//            meInfo.refreshView();

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_main, null);

            TextView register = view.findViewById(R.id.register);
            LinearLayout myInfo = view.findViewById(R.id.myInfo);
            TextView text_me_name = view.findViewById(R.id.text_me_name);

            register.setVisibility(View.GONE);
            myInfo.setVisibility(View.VISIBLE);
            text_me_name.setText(name);

        }catch (Exception e){}

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

}



/*login result

{
  "code": "10000",
  "data": {
    "account": "15040158649",
    "name": "用户8649",
    "pkId": "1953a72830a648c483c568f9d7d284b2",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUjI0NTAxMDS1MDOxVNJRKi1OLfJMAQlbmhonmhtZGBskmplYJJtYGCebmlmkWaaYpxhZmCQZAdXmZSZn-yXmpgJVP5-y4lnHdqgZiaUlGflFmSWZqcVAqcSCglCgoUDxrJJMYgyuBQAAAP__.fiO55IcMg-z9roGbQ2JSloerAHF0iX3gFN5YufhbdQo",
    "roleName": "[appUser]",
    "token": "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUjI0NTAxMDS1MDOxVNJRKi1OLfJMAQlbmhonmhtZGBskmplYJJtYGCebmlmkWaaYpxhZmCQZAdXmZSZn-yXmpgJVP5-y4lnHdqgZiaUlGflFmSWZqcVAqcSCglCgoUDxrJJMYgyuBQAAAP__.fiO55IcMg-z9roGbQ2JSloerAHF0iX3gFN5YufhbdQo"
  },
  "msg": "访问成功",
  "status": true
}

添加商品至订单
{"aoList": [{
"goodsId":"",
"goodsSpec":"",
"goodsSpecId":
"goodsNum": ""
" addressId":""
}]
}*/