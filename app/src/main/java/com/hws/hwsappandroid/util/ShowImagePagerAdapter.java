package com.hws.hwsappandroid.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hws.hwsappandroid.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowImagePagerAdapter extends PagerAdapter {
    private static final String TAG = "ShopNewsPagerAdapter";
    private ArrayList<String> images;
    private Context context;
    private int index;

    public ShowImagePagerAdapter(Context context, ArrayList<String> images, int index) {
        this.context = context;
        this.images = images;
        this.index = index;
    }

    @Override
    public int getCount() {
        return images.size() * 100;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (View)object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int pos = position % images.size();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_shop_image, container , false);

        ImageView imageView = view.findViewById(R.id.view_finder);
        Picasso.get()
                .load(images.get(pos))
                .into(imageView);

        container.addView(view);

        return view;
    }

}
