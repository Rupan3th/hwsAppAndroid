package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;

import com.cuneytayyildiz.gestureimageview.GestureImageView;
import com.hws.hwsappandroid.R;

public class ImageDetailActivity extends AppCompatActivity {
    private String imageName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        imageName = intent.getStringExtra("imageName");

        GestureImageView imageDetail = findViewById(R.id.view_finder);

        Bitmap bitmap = BitmapFactory.decodeFile(imageName);
        imageDetail.setImageBitmap(bitmap);
    }
}