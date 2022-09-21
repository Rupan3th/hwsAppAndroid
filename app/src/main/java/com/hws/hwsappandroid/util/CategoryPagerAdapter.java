package com.hws.hwsappandroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.HomeCategory;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryPagerAdapter extends PagerAdapter {
    private static final String TAG = "CategoryPagerAdapter";
    ArrayList<HomeCategory> categories;
    private Context context;
    private int index;

    public CategoryPagerAdapter(Context context, ArrayList<HomeCategory> categories, int index) {
        this.context = context;
        this.categories = categories;
        this.index = index;
    }

    @Override
    public int getCount() {
        return (categories.size()-1)/9 + 1;
//        return 2;
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

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.home_category_per_page, container , false);

        TableLayout layoutHomeCategory = (TableLayout) view.findViewById(R.id.layout_home_category);
        for (int i=0; i<2; i++) {
            TableRow row = (TableRow) layoutHomeCategory.getChildAt(i);
            for (int j=0; j<5; j++) {
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.home_category_item, null, false);
                ImageView image = layout.findViewById(R.id.image_home_category);
                TextView textView = layout.findViewById(R.id.text_home_category);

                if (i==1 && j==4) {
                    // last category - all
                    image.setImageResource(R.drawable.view_all);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyGlobals.getInstance().setSelect_CategoryName("all");
                            BottomNavigationView bottomNavigationView = ((Activity) context).findViewById(R.id.nav_view);
                            bottomNavigationView.setSelectedItemId(R.id.navigation_classification);
                        }
                    });
                }else {
                    int idx = (i*5+j)  + (position*9);
                    if(idx < categories.size()){
                        HomeCategory category = categories.get(i*5+j  + position*9);
                        Picasso.get().load(category.img).resize(100, 100).into(image);
                        textView.setText(category.name);
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String categoryName = category.name;

                                MyGlobals.getInstance().setSelect_CategoryName(categoryName);
//                                BottomNavigationView bottomNavigationView = ((Activity) context).findViewById(R.id.nav_view);
//                                bottomNavigationView.setSelectedItemId(R.id.navigation_classification);
                                Intent i = new Intent(view.getContext(), ChooseCategoryActivity.class);
                                i.putExtra("level", category.level);
                                i.putExtra("categoryCode",category.code);
                                i.putExtra("categoryName",categoryName);
                                context.startActivity(i);
                            }
                        });
                    }else textView.setText("");

                }
                row.addView(layout);
            }
        }


        container.addView(view);

        return view;
    }
}
