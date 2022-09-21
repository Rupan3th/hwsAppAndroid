package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.components.carouselview.CirclePageIndicator;
import com.hws.hwsappandroid.util.CrlPageIndicator;
import com.hws.hwsappandroid.util.ShowImagePagerAdapter;
import com.hws.hwsappandroid.util.emoji.CircleIndicator;

import java.util.ArrayList;

public class ImageDetailActivity extends AppCompatActivity {
    private String img_url="";
    private ViewPager pager;
    private ShowImagePagerAdapter pagerAdapter;
    private ArrayList<String> images;
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

        setContentView(R.layout.activity_image_detail);

//        decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        img_url = intent.getStringExtra("img_url");
        int index = intent.getIntExtra("index", 1);
        images = (ArrayList<String>) intent.getSerializableExtra("goodsDetailImg");

        pager = findViewById(R.id.pager_images);
        pagerAdapter = new ShowImagePagerAdapter(this, images, index);
        pager.setAdapter(pagerAdapter);

//        CrlPageIndicator mIndicator = findViewById(R.id.indicator);
//        mIndicator.setViewPager(pager);
//        mIndicator.setCurrentItem(index);
//        mIndicator.invalidate();

        pager.setCurrentItem(index + images.size()*50);
        pager.setOffscreenPageLimit(images.size());

//        ImageView imageDetail = findViewById(R.id.view_finder);
//
//        Picasso.get()
//                .load(img_url)
//                .into(imageDetail);

//        Bitmap bitmap = BitmapFactory.decodeFile(imageName);
//        imageDetail.setImageBitmap(bitmap);
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