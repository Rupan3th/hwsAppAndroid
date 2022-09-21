package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.ui.me.MeViewModel;

import java.util.Calendar;

public class PersonalInfoActivity extends AppCompatActivity {
    Calendar ca = Calendar.getInstance();
    int mYear = ca.get(Calendar.YEAR);
    int mMonth = ca.get(Calendar.MONTH);
    int mDay = ca.get(Calendar.DAY_OF_MONTH);

    TextView toolbar_personal_info;
    ImageButton btnBack, del_name;
    RelativeLayout more_btn;
    CardView personalInfoFrame;
    CardView NicknameFrame;
    Button cancel_button;
    Button confirm_button;
    EditText edit_Nickname;
    TextView TxtNickname, TxtGender, txt_birthday, TxtPhoneNum, naming_rules;
    String pkId, account, avatar_img, gender, birth, phone_num, nick_name;
    SharedPreferences pref;
    MeViewModel model;

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

        setContentView(R.layout.activity_personal_info);


        model = new ViewModelProvider(this).get(MeViewModel.class);

        pref = getSharedPreferences("user_info",MODE_PRIVATE);
        pkId = pref.getString("pkId","");
        account = pref.getString("account","");
        avatar_img = pref.getString("avatar_img","");
        nick_name = pref.getString("name","");
        phone_num = pref.getString("phone_num","");
        gender = pref.getString("gender","");
        birth = pref.getString("birth","");

        btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
                setResult(5);
                finish();
            }
        });

        toolbar_personal_info = findViewById(R.id.toolbar_personal_info);
        more_btn = findViewById(R.id.more_btn);

        TxtNickname = findViewById(R.id.TxtNickname);
        TxtGender = findViewById(R.id.TxtGender);
        txt_birthday = findViewById(R.id.txt_birthday);
        TxtPhoneNum = findViewById(R.id.TxtPhoneNum);
        naming_rules = findViewById(R.id.naming_rules);
        del_name = findViewById(R.id.del_name);
        edit_Nickname = findViewById(R.id.edit_Nickname);

        TxtNickname.setHint(nick_name);
        if(!gender.equals(""))   TxtGender.setText(gender);
        if(!birth.equals(""))   txt_birthday.setText(birth);
        TxtPhoneNum.setText(phone_num);

        personalInfoFrame = findViewById(R.id.personalInfoFrame);
        NicknameFrame = findViewById(R.id.NicknameFrame);
        cancel_button = findViewById(R.id.cancel_button);
        confirm_button = findViewById(R.id.confirm_button);

        LinearLayout Nickname = findViewById(R.id.Nickname);
        Nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoEditNickName();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TxtNickname.setText(nick_name);
                returnPersonalInfo();
            }
        });

        del_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_Nickname.setText("");
                edit_Nickname.setHint(getResources().getString(R.string.modify_nickname));
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edit_Nickname.getText().toString().equals("")){
                    nick_name = edit_Nickname.getText().toString();
                    TxtNickname.setText(nick_name);
                    TxtNickname.setHint("");

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("name", nick_name);
                    editor.commit();

                    returnPersonalInfo();
                }else {
                    edit_Nickname.setHint(getResources().getString(R.string.modify_nickname));
                }

            }
        });

        LinearLayout Gender = findViewById(R.id.Gender);
        Gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectGenderDialog();
            }
        });

        LinearLayout Birthday = findViewById(R.id.Birthday);
        Birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectBirthDialog();
            }
        });
    }

    public void gotoEditNickName(){
        personalInfoFrame.setVisibility(View.GONE);
//        toolbar_personal_info.setText(R.string.modify_nickname);
//        btnBack.setVisibility(View.GONE);
        more_btn.setVisibility(View.GONE);

        NicknameFrame.setVisibility(View.VISIBLE);
        naming_rules.setVisibility(View.VISIBLE);
//        cancel_button.setVisibility(View.VISIBLE);
        confirm_button.setVisibility(View.VISIBLE);

        edit_Nickname.setText(nick_name);
    }

    public void returnPersonalInfo(){
        personalInfoFrame.setVisibility(View.VISIBLE);
//        toolbar_personal_info.setText(R.string.personal_info);
//        btnBack.setVisibility(View.VISIBLE);
//        more_btn.setVisibility(View.VISIBLE);

        NicknameFrame.setVisibility(View.GONE);
//        cancel_button.setVisibility(View.GONE);
        confirm_button.setVisibility(View.GONE);
        naming_rules.setVisibility(View.GONE);
    }

    public void showSelectGenderDialog(){
        final Dialog selectGenderDialog = new Dialog(this);
        selectGenderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectGenderDialog.setContentView(R.layout.bottom_sheet_select_gender);

        TextView male = selectGenderDialog.findViewById(R.id.male);
        TextView female = selectGenderDialog.findViewById(R.id.female);
        male.setBackgroundColor(Color.parseColor("#F4F4F4"));
        gender = getString(R.string.male);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setBackgroundColor(Color.parseColor("#F4F4F4"));
                female.setBackgroundColor(Color.parseColor("#FFFFFF"));
                gender = getString(R.string.male);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setBackgroundColor(Color.parseColor("#FFFFFF"));
                female.setBackgroundColor(Color.parseColor("#F4F4F4"));
                gender = getString(R.string.female);
            }
        });

        Button cancel_button = selectGenderDialog.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectGenderDialog.dismiss();
            }
        });

        Button confirm_button = selectGenderDialog.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TxtGender.setText(gender);
                TxtGender.setHint("");

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("gender", gender);
                editor.commit();

                selectGenderDialog.dismiss();
            }
        });

        selectGenderDialog.show();
        selectGenderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectGenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectGenderDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        selectGenderDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void showSelectBirthDialog(){
        final Dialog selectBirthDialog = new Dialog(this);
        selectBirthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectBirthDialog.setContentView(R.layout.bottom_sheet_select_birth);

        DatePicker datePicker = selectBirthDialog.findViewById(R.id.datePicker);

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                String days;
                if (mMonth + 1 < 10) {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("年").append("0").
                                append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("年").append("0").
                                append(mMonth + 1).append("月").append(mDay).append("日").toString();
                    }

                } else {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("年").
                                append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("年").
                                append(mMonth + 1).append("月").append(mDay).append("日").toString();
                    }

                }

            }
        };

//        DatePickerDialog dp = new DatePickerDialog(PersonalInfoActivity.this, onDateSetListener, mYear, mMonth, mDay);
//        dp.show();
        Button cancel_button = selectBirthDialog.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBirthDialog.dismiss();
            }
        });

        Button confirm_button = selectBirthDialog.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth()+1;
                int year =  datePicker.getYear();
                birth = year + "-" + month + "-" + day;
                txt_birthday.setText(birth);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("birth", birth);
                editor.commit();

                selectBirthDialog.dismiss();
            }
        });

        selectBirthDialog.show();
        selectBirthDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectBirthDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectBirthDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        selectBirthDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void updateUserInfo(){
        String Gender;
        if(gender.equals("")) Gender = "0";
        else Gender = "1";

        model.updateUserInfo(pkId, account, avatar_img, birth, Gender, nick_name);
        model.getResult().observe(this, result -> {
            if(result.equals("Success"))
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
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