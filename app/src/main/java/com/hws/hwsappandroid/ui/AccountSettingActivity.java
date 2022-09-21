package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
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

import com.bumptech.glide.Glide;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ConfirmDialogView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;

import cz.msebera.android.httpclient.Header;

public class AccountSettingActivity extends AppCompatActivity {
    private Context mContext;
    private Resources res;

    ImageButton btnBack;
    TextView user_name, phone_num, TxtCache;
    ImageView image_avatar;

    String nick_name, phone, avatar;

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

        setContentView(R.layout.activity_account_setting);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        mContext = AccountSettingActivity.this;
        res = mContext.getResources();

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        nick_name = pref.getString("name","");
        phone = pref.getString("phone_num","");
        avatar = pref.getString("avatar_img","");

        btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(5);
                finish();
            }
        });

        TxtCache = findViewById(R.id.TxtCache);
        user_name = findViewById(R.id.user_name);
        phone_num = findViewById(R.id.phone_num);
        image_avatar = findViewById(R.id.image_avatar);
        user_name.setText(nick_name);
        phone_num.setText(phone.substring(0,3)+"****"+phone.substring(7) );
        if(!avatar.equals(""))
//            Picasso.get().load(pref.getString("avatar_img","")).into(imageMeAvatar);
            Glide.with(this).load(avatar).into(image_avatar);

        Button logout_btn = findViewById(R.id.button_logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                logout();
                SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("account", "");
                editor.putString("avatar_img", "");
                editor.putString("name", "");
                editor.putString("pkId", "");
                editor.putString("refreshToken", "");
                editor.putString("token", "");
                editor.putString("roleName", "");
                editor.putString("phone_num", "");
                editor.commit();

                APIManager.setContext(getApplicationContext());
                APIManager.setAuthToken("token");

                setResult(5);
                finish();
            }
        });

        LinearLayout user_name_area = findViewById(R.id.user_name_area);
        user_name_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountSettingActivity.this, PersonalInfoActivity.class);
                startActivityForResult(i, 5);
            }
        });

        LinearLayout address_management = findViewById(R.id.address_management);
        address_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountSettingActivity.this, ShippingAddressActivity.class);
                startActivity(i);
            }
        });

        LinearLayout feedback_manage = findViewById(R.id.feedback_manage);
        feedback_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountSettingActivity.this, SubmitFeedBackActivity.class);
                i.putExtra("phone", phone);
                startActivity(i);
            }
        });

        try {
            File cache = AccountSettingActivity.this.getCacheDir();
            TxtCache.setText(getCacheSize(cache));
        }catch (Exception e){}

        LinearLayout clear_cache = findViewById(R.id.clear_cache);
        clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(5);

            }
        });

        LinearLayout privacy_policy = findViewById(R.id.privacy_policy);
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountSettingActivity.this, PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });

        LinearLayout agreement_manage = findViewById(R.id.agreement_manage);
        agreement_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountSettingActivity.this, UserAgreementActivity.class);
                startActivity(i);
            }
        });

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            refreshView();
        }
    }

    public void refreshView(){

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        nick_name = pref.getString("name","");
        phone = pref.getString("phone_num","");

        user_name.setText(nick_name);
        phone_num.setText(phone.substring(0,3)+"****"+phone.substring(7) );
    }

    public void logout(){
        JSONObject jsonParams = new JSONObject();

        String url = "/logout";
        APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                progressDialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        CustomToast("Logout", true);

                        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("account", "");
                        editor.putString("avatar_img", "");
                        editor.putString("name", "");
                        editor.putString("pkId", "");
                        editor.putString("refreshToken", "");
                        editor.putString("token", "");
                        editor.putString("roleName", "");
                        editor.putString("phone_num", "");
                        editor.commit();

                        APIManager.setContext(getApplicationContext());
                        APIManager.setAuthToken("token");

                        setResult(5);
                        finish();

                    } else {
                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                progressDialog.dismiss();
                Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                progressDialog.dismiss();
                Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
            }
        });
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
                confirmDialog.dismiss();
                clearApplicationData(AccountSettingActivity.this);
                TxtCache.setText("0MB");
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

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                //다운로드 파일은 지우지 않도록 설정
                //if(s.equals("lib") || s.equals("files")) continue;
                deleteDir(new File(appDir, s));
                Toast.makeText(context, context.getResources().getString(R.string.cache_clear_success), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                //
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }
}