package com.hws.hwsappandroid.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static com.hws.hwsappandroid.util.CommonUtils.getDpValue;
import static com.hws.hwsappandroid.util.CommonUtils.getPixelValue;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.databinding.FragmentHomeBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodClass;
import com.hws.hwsappandroid.model.LiveChatContents;
import com.hws.hwsappandroid.ui.NewsListActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.components.carouselview.CarouselView;
import com.hws.hwsappandroid.components.carouselview.ViewListener;
import com.hws.hwsappandroid.ui.VerifyPhoneActivity;
import com.hws.hwsappandroid.util.CategoryPagerAdapter;
import com.hws.hwsappandroid.util.ClassesListAdapter;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.CrlPageIndicator;
import com.hws.hwsappandroid.util.HN_ItemClickListener;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.JWebSocketClient;
import com.hws.hwsappandroid.util.MyGlobals;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment implements ItemClickListener, HN_ItemClickListener,  DialogInterface.OnCancelListener, JWebSocketClient.OnNewMessageListener {

    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    EditText editTextHome;
    private RecyclerViewAdapter mAdapter;
    private ClassesListAdapter classesAdapter;
    public DrawerLayout drawerLayout;

    LinearLayout searchbar_home_expanded, refresher;
    int c_width = 328;
    LinearLayout main_bar ;
    int c_height;
    ArrayList<GoodClass> home_class = new ArrayList<>();
    HomeViewModel model;
    private ViewPager pager_category;
    private CategoryPagerAdapter categoryAdapter;
    public String sel_Lang = "";
    private RefreshLayout swipeRefreshLayout;
    CarouselView carouselView;
    ProgressHUD progressDialog;
    SharedPreferences pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        pref = getActivity().getSharedPreferences("user_info",MODE_PRIVATE);

        swipeRefreshLayout = binding.swipeToRefresh;
        refresher = binding.refresher;

        drawerLayout = getActivity().findViewById(R.id.drawer_layout); // binding.myDrawerLayout;
        NavigationView navigationView = getActivity().findViewById(R.id.select_lang_navigation); // binding.selectLangNavigation;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
        });

        ImageButton lang_selectBtn = binding.langSelectBtn;
        lang_selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindow popup = new PopupWindow(getContext());
                View layout = getLayoutInflater().inflate(R.layout.popup_content, null);
                TextView Chinese = layout.findViewById(R.id.chinese);
                TextView Korean = layout.findViewById(R.id.korean);
                TextView English = layout.findViewById(R.id.english);
                sel_Lang = pref.getString("Lang", "");

                if(sel_Lang.equals("")){
                    Chinese.setTextColor(getResources().getColor(R.color.purple_500));
                    Korean.setTextColor(getResources().getColor(R.color.text_main));
                    English.setTextColor(getResources().getColor(R.color.text_main));
                }
                if(sel_Lang.equals("ko")){
                    Chinese.setTextColor(getResources().getColor(R.color.text_main));
                    Korean.setTextColor(getResources().getColor(R.color.purple_500));
                    English.setTextColor(getResources().getColor(R.color.text_main));
                }
                if(sel_Lang.equals("en")){
                    Chinese.setTextColor(getResources().getColor(R.color.text_main));
                    Korean.setTextColor(getResources().getColor(R.color.text_main));
                    English.setTextColor(getResources().getColor(R.color.purple_500));
                }

                Chinese.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Chinese.setTextColor(getResources().getColor(R.color.purple_500));
                        Korean.setTextColor(getResources().getColor(R.color.text_main));
                        English.setTextColor(getResources().getColor(R.color.text_main));

                        Locale locale = new Locale("");
                        Locale.setDefault(locale);

                        Configuration configuration = new Configuration();
                        configuration.locale = locale;
                        getActivity().getResources().updateConfiguration(configuration, getActivity().getResources().getDisplayMetrics());

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_info",MODE_PRIVATE).edit();
                        editor.putString("Lang", "");
                        editor.apply();

                        Intent intent = getActivity().getIntent();
//                        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().finish();
                        startActivity(intent);

                        popup.dismiss();
                    }
                });
                Korean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Korean.setTextColor(getResources().getColor(R.color.purple_500));
                        Chinese.setTextColor(getResources().getColor(R.color.text_main));
                        English.setTextColor(getResources().getColor(R.color.text_main));

                        Locale locale = new Locale("ko");
                        Locale.setDefault(locale);

                        Configuration configuration = new Configuration();
                        configuration.locale = locale;
                        getActivity().getResources().updateConfiguration(configuration, getActivity().getResources().getDisplayMetrics());

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_info",MODE_PRIVATE).edit();
                        editor.putString("Lang", "ko");
                        editor.apply();

                        Intent intent = getActivity().getIntent();
//                        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().finish();
                        startActivity(intent);

                        popup.dismiss();
                    }
                });
                English.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        English.setTextColor(getResources().getColor(R.color.purple_500));
                        Chinese.setTextColor(getResources().getColor(R.color.text_main));
                        Korean.setTextColor(getResources().getColor(R.color.text_main));

                        Locale locale = new Locale("en");
                        Locale.setDefault(locale);

                        Configuration configuration = new Configuration();
                        configuration.locale = locale;
                        getActivity().getResources().updateConfiguration(configuration, getActivity().getResources().getDisplayMetrics());

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_info",MODE_PRIVATE).edit();
                        editor.putString("Lang", "en");
                        editor.apply();

                        Intent intent = getActivity().getIntent();
//                        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().finish();
                        startActivity(intent);

                        popup.dismiss();
                    }
                });
                popup.setContentView(layout);
                popup.setTouchable(true);
                popup.setFocusable(true);
                popup.setBackgroundDrawable(new BitmapDrawable());
                popup.showAsDropDown(view, -200, 0);
            }
        });

        // carousel
        carouselView = binding.carouselView;
        carouselView.setPageCount(0);

        progressDialog = ProgressHUD.show(root.getContext(), "", true, false, HomeFragment.this);
        if(NetworkCheck()) model.loadData();
        else CommonUtils.dismissProgress(progressDialog);

        setBanner();

        //home  navigation
        RecyclerView classes = binding.classes;
        LinearLayoutManager linearHorizontal = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        classes.setLayoutManager(linearHorizontal);
        classesAdapter = new ClassesListAdapter(getContext());
        classes.setAdapter(classesAdapter);
        classesAdapter.setClickListener(this);
        model.getGoodClass().observe(getViewLifecycleOwner(), goodClasses -> {
            classesAdapter.setData(goodClasses);
            home_class = goodClasses;
        });

        // category
        pager_category = binding.pagerCategory;
        model.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if(categories.size()>0) CommonUtils.dismissProgress(progressDialog);

            categoryAdapter = new CategoryPagerAdapter(getContext(), categories, 0);
            pager_category.setAdapter(categoryAdapter);

            CrlPageIndicator mIndicator = binding.categoryIndicator;
            mIndicator.setViewPager(pager_category);
            mIndicator.setFillColor(getResources().getColor(R.color.purple_500));
            mIndicator.setCurrentItem(0);
            mIndicator.invalidate();

            pager_category.setCurrentItem(0);
            pager_category.setOffscreenPageLimit((categories.size()-1)/9 + 1);
        });

        // Search bar
        editTextHome = binding.editTextHome;
        editTextHome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    editTextHome.clearFocus();
                    Intent i = new Intent(getContext(), HomeSearchActivity.class);
                    startActivity(i);
                }
            }
        });
        binding.searchbarHomeExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), HomeSearchActivity.class);
                startActivity(i);
            }
        });

        //notify
        ImageButton button_notify = binding.buttonNotify;
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(APIManager.getAuthToken().length() > 5) {
                    Intent i = new Intent(getContext(), NewsListActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
                    startActivityForResult(i, 5);
                }
            }
        });

        TextView notifyTextView = binding.notify;
        JWebSocketClient client = JWebSocketClient.getInstance(getContext());
        JWebSocketClient.addMessageListener(this);
        if (client != null) {
            int unreadCount = client.getUnreadCount();
            notifyTextView.setText("" + unreadCount);
            if (unreadCount > 0) notifyTextView.setVisibility(View.VISIBLE);
        }

        // goods
        recyclerView = binding.recyclerView;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), true, 0);
        recyclerView.setAdapter(mAdapter);

        model.getGoods().observe(getViewLifecycleOwner(), goods -> {
            mAdapter.setData(goods);
        });
        mAdapter.setClickListener(this);

        // fetch more goods when scroll reaches bottom
        searchbar_home_expanded = binding.searchbarHomeExpanded;

        binding.mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {
                    int scroll_h = getDpValue(getContext(), scrollY-oldScrollY);
                    int s_width = getDpValue(getContext(), searchbar_home_expanded.getWidth());
                    int rbtns_width = 68;
                    int logo_img_width = 65;
                    if(s_width > c_width ) c_width = s_width;
                    int margin = rbtns_width + logo_img_width;

                    try{
                        ImageView mainLogo = binding.mainLogo;
                        float logo_alpha = mainLogo.getAlpha();
                        ImageView mainLogoImg = binding.mainLogoImg;
                        float logoImg_alpha = mainLogoImg.getAlpha();

                        ImageView logo_img = binding.logoImg;
                        int l_width = getDpValue(getContext(), logo_img.getWidth());

                        if(s_width > c_width-margin){
                            if(l_width+scroll_h*3 >= logo_img_width)    logo_img.getLayoutParams().width = getPixelValue(getContext(), logo_img_width);
                            else logo_img.getLayoutParams().width =  getPixelValue(getContext(), l_width+scroll_h*3);

                            if(s_width-scroll_h*6 <= c_width-margin){
                                searchbar_home_expanded.getLayoutParams().width = getPixelValue(getContext(), c_width-margin);
                            }else {
                                searchbar_home_expanded.getLayoutParams().width = getPixelValue(getContext(), s_width-scroll_h*6);
                            }
                            logo_alpha = logo_alpha-scroll_h*0.05f;
                            if(logo_alpha <= 0.0f) logo_alpha = 0.0f;
                            mainLogo.setAlpha(logo_alpha);
                            logoImg_alpha = logoImg_alpha + scroll_h*0.05f;
                            if(logoImg_alpha >= 1.0f) logoImg_alpha = 1.0f;
                            mainLogoImg.setAlpha(logoImg_alpha);
                        }
                        searchbar_home_expanded.requestLayout();

                        main_bar = binding.mainBar;
                        c_height = getDpValue(getContext(), main_bar.getHeight());
                        if(c_height > 72){
                            if(c_height-scroll_h <= 72){
                                main_bar.getLayoutParams().height = getPixelValue(getContext(), 72);
                            }else {
                                main_bar.getLayoutParams().height = getPixelValue(getContext(), c_height-scroll_h);
                            }
                            main_bar.requestLayout();
                        }

                        LinearLayout ghost_bar = binding.ghostBar;
                        int g_height = getDpValue(getContext(), ghost_bar.getHeight());
                        if(g_height > 0){
                            if(g_height-scroll_h <= 0){
                                ghost_bar.getLayoutParams().height = getPixelValue(getContext(), 0);
                            }else {
                                ghost_bar.getLayoutParams().height = getPixelValue(getContext(), g_height-scroll_h);
                            }
                            ghost_bar.requestLayout();
                        }
                    }
                    catch (Exception e){}

                }
                if (scrollY < oldScrollY) {
                    int dpH = getDpValue(getContext(),scrollY);
                    int scroll_h = getDpValue(getContext(), oldScrollY-scrollY);
                    int s_width = getDpValue(getContext(), searchbar_home_expanded.getWidth());

                    try{
                        ImageView logo_img = binding.logoImg;
                        int l_width = getDpValue(getContext(), logo_img.getWidth());

                        if(dpH < 72){
                            if(s_width < c_width && dpH < 38){
                                if(l_width-scroll_h*3 < 0)    logo_img.getLayoutParams().width = getPixelValue(getContext(), 0);
                                else logo_img.getLayoutParams().width =  getPixelValue(getContext(), l_width-scroll_h*3);

                                if(s_width+scroll_h*6 >= c_width){
                                    searchbar_home_expanded.getLayoutParams().width = getPixelValue(getContext(), c_width);
                                }else {
                                    searchbar_home_expanded.getLayoutParams().width = getPixelValue(getContext(), s_width+scroll_h*6);
                                }
                                searchbar_home_expanded.requestLayout();

                                ImageView mainLogo = binding.mainLogo;
                                float logo_alpha = mainLogo.getAlpha();
                                logo_alpha = logo_alpha + scroll_h*0.05f;
                                if(logo_alpha >= 1.0f) logo_alpha =1.0f;
                                mainLogo.setAlpha(logo_alpha);

                                ImageView mainLogoImg = binding.mainLogoImg;
                                float logoImg_alpha = mainLogoImg.getAlpha();
                                logoImg_alpha = logoImg_alpha - scroll_h*0.05f;
                                if(logoImg_alpha <= 0.0f) logoImg_alpha = 0.0f;
                                mainLogoImg.setAlpha(logoImg_alpha);
                            }

                            main_bar = binding.mainBar;
                            c_height = getDpValue(getContext(), main_bar.getHeight());
                            if(c_height < 112){
                                if(c_height+scroll_h >= 112){
                                    main_bar.getLayoutParams().height = getPixelValue(getContext(), 112);
                                }else {
                                    main_bar.getLayoutParams().height = getPixelValue(getContext(), c_height+scroll_h);
                                }
                                main_bar.requestLayout();
                            }

                            LinearLayout ghost_bar = binding.ghostBar;
                            int g_height = getDpValue(getContext(), ghost_bar.getHeight());
                            if(g_height < 36){
                                if(g_height + scroll_h >= 36){
                                    ghost_bar.getLayoutParams().height = getPixelValue(getContext(), 36);
                                }else {
                                    ghost_bar.getLayoutParams().height = getPixelValue(getContext(), g_height+scroll_h);
                                }
                                ghost_bar.requestLayout();
                            }
                        }
                    }catch (Exception e){}

                }
                if (scrollY == 0 && scrollY < oldScrollY) {
//
                }
                if (scrollY >= 48) {
//
                }
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });

        LinearLayout hamburger = binding.hamburger;
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyGlobals.getInstance().setSelect_CategoryName("all");
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_classification);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.loadData();
                setBanner();
                model.getGoods().observe(getViewLifecycleOwner(), goods -> {
                    mAdapter.setData(goods);
                    if(goods.size() > 0){

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefresh();
                            }
                        }, 1000);
                    }
                });

            }
        });

        return root;
    }


    public void setBanner(){
        model.getBanners().observe(getViewLifecycleOwner(), banners -> {
            if(banners!=null){
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
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banners.get(position).gotoContent));
                                startActivity(browserIntent);
                            }
                        });
                        imageView.setBackgroundColor(R.drawable.round_white_solid);
                        return imageView;
                    }
                });
                carouselView.setPageCount(banners.size());
            }
            else{
                NoInternet();
                CommonUtils.dismissProgress(progressDialog);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

    @Override
    public void onItemClick(View view, int position) {
        for(int i=0; i<home_class.size(); i++){
            home_class.get(i).selected = false;
        }
        home_class.get(position).selected = true;
        classesAdapter.setData(home_class);
        MyGlobals.getInstance().setSelect_CategoryName(home_class.get(position).categoryName);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_classification);
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

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void NoInternet(){
        binding.noInternet.setVisibility(View.VISIBLE);

        binding.bannerBg.setVisibility(View.VISIBLE);
        binding.mainBar.setVisibility(View.VISIBLE);
        binding.ghostBar.setVisibility(View.GONE);
        binding.searchLayer.setVisibility(View.GONE);
        binding.mainScrollView.setVisibility(View.GONE);

    }

    public boolean NetworkCheck(){
        boolean isConnected = isNetworkConnected(getContext());
        if (!isConnected)
        {
            NoInternet();
        }
       return isConnected;
    }

    public boolean isNetworkConnected(Context context)    {
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