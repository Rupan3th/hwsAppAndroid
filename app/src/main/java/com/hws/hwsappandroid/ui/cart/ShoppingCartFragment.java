package com.hws.hwsappandroid.ui.cart;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentShoppingCartBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SwipeSectionItemModel;
import com.hws.hwsappandroid.model.UserCartItem;
import com.hws.hwsappandroid.ui.CartSettlementActivity;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.OrderDetailsActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.ui.SearchActivity;
import com.hws.hwsappandroid.ui.VerifyPhoneActivity;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.MyGlobals;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.hws.hwsappandroid.util.SectionRecyclerViewSwipeAdapter;
import com.hws.hwsappandroid.model.SwipeSectionModel;
import com.hws.hwsappandroid.util.ShoppingCartListAdapter;
import com.hws.hwsappandroid.util.SwipeController;
import com.hws.hwsappandroid.util.SwipeControllerActions;

import java.io.File;
import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment implements ItemClickListener {
    public View view;
    private FragmentShoppingCartBinding binding;
//    private RecyclerViewType recyclerViewType = RecyclerViewType.LINEAR_VERTICAL;
    private RecyclerView recyclerView, recommended_products;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";
    ShoppingCartModel model;
    ShoppingCartListAdapter adapter;
    private RecyclerViewAdapter mAdapter;
    ArrayList<UserCartItem> mShoppingCart = new ArrayList<>();
    TextView totalPrice, total_num;
    Button toSettleBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View decorView = requireActivity().getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        SharedPreferences pref = getActivity().getSharedPreferences("user_info",MODE_PRIVATE);
        String AuthToken = pref.getString("token", "");
        if(AuthToken.equals("")) {
            Intent i = new Intent(getContext(), VerifyPhoneActivity.class);
            startActivity(i);
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        MyGlobals.getInstance().set_format_Total_price();
        MyGlobals.getInstance().set_format_Total_num();

        model = new ViewModelProvider(this).get(ShoppingCartModel.class);

        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        toolbar = binding.toolbarShoppingCart;
//        final TextView textView = binding.txtShoppingCart;
//        shoppingCartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        ImageButton more = binding.more;
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button gotoHome = binding.gotoHome;
        gotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        });

        totalPrice = binding.totalPrice;
        total_num = binding.totalNum;

        setUpRecyclerView();
        populateRecyclerView();

        recommended_products = binding.recommendedProducts;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recommended_products.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), true, 2);
        mAdapter.setClickListener(this);
        recommended_products.setAdapter(mAdapter);

        if(NetworkCheck()) model.loadRecommendData();

        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });

        model.getSelectedGoodsNum().observe(this, goodsNum -> {
            MyGlobals.getInstance().set_Total_num(goodsNum);
            toSettleBtn.setText("去结算(" + goodsNum + ")");
        });

        model.getSelectedTotalPrice().observe(this, t_Price -> {
            MyGlobals.getInstance().set_Total_price(t_Price);
            totalPrice.setText(String.format("%.2f", t_Price));
        });

        model.isAllSelected().observe(this, allSelected -> {
            binding.checkboxAll.setChecked(allSelected);
        });

        binding.mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
//                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
            }
        });

        toSettleBtn = binding.toSettleBtn;
        toSettleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyGlobals.getInstance().getTotal_num() > 0){
                    Intent i = new Intent(getContext(), CartSettlementActivity.class);
//                      i.putExtra("searchWord", s);
                    startActivity(i);
                }
            }
        });

        return root;
    }

    private void setUpRecyclerView() {
        recyclerView = binding.sectionedRecyclerSwipe;
//        recyclerView = (RecyclerView) findViewById(R.id.sectioned_recycler_swipe);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void populateRecyclerView() {
        adapter = new ShoppingCartListAdapter(getContext());
        adapter.setShoppingCartModel(model);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
//                    toolbar.setVisibility(View.INVISIBLE);
                } else if (!recyclerView.canScrollVertically(1)) {

                } else {
//                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        if(NetworkCheck()) model.loadData();
        model.getMyCart().observe(getViewLifecycleOwner(), mCarts -> {
            if(mCarts.size()==0){
                binding.cartIsEmpty.setVisibility(View.VISIBLE);
                binding.bottomCtr.setVisibility(View.GONE);
                binding.selectLine.setVisibility(View.GONE);
            }else {
                binding.bottomCtr.setVisibility(View.VISIBLE);
                binding.selectLine.setVisibility(View.VISIBLE);
                binding.cartIsEmpty.setVisibility(View.GONE);
            }

            adapter.setData(mCarts);
            mShoppingCart = mCarts;
            MyGlobals.getInstance().setMyShoppingCart(mShoppingCart);

            int total_goods = 0;
            for(int i=0; i<mCarts.size(); i++){
                total_goods = total_goods + mCarts.get(i).goods.size();
            }
            total_num.setText(" " + total_goods);
            MyGlobals.getInstance().setNotify_cart(total_goods);
        });

        CheckBox checkbox_all = binding.checkboxAll;
        checkbox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                float t_price = 0.0f;
//                int t_num = 0;
                ArrayList<String> pkIds = new ArrayList<>();

                if(checkbox_all.isChecked()){
                    for(int i=0; i<mShoppingCart.size(); i++){
                        UserCartItem cartItem = mShoppingCart.get(i);
                        cartItem.selected = true;
                        for(int j=0; j<cartItem.goods.size(); j++){
                            GoodOfShoppingCart shoppingCart = cartItem.goods.get(j);
                            shoppingCart.selected = true;
//                            t_price =  t_price + Float.parseFloat(shoppingCart.goodsPrice)*shoppingCart.goodsNum;
//                            t_num = t_num + shoppingCart.goodsNum;
                            pkIds.add(shoppingCart.pkId);
                        }
                    }
                }
                else {
                    for(int i=0; i<mShoppingCart.size(); i++){
                        mShoppingCart.get(i).selected = false;
                        for(int j=0; j<mShoppingCart.get(i).goods.size(); j++){
                            mShoppingCart.get(i).goods.get(j).selected = false;
                            pkIds.add(mShoppingCart.get(i).goods.get(j).pkId);
                        }
                    }
//                    t_price = 0.0f;
//                    t_num = 0;
                }
                adapter.setData(mShoppingCart);
                model.updateCheckStatus(pkIds, checkbox_all.isChecked());
//                MyGlobals.getInstance().set_Total_price(t_price);
//                MyGlobals.getInstance().set_Total_num(t_num);
//                totalPrice.setText(String.format("%.2f", MyGlobals.getInstance().getTotal_price()));
//                toSettleBtn.setText("去结算(" + MyGlobals.getInstance().getTotal_num() + ")");
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
////                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

    public void NoInternet() {
        binding.noInternet.setVisibility(View.VISIBLE);

        binding.mainScrollView.setVisibility(View.GONE);
        binding.bottomCtr.setVisibility(View.GONE);
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
