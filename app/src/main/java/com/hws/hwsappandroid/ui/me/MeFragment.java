package com.hws.hwsappandroid.ui.me;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.hws.hwsappandroid.util.CommonUtils.getDpValue;
import static com.hws.hwsappandroid.util.CommonUtils.getPixelValue;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.GnssAntennaInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.components.carouselview.CarouselView;
import com.hws.hwsappandroid.components.carouselview.ViewListener;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.LiveChatContents;
import com.hws.hwsappandroid.model.RefundModel;
import com.hws.hwsappandroid.ui.AccountSettingActivity;
import com.hws.hwsappandroid.ui.AfterSalesListActivity;
import com.hws.hwsappandroid.ui.MyCollectionActivity;
import com.hws.hwsappandroid.ui.NewsListActivity;
import com.hws.hwsappandroid.ui.PersonalInfoActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.ui.ShippingAddressActivity;
import com.hws.hwsappandroid.ui.SubmitFeedBackActivity;
import com.hws.hwsappandroid.ui.VerifyPhoneActivity;
import com.hws.hwsappandroid.ui.home.HomeFragment;
import com.hws.hwsappandroid.ui.me.main.MyOrderActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentMeBinding;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.JWebSocketClient;
import com.hws.hwsappandroid.util.MyGlobals;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MeFragment extends Fragment implements ItemClickListener, JWebSocketClient.OnNewMessageListener {

    private FragmentMeBinding binding;
    private Uri mImageCaptureUri;
    private Uri mImageCropUri;
    public File currentPhotoFile;
    public String currentPhotoFileName;
    public String currentPhotoPath;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    ImageView imageMeAvatar, image_me_small;
    TextView register, text_me_name, notify_pendingPayment, notify_pendingShipment, notify_pendingReceipt, notify_complete;
    TextView notifyAfterSale, notifyFavorite, notifyContact, notifyTextView, toolbar_title;
    LinearLayout myInfo, tool_bar, curtain;
    CardView image_profile_small;
    ImageView bg;
    View tool_bar_boarder;
    private String absolutePath;
    private String NickName, phone;
    MeViewModel model;

    private RecyclerView recommended_products;
    private RecyclerViewAdapter mAdapter;
    SharedPreferences pref;
    int refund_num;

    private BottomSheetDialog bottomSheetDialog;
    private static final String SAMPLE_CAPTURED_IMAGE_NAME = "SampleCaptureImage.jpg";
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpg";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyGlobals.getInstance().setSelect_CategoryName("");
        super.onCreateView(inflater, container, savedInstanceState);

        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        pref = getActivity().getSharedPreferences("user_info",MODE_PRIVATE);
        String AuthToken = pref.getString("token", "");
        if(AuthToken.equals("")) {
            Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
            startActivity(i);
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        model = new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //안드로이드 버전확인
            //권한 허용이 됐는지 확인
            if(getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            }
            else { //권한 허용 요청
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        LinearLayout topLayout = binding.topLayout;
        topLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        tool_bar = binding.toolBar;
        tool_bar_boarder = binding.toolBarBoarder;
        image_profile_small = binding.imageProfileSmall;
        toolbar_title = binding.toolbarTitle;
        curtain = binding.curtain;
        bg = binding.meBg;

        notify_pendingPayment = binding.notifyWaitPay;
        notify_pendingShipment = binding.notifyWaitSend;
        notify_pendingReceipt = binding.notifyWaitGet;
//        notify_complete = binding.notifyCompleted;

        notifyAfterSale = binding.notifyAfterSale;
        notifyFavorite = binding.notifyFavorite;
        notifyContact = binding.notifyContact;

        pref = getActivity().getSharedPreferences("user_info",MODE_PRIVATE);
        NickName = pref.getString("name","");
        phone = pref.getString("phone_num","");

        text_me_name = binding.textMeName;
        text_me_name.setText(NickName);
        register = binding.register;
        myInfo = binding.myInfo;
        notifyTextView = binding.notify;
//        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                refreshView();
//            }
//        };
//        pref.registerOnSharedPreferenceChangeListener(listener);

        if(NickName.equals("")){
            register.setVisibility(View.VISIBLE);
            myInfo.setVisibility(View.GONE);
        }
        else {
            getMyOrderNumbers();

            getRefundNum();

//            getFavoriteNum();
        }

        imageMeAvatar = binding.imageMeAvatar;
        image_me_small = binding.imageMeSmall;
        if(!pref.getString("avatar_img","").equals("")) {
//            Picasso.get().load(pref.getString("avatar_img","")).into(imageMeAvatar);
            Glide.with(getContext()).load(pref.getString("avatar_img", "")).into(imageMeAvatar);
            Glide.with(getContext()).load(pref.getString("avatar_img", "")).into(image_me_small);
        }
        TextView phone_num = binding.phoneNum;
        try{
            phone_num.setText("手机号：" + phone.substring(0,3)+"****"+phone.substring(7) );
        }catch (Exception e){}

        CarouselView carouselView = binding.carouselView;
        carouselView.setPageCount(0);

        imageMeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                startActivityForResult(i, 5);
            }
        });

        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), PersonalInfoActivity.class);
                startActivityForResult(i, 5);
            }
        });

        binding.settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pref.getString("token","").equals("")){
                    Intent i = new Intent(getContext(), AccountSettingActivity.class);
                    startActivityForResult(i, 5);
                }else {
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }
            }
        });

        //notify
        ImageButton button_notify = binding.buttonNotify;
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
                    Intent i = new Intent(getContext(), NewsListActivity.class);
                    startActivity(i);
                }
            }
        });

        JWebSocketClient client = JWebSocketClient.getInstance(getContext());
        JWebSocketClient.addMessageListener(this);
        if (client != null) {
            int unreadCount = client.getUnreadCount();
            notifyTextView.setText("" + unreadCount);
            if (unreadCount > 0) notifyTextView.setVisibility(View.VISIBLE);
        }

        // buttons for my orders
        binding.buttonOrderAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
                    Intent i = new Intent(getContext(), MyOrderActivity.class);
                    i.putExtra("tab", 0);
                    startActivityForResult(i, 5);
                }
            }
        });
        binding.buttonWaitPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
                    Intent i = new Intent(getContext(), MyOrderActivity.class);
                    i.putExtra("tab", 1);
                    startActivityForResult(i, 5);
                }
            }
        });
        binding.buttonWaitSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
                    Intent i = new Intent(getContext(), MyOrderActivity.class);
                    i.putExtra("tab", 2);
                    startActivityForResult(i, 5);
                }
            }
        });
        binding.buttonWaitGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
                    Intent i = new Intent(getContext(), MyOrderActivity.class);
                    i.putExtra("tab", 3);
                    startActivityForResult(i, 5);
                }
            }
        });
//        binding.buttonCompleted.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(NickName.equals("")){
//                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
//                    startActivityForResult(i, 5);
//                }else {
//                    Intent i = new Intent(getContext(), MyOrderActivity.class);
//                    i.putExtra("tab", 4);
//                    startActivityForResult(i, 5);
//                }
//            }
//        });

        // buttons for my service
        binding.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
//                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(), MyCollectionActivity.class);
                    startActivityForResult(i, 5);
                }
            }
        });
        binding.buttonAfterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
//                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(), AfterSalesListActivity.class);
                    startActivityForResult(i, 5);
                }
            }
        });
        binding.buttonAddressManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
//                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(), ShippingAddressActivity.class);
                    startActivity(i);
                }
            }
        });
        binding.buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
//                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(), NewsListActivity.class);
                    startActivity(i);
                }
            }
        });
        binding.buttonFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NickName.equals("")){
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }else {
//                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(), SubmitFeedBackActivity.class);
                    startActivity(i);
                }
            }
        });
        binding.buttonEnterpriseBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonCreditCardManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonCustomerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });

        recommended_products = binding.recommendedProducts;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recommended_products.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), true, 0);
        mAdapter.setClickListener(this);
        recommended_products.setAdapter(mAdapter);

        if(NetworkCheck()) model.loadData();
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });

        binding.mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                int dpScrollY = getDpValue(getContext(),scrollY);
                if(dpScrollY < 56){
                    float alpha = ((float)dpScrollY )/ 56;
                    image_profile_small.setAlpha(alpha);
                    toolbar_title.setAlpha(alpha);
                    tool_bar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    tool_bar_boarder.setVisibility(View.GONE);
                }
                else {
                    tool_bar.setBackgroundColor(getResources().getColor(R.color.white));
                    tool_bar_boarder.setVisibility(View.VISIBLE);
                }

                if (scrollY > oldScrollY) {
//                    Log.i(TAG, "Scroll DOWN");

                    int scroll_h = getDpValue(getContext(), scrollY-oldScrollY);
                    int curtain_height = getDpValue(getContext(), curtain.getHeight());
                    int bg_height = getDpValue(getContext(), bg.getHeight());

                    if(curtain_height+scroll_h >= bg_height-84){
                        curtain.getLayoutParams().height = getPixelValue(getContext(), bg_height-84);
                    }else{
                        curtain.getLayoutParams().height = getPixelValue(getContext(), curtain_height+scroll_h);
                    }
                    curtain.requestLayout();

                }
                if (scrollY < oldScrollY) {
//                    Log.i(TAG, "Scroll UP");
                    int dpH = getDpValue(getContext(),scrollY);
                    int scroll_h = getDpValue(getContext(), oldScrollY-scrollY);
                    int curtain_height = getDpValue(getContext(), curtain.getHeight());
                    int bg_height = getDpValue(getContext(), bg.getHeight());

                    if(dpH < bg_height-84){
                        if(curtain_height-scroll_h < 0){
                            curtain.getLayoutParams().height = getPixelValue(getContext(), 0);
                        }else{
                            curtain.getLayoutParams().height = getPixelValue(getContext(), curtain_height-scroll_h);
                        }
                        curtain.requestLayout();
                    }

                }
                if (scrollY == 0) {
//                    Log.i(TAG, "TOP SCROLL");
//                    binding.toolbarHome.setVisibility(View.INVISIBLE);
                }
                if (scrollY >= 48) {
//                    binding.toolbarHome.setVisibility(View.VISIBLE);
                }
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });

        setupBottomSheetDialog();

        return root;
    }

    @Override
    public void onNewMessage(LiveChatContents chatMsg, int unreadCount) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView notifyTextView = binding.notify;
                notifyTextView.setText("" + unreadCount);
                if (unreadCount > 0) notifyTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        JWebSocketClient.removeMessageListener(this);
    }

    private void setupBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
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

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    try{
                        File imagePath = getActivity().getExternalFilesDir("images");
                        File newFile = new File(imagePath, SAMPLE_CAPTURED_IMAGE_NAME);
                        mImageCaptureUri = FileProvider.getUriForFile(getContext(),
                    getActivity().getApplicationContext().getPackageName() + ".fileprovider",
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
        if(requestCode==5)
        {
            refreshView();
        }

        if (resultCode == RESULT_OK) {
            switch(requestCode)
            {
                case PICK_FROM_ALBUM:
                {
                    try {
                        Uri uri = data.getData();
                        createImageFile();
                        UCrop.of(uri, mImageCropUri)
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(160, 160)
                                .start(getContext(), this);
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
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(160, 160)
                                .start(getContext(), this);
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

            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshView(){
        NickName = pref.getString("name","");

        if(NickName.equals("")){
            register.setVisibility(View.VISIBLE);
            myInfo.setVisibility(View.GONE);
        }else{
            register.setVisibility(View.GONE);
            myInfo.setVisibility(View.VISIBLE);
        }

        text_me_name.setText(NickName);

        getMyOrderNumbers();
        getRefundNum();
        //getFavoriteNum();
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

    public void fileUpload(File file){
        String url = "/fileUpload/upload";
        RequestParams params = new RequestParams();

        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        APIManager.postUpload(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        String avatar_img =  response.optString("data", "");
//                        Picasso.get().load(avatar_img).into(imageMeAvatar);
                        Glide.with(getActivity()).load(avatar_img).into(imageMeAvatar);
                        Glide.with(getActivity()).load(avatar_img).into(image_me_small);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("avatar_img", avatar_img);
                        editor.commit();

                        Intent i = new Intent(getContext(), PersonalInfoActivity.class);
                        startActivityForResult(i, 5);

                    } else {
                        Log.d("Home request", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Home request", ""+statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Home request", responseString);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getMyOrderNumbers(){
        model.GetOrderByStatus();
        model.getOrderNum().observe(this, orderNumByStatus -> {
            if (orderNumByStatus != null) {
                if(orderNumByStatus.pending_payment == 0){
                    notify_pendingPayment.setVisibility(View.INVISIBLE);
                }else notify_pendingPayment.setVisibility(View.VISIBLE);
                if(orderNumByStatus.pending_shipment == 0){
                    notify_pendingShipment.setVisibility(View.INVISIBLE);
                }else notify_pendingShipment.setVisibility(View.VISIBLE);
                if(orderNumByStatus.pending_receipt == 0){
                    notify_pendingReceipt.setVisibility(View.INVISIBLE);
                }else notify_pendingReceipt.setVisibility(View.VISIBLE);
//                if(orderNumByStatus.complete == 0){
//                    notify_complete.setVisibility(View.INVISIBLE);
//                }else notify_complete.setVisibility(View.VISIBLE);

                notify_pendingPayment.setText("" + orderNumByStatus.pending_payment);
                notify_pendingShipment.setText("" + orderNumByStatus.pending_shipment);
                notify_pendingReceipt.setText("" + orderNumByStatus.pending_receipt);
//                notify_complete.setText("" + orderNumByStatus.complete);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("pending_payment", ""+orderNumByStatus.pending_payment);
                editor.putString("pending_shipment", ""+orderNumByStatus.pending_shipment);
                editor.putString("pending_receipt", ""+orderNumByStatus.pending_receipt);
                editor.putString("complete", ""+orderNumByStatus.complete);
                editor.commit();
            }
        });
    }

    public void getRefundNum(){
//        refund_num = 0;
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject jsonParams = new JSONObject();
//                String url = "/bizGoodsRefund/queryRefundList";
//                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            if (response.getBoolean("status")) {
//                                JSONObject obj = response.getJSONObject("data");
//                                refund_num = obj.optInt("applyNum", 0);
//                                if(refund_num == 0 ){
//                                    notifyAfterSale.setVisibility(View.INVISIBLE);
//                                }
//                                else {
//                                    notifyAfterSale.setVisibility(View.VISIBLE);
//                                    notifyAfterSale.setText(""+ refund_num);
//                                }
//                            } else {
//                                Log.d("Home request", response.toString());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        Log.d("Home request", "" + statusCode);
////                        progressDialog.dismiss();
////                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        Log.d("Home request", responseString);
////                        progressDialog.dismiss();
////                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
        model.loadRefundList();
        model.getRefundList().observe(this, refundList -> {
            int refund_num = 0;
            for(int i=0; i<refundList.size(); i++){
                if(refundList.get(i).refundApplyStatus == 1)  refund_num++;
            }

            if(refund_num == 0 ){
                notifyAfterSale.setVisibility(View.INVISIBLE);
            }
            else {
                notifyAfterSale.setVisibility(View.VISIBLE);
                notifyAfterSale.setText(""+ refund_num);
            }
        });
    }

    public void getFavoriteNum(){
        model.loadFavoriteList();
        model.getFavoriteList().observe(this, favoriteGoods -> {
            if(favoriteGoods.size()== 0 ){
                notifyFavorite.setVisibility(View.INVISIBLE);
            }
            else {
                notifyFavorite.setVisibility(View.VISIBLE);
                notifyFavorite.setText(""+ favoriteGoods.size());
            }
        });
    }

    public void NoInternet() {
        binding.noInternet.setVisibility(View.VISIBLE);

        binding.card1.setVisibility(View.GONE);
        binding.card2.setVisibility(View.GONE);
        binding.card3.setVisibility(View.GONE);
        binding.recommend.setVisibility(View.GONE);
        binding.recommendedProducts.setVisibility(View.GONE);
    }

    public boolean NetworkCheck(){
        boolean isConnected = isNetworkConnected(getContext());
        if (!isConnected)
        {
            NoInternet();
        }
        return isConnected;
    }

    public boolean isNetworkConnected(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean bwimax = false;
        if (wimax != null)
        {
            bwimax = wimax.isConnected();
        }
        if (mobile != null)
        {
            if (mobile.isConnected() || wifi.isConnected() || bwimax)
            {
                return true;
            }
        }
        else
        {
            if (wifi.isConnected() || bwimax)
            {
                return true;
            }
        }
        return false;
    }

}