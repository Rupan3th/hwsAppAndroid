package com.hws.hwsappandroid.ui;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.RefundSubmitModel;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ConfirmDialogView;
import com.hws.hwsappandroid.util.KindTipsView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

public class RequestRefundActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    private Uri mImageCaptureUri;
    private Uri mImageCropUri;
    public File currentPhotoFile;
    public String currentPhotoFileName;
    public String currentPhotoPath;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    public LinearLayout select_app_Type, select_receipt_status, select_reason_apply;
    public TextView reason_apply, text_product_info, product_option, text_price, text_amount, application_type, receipt_status;
    public TextView total_price, application_amount, application_notify, receive_phone, remain_letter;
    public ImageView image1, image2, image3, image4, image_product, imageUpload;
    public EditText edit_refund_Explain;
    public RadioGroup select_received, select_applyType;
    public RadioButton radio_not_received, radio_received, radio_return, radio_refundOnly;
    public CardView refund_Explain;
    public Button submitRefundBtn;

    public String reason_refund="";
    public String orderId, phone, totalMoney;
    public String goodsId,goodsSpecId,goodsPic,goodsName,goodsSpec,goodsPrice;
    public int goodsNum, reason_No = 1, image_num = 0;

    public RefundSubmitModel refundSubmitData = new RefundSubmitModel();
    public ArrayList<String> image_files = new ArrayList<>();

    String[] refund_reason = {"",};
    String[] refund_type = {"",};
    String[] receiving_status = {"",};

    public RequestRefundModel model;
    private BottomSheetDialog bottomSheetDialog;
    private static final String SAMPLE_CAPTURED_IMAGE_NAME = "SampleCaptureImage.jpg";
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpg";

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

        setContentView(R.layout.activity_request_refund);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        image_files.add("https://huawushang.oss-cn-beijing.aliyuncs.com/hws/goodsMainPic/P202206211111543249.jpg");
        refundSubmitData.files = image_files;

        refund_reason = getResources().getStringArray(R.array.refund_reason);
        refund_type = getResources().getStringArray(R.array.refund_type);
        receiving_status = getResources().getStringArray(R.array.receiving_status);

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        phone = pref.getString("phone_num","");

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        totalMoney = intent.getStringExtra("goodsPrice");
        goodsId = intent.getStringExtra("goodsId");
        goodsSpecId = intent.getStringExtra("goodsSpecId");
        goodsPic = intent.getStringExtra("goodsPic");
        goodsName = intent.getStringExtra("goodsName");
        goodsSpec = intent.getStringExtra("goodsSpec");
        goodsPrice = intent.getStringExtra("goodsPrice");
        goodsNum= intent.getIntExtra("goodsNum", 1);

        refundSubmitData.goodsId = goodsId;
        refundSubmitData.goodsSpecId = goodsSpecId;
        refundSubmitData.orderId = orderId;
        refundSubmitData.phone = phone;


        model = new ViewModelProvider(this).get(RequestRefundModel.class);

        image_product = findViewById(R.id.image_product);
        text_product_info = findViewById(R.id.text_product_info);
        product_option = findViewById(R.id.product_option);
        text_price = findViewById(R.id.text_price);
        text_amount = findViewById(R.id.text_amount);
        select_app_Type = findViewById(R.id.select_app_Type);
        select_receipt_status = findViewById(R.id.select_receipt_status);
        select_reason_apply = findViewById(R.id.select_reason_apply);
        application_type = findViewById(R.id.application_type);
        receipt_status = findViewById(R.id.receipt_status);
        reason_apply = findViewById(R.id.reason_apply);
        total_price = findViewById(R.id.total_price);
        application_amount = findViewById(R.id.application_amount);
        application_notify = findViewById(R.id.application_notify);
        receive_phone = findViewById(R.id.receive_phone);
        edit_refund_Explain = findViewById(R.id.edit_refund_Explain);
        remain_letter = findViewById(R.id.remain_letter);
        imageUpload = findViewById(R.id.imageUpload);
        refund_Explain = findViewById(R.id.refund_Explain);
        select_received = findViewById(R.id.select_received);
        select_applyType = findViewById(R.id.select_applyType);
        radio_not_received = findViewById(R.id.radio_not_received);
        radio_received = findViewById(R.id.radio_received);
        radio_return = findViewById(R.id.radio_return);
        radio_refundOnly = findViewById(R.id.radio_refundOnly);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Picasso.get()
                .load(goodsPic)
                .resize(600, 600)
                .into(image_product);
        text_product_info.setText(goodsName);
        product_option.setText(goodsSpec);
        text_price.setText(goodsPrice);
        text_amount.setText("x" + goodsNum);
        total_price.setText("￥" + totalMoney);
        application_amount.setText(totalMoney);
        application_notify.setText("可修改，最多不能超过￥" + totalMoney);
        receive_phone.setText(phone);

        refundSubmitData.receivingStatus = 0;
        refundSubmitData.refundType = 0;

//        if(radio_not_received.isChecked())   refundSubmitData.receivingStatus = 0;
//        else  refundSubmitData.receivingStatus = 1;
//        if(radio_return.isChecked())    refundSubmitData.refundType = 0;
//        else   refundSubmitData.refundType = 1;

        select_received.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("chk", "id" + checkedId);
                if (checkedId == R.id.radio_return) {
                    refundSubmitData.receivingStatus = 0;
                } else if (checkedId == R.id.radio_received) {
                    refundSubmitData.receivingStatus = 1;
                }
            }
        });
        select_applyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("chk", "id" + checkedId);
                if (checkedId == R.id.radio_not_received) {
                    refundSubmitData.refundType = 0;
                } else if (checkedId == R.id.radio_refundOnly) {
                    refundSubmitData.refundType = 1;
                }
            }
        });

        refundSubmitData.refundMoney = application_amount.getText().toString();
        ImageButton edit_money_btn = findViewById(R.id.edit_money_btn);
        edit_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RequestRefundActivity.this);
                alert.setTitle(R.string.refund_amount);
                alert.setMessage("可修改，最多不能超过￥" + totalMoney);

                final EditText money = new EditText(RequestRefundActivity.this);
                money.setText(totalMoney);
                money.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(money);

                alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        float myInputNum = 0;
                        float myTotalMoney = 0;
                        try {
                            myInputNum = Float.parseFloat(money.getText().toString());
                            myTotalMoney= Float.parseFloat(totalMoney);
                        } catch(NumberFormatException nfe) {
                            System.out.println("Could not parse " + nfe);
                        }

                        if(myInputNum > myTotalMoney){
                            application_amount.setText(totalMoney);
                            refundSubmitData.refundMoney = totalMoney;
                        }else{
                            application_amount.setText(money.getText().toString());
                            refundSubmitData.refundMoney = money.getText().toString();
                        }
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });

        ///Edit receive phone
        receive_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RequestRefundActivity.this);
                alert.setTitle("更改联系电话");

                final EditText phone = new EditText(RequestRefundActivity.this);
                phone.setInputType(InputType.TYPE_CLASS_PHONE);
                phone.setText(refundSubmitData.phone);
                alert.setView(phone);

                alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newPhone = phone.getText().toString();
                        if(isMobileNO(newPhone)) {
                            receive_phone.setText(newPhone);
                            refundSubmitData.phone = newPhone;
                        }else {
                            Toast.makeText(RequestRefundActivity.this, "请输入有效的手机号码！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });

        ///Input detailDescription
        edit_refund_Explain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = edit_refund_Explain.getText().toString();
                remain_letter.setText(input.length()+"/170");
                refundSubmitData.detailDescription = input;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /// Upload Image
        image1 = findViewById(R.id.image1);
//        image1.getLayoutParams().width = image_width;
//        image1.requestLayout();
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_num = image_num + 1;
                if(image_num == 4) imageUpload.setVisibility(View.GONE);
                bottomSheetDialog.show();
                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
            }
        });

        /// refund Type
        select_app_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectApplicationType();
            }
        });

        /// receipt status
        select_receipt_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectReceiptStatus();
            }
        });

        /// Refund reason
        select_reason_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectReasonDialog();
            }
        });

        submitRefundBtn = findViewById(R.id.submitRefundBtn);
        submitRefundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refundSubmitData.detailDescription!=null && refundSubmitData.detailDescription.length()>0) showConfirmDialog(3);
                else  Toast.makeText(RequestRefundActivity.this, getResources().getString(R.string.edit_reason_refund_hint), Toast.LENGTH_SHORT).show();
            }
        });

        setupBottomSheetDialog();

    }

    public void showSelectApplicationType(){
        final Dialog selectDialog = new Dialog(this);
        selectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectDialog.setContentView(R.layout.bottom_sheet_apply_type);

        RadioGroup radioGroup = (RadioGroup) selectDialog.findViewById(R.id.select_applyType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("chk", "id" + checkedId);
                if (checkedId == R.id.radio_return) {
                    refundSubmitData.refundType = 0;
                } else if (checkedId == R.id.radio_refundOnly) {
                    refundSubmitData.refundType = 1;
                }
            }
        });
        Button confirmBtn = (Button) selectDialog.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                application_type.setText(refund_type[refundSubmitData.refundType]);
                selectDialog.dismiss();
            }
        });
        selectDialog.show();
        selectDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        selectDialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    public void showSelectReceiptStatus(){
        final Dialog selectDialog = new Dialog(this);
        selectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectDialog.setContentView(R.layout.bottom_sheet_receipt_status);

        RadioGroup radioGroup = (RadioGroup) selectDialog.findViewById(R.id.select_received);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("chk", "id" + checkedId);
                if (checkedId == R.id.radio_not_received) {
                    refundSubmitData.receivingStatus = 0;
                } else if (checkedId == R.id.radio_received) {
                    refundSubmitData.receivingStatus = 1;
                }
            }
        });
        Button confirmBtn = (Button) selectDialog.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receipt_status.setText(receiving_status[refundSubmitData.receivingStatus]);
                selectDialog.dismiss();
            }
        });
        selectDialog.show();
        selectDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        selectDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showSelectReasonDialog() {
        final Dialog selectReasonDialog = new Dialog(this);
        selectReasonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectReasonDialog.setContentView(R.layout.bottom_sheet_reason_refund);

//        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMarginStart(20);
//        layoutParams.setMarginEnd(20);

        RadioGroup radioGroup = (RadioGroup) selectReasonDialog.findViewById(R.id.select_reason);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        RadioButton taking_wrong = (RadioButton) selectReasonDialog.findViewById(R.id.taking_wrong);

        Button confirmBtn = (Button) selectReasonDialog.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectReasonDialog.dismiss();

                reason_apply.setText(reason_refund);
                refundSubmitData.refundReason = reason_No;

                if(taking_wrong.isChecked()) {
                    refundSubmitData.refundReason = 1;
                    reason_apply.setText(R.string.taking_wrong);
                }
            }
        });

        selectReasonDialog.show();
        selectReasonDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectReasonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectReasonDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        selectReasonDialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

            if(i == R.id.taking_wrong){
                reason_refund = getString(R.string.taking_wrong);
                reason_No = 1;
//                reason_apply.setText(R.string.taking_wrong);
            }
            else if(i == R.id.material_not_match){
                reason_refund = getString(R.string.material_does_not_match);
                reason_No =2;
//                reason_apply.setText(R.string.material_does_not_match);
            }
            else if(i == R.id.size_not_match){
                reason_refund = getString(R.string.size_does_not_match);
                reason_No = 3;
//                reason_apply.setText(R.string.size_does_not_match);
            }
            else if(i == R.id.sent_wrong_item){
                reason_refund = getString(R.string.sent_wrong_item);
                reason_No = 4;
//                reason_apply.setText(R.string.sent_wrong_item);
            }
            else if(i == R.id.counterfeit_brand){
                reason_refund = getString(R.string.counterfeit_brand);
                reason_No = 5;
//                reason_apply.setText(R.string.counterfeit_brand);
            }
            else if(i == R.id.broken_or_stained){
                reason_refund = getString(R.string.small_pieces_broken_or_stained);
                reason_No = 6;
//                reason_apply.setText(R.string.small_pieces_broken_or_stained);
            }
            else if(i == R.id.rough_flawed){
                reason_refund = getString(R.string.rough_flawed_workmanship);
                reason_No = 7;
//                reason_apply.setText(R.string.rough_flawed_workmanship);
            }
            else if(i == R.id.date_not_match){
                reason_refund = getString(R.string.manufacturing_date_does_not_match);
                reason_No = 8;
//                reason_apply.setText(R.string.manufacturing_date_does_not_match);
            }
            else if(i == R.id.color_not_match){
                reason_refund = getString(R.string.color_style_does_not_match);
                reason_No = 9;
//                reason_apply.setText(R.string.color_style_does_not_match);
            }
            else {
                reason_refund = getString(R.string.taking_wrong);
                reason_No = 1;
//                reason_apply.setText(R.string.taking_wrong);
            }
        }
    };

    private void setupBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_select_photo);
        TextView camera = bottomSheetDialog.findViewById(R.id.camera);
        TextView album = bottomSheetDialog.findViewById(R.id.album);
        TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                try{
//                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//                    intent.putExtra("return-data", true);
//                    startActivityForResult(intent, PICK_FROM_CAMERA);
//                }catch (Exception e){}

                if (intent.resolveActivity(getPackageManager()) != null) {
                    try{
                        File imagePath = getExternalFilesDir("images");
                        File newFile = new File(imagePath, SAMPLE_CAPTURED_IMAGE_NAME);
                        mImageCaptureUri = FileProvider.getUriForFile(RequestRefundActivity.this,
                                getApplicationContext().getPackageName() + ".fileprovider",
                                newFile);
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                bottomSheetDialog.dismiss();
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
                bottomSheetDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName =  "JPEG_" + timeStamp + "_";

//        File imagePath = getActivity().getExternalFilesDir("images"); //images 디렉토리가 없을시 생성한다.
        File imagePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "hwawuxiang"); //images 디렉토리가 없을시 생성한다.
        if (!imagePath.exists()) {
            imagePath.mkdir();
        }
        File newFile = new File(imagePath, SAMPLE_CROPPED_IMAGE_NAME);

        currentPhotoFile = newFile;
        currentPhotoFileName = newFile.getName();
        currentPhotoPath = newFile.getAbsolutePath(); //파일의 절대패스를 저장한다.

        try {
//            mImageCaptureUri = FileProvider.getUriForFile(getContext(),
//                    getActivity().getApplicationContext().getPackageName() + ".fileprovider",
//                    newFile);
            mImageCropUri = Uri.fromFile(newFile);
        } catch (Exception ex) {
            Log.d("FileProvider", ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }

        return newFile;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            switch(requestCode)
            {
                case PICK_FROM_ALBUM:
                {
                    try {
                        Uri uri = data.getData();
                        createImageFile();
                        UCrop.of(uri, mImageCropUri)
                                .withAspectRatio(3, 4)
                                .withMaxResultSize(160, 160)
                                .start(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//
//                intent.putExtra("outputX", 300); // CROP한 이미지의 x축 크기
//                intent.putExtra("outputY", 300); // CROP한 이미지의 y축 크기
//                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
//                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
//                intent.putExtra("scale", true);
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동
                    break;
                }
                case PICK_FROM_CAMERA:
                {
                    try {
                        createImageFile();
                        UCrop.of(mImageCaptureUri, mImageCropUri)
                                .withAspectRatio(3, 4)
                                .withMaxResultSize(160, 160)
                                .start(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    fileUpload(currentPhotoFile);
                    break;
                }

                case UCrop.REQUEST_CROP:
                {
                    try {
                        File f = new File(mImageCropUri.getPath());
                        fileUpload(f);
//                        if (f.exists())
//                        {
//                            f.delete();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Log.d("Image Crop", "Failed");
        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel = new File(dirPath);
        if(!directory_SmartWheel.exists())
            directory_SmartWheel.mkdir();
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;
        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri galleryAddPic(Uri srcImageFileUri ,String srcImageFileName) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, srcImageFileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/MyImages"); // 두개의 경로[DCIM/ , Pictures/]만 가능함 , 생략시 Pictures/ 에 생성됨
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 1); //다른앱이 파일에 접근하지 못하도록 함(Android 10 이상)
        Uri newImageFileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        try {
            AssetFileDescriptor afdInput = contentResolver.openAssetFileDescriptor(srcImageFileUri, "r");
            AssetFileDescriptor afdOutput = contentResolver.openAssetFileDescriptor(newImageFileUri, "w");
            FileInputStream fis = afdInput.createInputStream();
            FileOutputStream fos = afdOutput.createOutputStream();

            byte[] readByteBuf = new byte[1024];
            while(true){
                int readLen = fis.read(readByteBuf);
                if (readLen <= 0) {
                    break;
                }
                fos.write(readByteBuf,0,readLen);
            }

            fos.flush();
            fos.close();
            afdOutput.close();

            fis.close();
            afdInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentValues.clear();
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0); //다른앱이 파일에 접근할수 있도록 함
        contentResolver.update(newImageFileUri, contentValues, null, null);
        return newImageFileUri;
    }

    public void fileUpload(File file){
        final ProgressHUD progressDialog = ProgressHUD.show(RequestRefundActivity.this, "", true, false, RequestRefundActivity.this);

        String url = "/bizGoodsRefund/uploadFile";
        RequestParams params = new RequestParams();

        try {
            params.put("file", file);
        } catch(FileNotFoundException e) {}

        APIManager.postUpload(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        String goods_img =  response.optString("data", "");
                        image_files.add(goods_img);
                        refundSubmitData.files = image_files;
                        if(image_num  == 1)   Glide.with(getApplicationContext()).load(goods_img).into(image1); //  Picasso.get().load(goods_img).into(image1);
                        if(image_num  == 2)   Glide.with(getApplicationContext()).load(goods_img).into(image2);
                        if(image_num  == 3)   Glide.with(getApplicationContext()).load(goods_img).into(image3);
                        if(image_num  == 4)   Glide.with(getApplicationContext()).load(goods_img).into(image4);
                        progressDialog.dismiss();
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

    public void submitRefund(RefundSubmitModel refundSubmitData){
        final ProgressHUD progressDialog = ProgressHUD.show(RequestRefundActivity.this, "", true, false, RequestRefundActivity.this);

        model.submit(refundSubmitData);
        model.getResult().observe(this, result -> {
            if(result.equals("success")){
                Intent i = new Intent(RequestRefundActivity.this, AfterSalesListActivity.class);
//                i.putExtra("orderId", myOrderModel.pkId);
                startActivity(i);
                finish();
                progressDialog.dismiss();
            }
            else
                Toast.makeText(RequestRefundActivity.this, getResources().getString(R.string.failed_submit), Toast.LENGTH_SHORT).show();
                CommonUtils.dismissProgress(progressDialog);
        });
        CommonUtils.dismissProgress(progressDialog);
        Toast.makeText(RequestRefundActivity.this, getResources().getString(R.string.non_refundable), Toast.LENGTH_SHORT).show();
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
        confirm.setText(getResources().getString(R.string.confirm_submission));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRefund(refundSubmitData);
                confirmDialog.dismiss();
            }
        });

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(view);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rect displayRectangle = new Rect();
        Window window = confirmDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        confirmDialog.getWindow().setLayout(CommonUtils.getPixelValue(this, 303), ViewGroup.LayoutParams.WRAP_CONTENT);

        confirmDialog.show();
//        confirmDialog.getWindow().setGravity(Gravity.BOTTOM);
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