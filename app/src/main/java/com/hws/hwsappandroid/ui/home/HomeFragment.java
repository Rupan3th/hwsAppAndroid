package com.hws.hwsappandroid.ui.home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.hws.hwsappandroid.databinding.FragmentHomeBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.ui.SearchActivity;
import com.hws.hwsappandroid.components.carouselview.CarouselView;
import com.hws.hwsappandroid.components.carouselview.ViewListener;
import com.hws.hwsappandroid.ui.classification.ClassificationFragment;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements ItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FragmentHomeBinding binding;
    AppBarLayout appBarLayout;
    CarouselView carouselView;
    int lastAppbarOffset = 0;
    RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
//    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

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

        drawerLayout = getActivity().findViewById(R.id.drawer_layout); // binding.myDrawerLayout;
        NavigationView navigationView = getActivity().findViewById(R.id.select_lang_navigation); // binding.selectLangNavigation;
        navigationView.setNavigationItemSelectedListener(this);
        Button lang_selectBtn = binding.langSelectBtn;
        lang_selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Slide slide = new Slide();
//                slide.setSlideEdge(Gravity.END);
//                TransitionManager.beginDelayedTransition(home_fragment, slide);
//                selectLangNav.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(GravityCompat.END);
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
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent pd = new Intent(getActivity(), ProductDetailActivity.class);
                            String gotoContent = banners.get(position).gotoContent;
                            startActivity(pd);
                        }
                    });
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
        mAdapter = new RecyclerViewAdapter(getContext(), true);
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
        Intent detailproduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String good_name = productInfo.goodsName;
        String good_sn = productInfo.goodsSn;
//        String imageName = courseModelArrayList.get(position).getCourse_name();
//        String productInfo = courseModelArrayList.get(position).getProductInfo();
//        String price = courseModelArrayList.get(position).getPrice();

//        detailImage.putExtra("imageName", imageName);
//
        startActivity(detailproduct);

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
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //goto classification
                            Bundle bundle = new Bundle();
                            bundle.putString("categoryName", "all");
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            ClassificationFragment CF = new ClassificationFragment();
                            CF.setArguments(bundle);
                            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, CF).commit();

                            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
//                            bottomNavigationView.setSelectedItemId(R.id.navigation_classification);
                            MenuItem item = bottomNavigationView.getMenu().findItem(R.id.navigation_classification);
                            item.setChecked(true);
                        }
                    });
                } else {
                    HomeCategory category = categories.get(i*5+j);
                    Picasso.get().load(category.img).into(image);
                    TextView textView = layout.findViewById(R.id.text_home_category);
                    textView.setText(category.name);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String categoryName = category.name;
                            //goto classification
                            Bundle bundle = new Bundle();
                            bundle.putString("categoryName", categoryName);
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            ClassificationFragment CF = new ClassificationFragment();
                            CF.setArguments(bundle);
                            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, CF).commit();

                            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
                            MenuItem item = bottomNavigationView.getMenu().findItem(R.id.navigation_classification);
                            item.setChecked(true);
                        }
                    });
                }
                row.addView(layout);
//                ViewGroup.LayoutParams params = layout.getLayoutParams();
//                params.width = pixel/5;
//                layout.setLayoutParams(params);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.lang_chinese:
                Toast.makeText(getContext(),R.string.chinese_simple,Toast.LENGTH_SHORT).show();
                break;
            case R.id.lang_korean:
                Toast.makeText(getContext(),R.string.korean,Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}