package com.hws.hwsappandroid.ui.home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.hws.hwsappandroid.ProductDetailActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.SearchActivity;
import com.hws.hwsappandroid.components.carouselview.CarouselView;
import com.hws.hwsappandroid.components.carouselview.ViewListener;
import com.hws.hwsappandroid.databinding.FragmentHomeBinding;
import com.hws.hwsappandroid.model.CourseModel;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements ItemClickListener {

    private FragmentHomeBinding binding;
    AppBarLayout appBarLayout;
    CarouselView carouselView;
    int lastAppbarOffset = 0;
    RecyclerView recyclerView;
    private HomeRecyclerViewAdapter mAdapter;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel model = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        appBarLayout = binding.homeAppbar;

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            boolean isCollapsed = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
//                if (offset == lastAppbarOffset) return;
                if (offset + scrollRange < 2 && !isCollapsed) {
                    Log.d("Home:", "Collapsing...,"+offset);
                    isCollapsed = true;
                    setAppBarLayoutOffset(-scrollRange, isCollapsed);
                } else if (offset + scrollRange >= 2 && isCollapsed) {
                    Log.d("Home:", "Expanding...,"+offset);
                    isCollapsed = false;
                    setAppBarLayoutOffset(0, isCollapsed);
                }
                lastAppbarOffset = offset;
            }
        });

        // carousel
        CarouselView carouselView = binding.carouselView;
        carouselView.setPageCount(0);

        model.loadData();
        model.getBanners().observe(getViewLifecycleOwner(), banners -> {
            carouselView.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(int position) {
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    Picasso.get().load(banners.get(position).bannerPic).fit().centerCrop()
                            .into(imageView);
                    return imageView;
                }
            });
            carouselView.setPageCount(banners.size());
        });

        // category
        model.getCategories().observe(getViewLifecycleOwner(), this::renderCategory);

        // topbar
        binding.searchbarHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
            }
        });
        binding.editTextHome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    binding.editTextHome.clearFocus();
                    Intent i = new Intent(getContext(), SearchActivity.class);
                    startActivity(i);
                }
            }
        });
        binding.searchbarHomeExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
            }
        });
        binding.editTextHomeCollapsed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    binding.editTextHomeCollapsed.clearFocus();
                    Intent i = new Intent(getContext(), SearchActivity.class);
                    startActivity(i);
                }
            }
        });

        // goods
        recyclerView = binding.recyclerView;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new HomeRecyclerViewAdapter(getContext(), true);
        recyclerView.setAdapter(mAdapter);
        model.getGoods().observe(getViewLifecycleOwner(), goods -> {
            mAdapter.setData(goods);
        });
        mAdapter.setClickListener(this);

        // fetch more goods when scroll reaches bottom
        binding.mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
//                    Log.i(TAG, "Scroll DOWN");
                }
                if (scrollY < oldScrollY) {
//                    Log.i(TAG, "Scroll UP");
                }
                if (scrollY == 0) {
//                    Log.i(TAG, "TOP SCROLL");
                }
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });
        return root;
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailImage = new Intent(getActivity(), ProductDetailActivity.class);
//        String imageName = courseModelArrayList.get(position).getCourse_name();
//        String productInfo = courseModelArrayList.get(position).getProductInfo();
//        String price = courseModelArrayList.get(position).getPrice();

//        detailImage.putExtra("imageName", imageName);
//
        startActivity(detailImage);

    }

    void setAppBarLayoutOffset(int offset, boolean isCollapsed) {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        behavior.setTopAndBottomOffset()
        appBarLayout.setExpanded(!isCollapsed);
        float dip = isCollapsed ? 0 : -48f ;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.toolbarHome, "translationY", px);
        animator.setDuration(100);
        animator.start();
    }

    void renderCategory(ArrayList<HomeCategory> categories) {
        int pixel = getResources().getDisplayMetrics().widthPixels;
        Log.d("Home", ""+pixel);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int i=0; i<2; i++) {
            TableRow row = (TableRow) binding.layoutHomeCategory.getChildAt(i);
            for (int j=0; j<5; j++) {
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.home_category_item, null, false);
                ImageView image = layout.findViewById(R.id.image_home_category);
                if (i==1 && j==4) {
                    // last category - all
                    image.setImageResource(R.drawable.view_all);
                } else {
                    HomeCategory category = categories.get(i*5+j);
                    Picasso.get().load(category.img).into(image);
                    TextView textView = layout.findViewById(R.id.text_home_category);
                    textView.setText(category.name);
                }
                row.addView(layout);
//                ViewGroup.LayoutParams params = layout.getLayoutParams();
//                params.width = pixel/5;
//                layout.setLayoutParams(params);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}