package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.SplashActivity;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.walnutlabs.android.ProgressHUD;

public class SubmitFeedBackActivity extends AppCompatActivity implements DialogInterface.OnCancelListener{

    EditText phone_num, contents;
    TextView remain_letter;
    Button submitFeedback;

    SubmitFeedBackModel model;

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

        setContentView(R.layout.activity_submit_feed_back);

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(SubmitFeedBackModel.class);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");

        phone_num = findViewById(R.id.phone_num);
        phone_num.setText(phone);
        contents = findViewById(R.id.contents);
        remain_letter = findViewById(R.id.count);
        submitFeedback = findViewById(R.id.submitFeedback);

        contents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = contents.getText().toString();
                remain_letter.setText(input.length()+"/200");

                if(phone_num.getText().toString().length()>1 && contents.getText().toString().length()>1) {
                    submitFeedback.setBackgroundResource(R.drawable.round_red_solid);
                }
                else  submitFeedback.setBackgroundResource(R.drawable.btn_select_lang);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(phone_num.getText().toString().length()>1 && contents.getText().toString().length()>1) {
                    submitFeedback.setBackgroundResource(R.drawable.round_red_solid);
                }
                else  submitFeedback.setBackgroundResource(R.drawable.btn_select_lang);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(phone_num.getText().toString().length()>1 && contents.getText().toString().length()>1){
                    final ProgressHUD progressDialog = ProgressHUD.show(SubmitFeedBackActivity.this, "", true, false, SubmitFeedBackActivity.this);

                    model.saveFeedBack(phone_num.getText().toString(), contents.getText().toString());
                    model.getResult().observe(SubmitFeedBackActivity.this, mResult -> {
                        if(mResult.equals("success")){
                            progressDialog.dismiss();
                            Toast.makeText(SubmitFeedBackActivity.this, getResources().getString(R.string.feedback_submit_success),Toast.LENGTH_SHORT).show();
                        }
                        CommonUtils.dismissProgress(progressDialog);
                    });
                }
            }
        });

    }

    @Override
    public void onCancel(DialogInterface dialog) {

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