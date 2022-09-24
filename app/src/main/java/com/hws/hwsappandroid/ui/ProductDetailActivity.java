package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.MainActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.components.carouselview.CarouselView;
import com.hws.hwsappandroid.components.carouselview.ViewListener;
import com.hws.hwsappandroid.model.AddToCart;
import com.hws.hwsappandroid.model.GoodInfo;
import com.hws.hwsappandroid.model.GoodsSpec;
import com.hws.hwsappandroid.model.GoodsSpecMap;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.ui.cart.ShoppingCartAssist;

import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.FlowLayout;
import com.hws.hwsappandroid.util.GoodsDetailImagesAdapter;
import com.hws.hwsappandroid.util.GoodsParamListAdapter;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.MyGlobals;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity implements ItemClickListener, DialogInterface.OnCancelListener {

    public NestedScrollView mainScrollView;
    public LinearLayout toolbar_pdt_detail, favorite_btn;
    public RecyclerView imageRecyclerView, product_param;
    public ImageView imgItem;
    public TextView productDetailName, productPrice, price_decimal_places, storeLocation, cart_notify ;
    public TextView tvItemPrice, tvPrice_decimal_places, tvItemDetail, Item_inventory, tvItemAmount;
    public ImageButton favorite_star, plusBtn, minusBtn;
    public CardView goodParamCard;
    public Button confirmBtn;


    public GoodsDetailImagesAdapter mAdapter;
    private GoodsParamListAdapter mGPAdapter;

    public boolean buy_now_sate = false, favorite = false, view_all = false, addedCart = false, select_one, select_two;
    public int amount = 1, product_stock;
    public String AuthToken, shopId = "", pkId="", favorite_pkId, selected_goodsSpec, selected_option1="", selected_option2="";

    ArrayList<String> goodsDetailImg = new ArrayList<>();
    ArrayList<String> goodsSpecValue1List = new ArrayList<>();
    ArrayList<String> goodsSpecValue2List = new ArrayList<>();

    ArrayList<GoodsSpecMap> goodsSpecMapList = new ArrayList<>();
    ArrayList<Params> goodsParam = new ArrayList<>();
    ArrayList<Params> minGoodsParam = new ArrayList<>();

    GoodsSpec selectSpecInf = new GoodsSpec();
    GoodInfo goodInfo = new GoodInfo();
    AddToCart userCart;

    ProductDetailModel model;
    View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        AuthToken = pref.getString("token", "");

        Intent intent = getIntent();
        pkId = intent.getStringExtra("pkId");

        model = new ViewModelProvider(this).get(ProductDetailModel.class);
        userCart = new AddToCart();

        ImageButton backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar_pdt_detail = findViewById(R.id.toolbar_pdt_detail);
        mainScrollView = findViewById(R.id.ScrollView);
        mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
//                    Log.i(TAG, "Scroll DOWN");
                }
                if (scrollY < oldScrollY) {
//                    Log.i(TAG, "Scroll UP");
                }
                if (scrollY == 0) {
                    toolbar_pdt_detail.setVisibility(View.INVISIBLE);
                    backBtn.setImageResource(R.drawable.back_circle);
                }
                if (scrollY >= 50) {
                    toolbar_pdt_detail.setVisibility(View.VISIBLE);
                    backBtn.setImageResource(R.drawable.back);
                }
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
//                    model.loadMoreGoods();
                }
            }
        });

        cart_notify = findViewById(R.id.cart_notify);
        cart_notify.setText("" + MyGlobals.getInstance().getNotify_cart());
        if(MyGlobals.getInstance().getNotify_cart() == 0) cart_notify.setVisibility(View.INVISIBLE);
        else cart_notify.setVisibility(View.VISIBLE);

        LinearLayout serviceBtn = findViewById(R.id.serviceBtn);
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AuthToken.equals("")) {
                    Intent i = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getApplicationContext(), MultiEmotionActivity.class);
                    i.putExtra("shopId", goodInfo.goodsShop.pkId);
                    i.putExtra("shopName", goodInfo.goodsShop.shopName);
                    i.putExtra("shopLogoPic", goodInfo.goodsShop.shopLogoPic);
                    i.putExtra("bizClientId", goodInfo.goodsShop.bizClientId);
                    i.putExtra("operatorId", goodInfo.goodsShop.operatorId);
                    startActivity(i);
                }
            }
        });

        LinearLayout shopBtn = findViewById(R.id.shopBtn);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductDetailActivity.this, StoreDetailsActivity.class);
                i.putExtra("shopId", goodInfo.goodsShop.pkId);
                if(!goodInfo.goodsShop.pkId.equals(""))
                    startActivity(i);
            }
        });

        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AuthToken.equals("")) {
                    Intent i = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(getApplicationContext(), ShoppingCartAssist.class);
                    startActivity(i);
                }
            }
        });

        Button addToCartBtn = findViewById(R.id.addToCartBtn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy_now_sate = false;
                if(AuthToken.equals("")) {
                    Intent i = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                    startActivity(i);
                }else {
                    showBottomSheetDialog();
                }

            }
        });
        Button buyNowBtn = findViewById(R.id.BuyNowBtn);
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy_now_sate = true;
                if(AuthToken.equals("")) {
                    Intent i = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                    startActivity(i);
                }else {
                    showBottomSheetDialog();
                }

            }
        });

        // carousel
        CarouselView carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(0);

        model.loadData(pkId);
        model.getGoodImages().observe(this, goodImages -> {
            carouselView.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(int position) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    Picasso.get()
                            .load(goodImages.get(position)).fit().centerCrop()
                            .into(imageView);
                    TextView carousel_left = findViewById(R.id.carousel_left);
                    carousel_left.setText("1");
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getApplicationContext(), ImageDetailActivity.class);
                            i.putExtra("goodsDetailImg", goodImages);
                            i.putExtra("index", position);
                            startActivity(i);
                        }
                    });
                    return imageView;
                }
            });
            carouselView.setPageCount(goodImages.size());
            carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    TextView carousel_left = findViewById(R.id.carousel_left);
                    carousel_left.setText(""+(position+1));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            TextView carousel_right = findViewById(R.id.carousel_right);
            carousel_right.setText(""+goodImages.size());

            carouselView.getLayoutParams().height = carouselView.getWidth();
            carouselView.requestLayout();
        });

        productDetailName = findViewById(R.id.productDetailName);
        productPrice = findViewById(R.id.productPrice);
        price_decimal_places = findViewById(R.id.price_decimal_places);
        storeLocation = findViewById(R.id.storeLocation);
        favorite_star = findViewById(R.id.favorite_star);
        favorite_btn = findViewById(R.id.favorite_btn);

        goodParamCard = findViewById(R.id.goodParamCard);
        product_param = findViewById(R.id.product_param);
        product_param.setLayoutManager(new LinearLayoutManager(this));
        mGPAdapter = new GoodsParamListAdapter(getApplicationContext());
        product_param.setAdapter(mGPAdapter);

        model.getGoods().observe(this, goods -> {
            goodInfo = goods;
            String mPrice = goodInfo.goods.price;
            int idx = mPrice.indexOf(".");
            productPrice.setText(mPrice.substring(0, idx)+".");
            price_decimal_places.setText(mPrice.substring(idx+1));

            productDetailName.setText(goodInfo.goods.goodsName);
            String prov = goodInfo.goodsShop.province;
            String city = goodInfo.goodsShop.city;
            if(prov.substring(prov.length() - 1).equals("省")) prov = prov.substring(0, prov.length()-1);
            if(prov.substring(prov.length() - 1).equals("市")) prov = prov.substring(0, prov.length()-1);
            if(city.substring(city.length() - 1).equals("市")) city = city.substring(0, city.length()-1);
            storeLocation.setText(prov + city);

            if(goodInfo.canFavorite) {
                favorite_star.setImageResource(R.mipmap.star);
                favorite = true;
            }
            else {
                favorite_star.setImageResource(R.mipmap.star_);
                favorite = false;
            }

            goodsParam = goodInfo.goods.goodsParam;
            if(goodsParam.size() == 0)  goodParamCard.setVisibility(View.GONE);
            if(goodsParam.size() > 2){
                for(int i=0; i<2; i++) {
                    minGoodsParam.add(goodsParam.get(i));
                }
                mGPAdapter.setData(minGoodsParam);
            }

            goodsSpecMapList = goodInfo.specInfoMap.goodsSpecMap;
            goodsSpecValue1List = goodInfo.specInfoMap.goodsSpecValue1List;
            goodsSpecValue2List = goodInfo.specInfoMap.goodsSpecValue2List;

            userCart.goodsId = goodInfo.goods.pkId;
            userCart.shopId = goodInfo.goods.shopId;
            userCart.goodsNum = amount;
            userCart.goodsSn = goodInfo.goods.goodsSn;
        });

        favorite_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favorite){
                    favorite_star.setImageResource(R.mipmap.star_);
                    favorite = false;
                    model.cancelFavorite(pkId);
                    model.getResult().observe(ProductDetailActivity.this, result -> {
                        if(result.equals("success"))  favorite = false;
                    });
                }
                else {
                    favorite_star.setImageResource(R.mipmap.star);
                    model.setFavorite(pkId);
                    model.getSetResult().observe(ProductDetailActivity.this, result -> {
                        favorite_pkId = result;
                        favorite = true;
                    });
                }
            }
        });

        LinearLayout detail_info_title = findViewById(R.id.detail_info_title);
        CardView detail_info = findViewById(R.id.detail_info);
        ImageButton view_all_params = findViewById(R.id.view_all_params);
        view_all_params.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view_all){
                    view_all_params.setImageResource(R.mipmap.collapse_params);
                    mGPAdapter.setData(goodsParam);
                    view_all = true;
                }else{
                    view_all_params.setImageResource(R.mipmap.view_all_params);
                    mGPAdapter.setData(minGoodsParam);
                    view_all = false;
                }
            }
        });

        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GoodsDetailImagesAdapter(getApplicationContext());
        imageRecyclerView.setAdapter(mAdapter);
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goodInfo.goods.goodsDetail);
//            mAdapter.setData(goods.goodsImg);

            for(int i=0; i<goodInfo.goods.goodsDetail.size(); i++){
                goodsDetailImg.add(goodInfo.goods.goodsDetail.get(i).goodsDetailImg);
            }
        });
//        mAdapter.setClickListener(this);

    }

    @Override
    public void onClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        String img_url = mAdapter.getItem(position).goodsDetailImg;
        Intent i = new Intent(getApplicationContext(), ImageDetailActivity.class);
        i.putExtra("goodsDetailImg", goodsDetailImg);
        i.putExtra("index", position);
//        i.putExtra("img_url", img_url);
        startActivity(i);
    }

    private void showBottomSheetDialog() {
        select_one = false;
        select_two = false;

        final Dialog bottomSheetDialog = new Dialog(this);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_buy_now);

        ImageButton close_btn = bottomSheetDialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        imgItem = bottomSheetDialog.findViewById(R.id.imgItem);
        tvItemPrice = bottomSheetDialog.findViewById(R.id.tvItemPrice);
        tvPrice_decimal_places = bottomSheetDialog.findViewById(R.id.price_decimal_places);
        tvItemDetail = bottomSheetDialog.findViewById(R.id.tvItemDetail);
        Item_inventory = bottomSheetDialog.findViewById(R.id.Item_inventory);
        TextView goods_no = bottomSheetDialog.findViewById(R.id.goods_no);
        TextView option_name1 = bottomSheetDialog.findViewById(R.id.option_name1);
        TextView option_name2 = bottomSheetDialog.findViewById(R.id.option_name2);
        confirmBtn = bottomSheetDialog.findViewById(R.id.confirmBtn);

        tvItemAmount = bottomSheetDialog.findViewById(R.id.tvItemAmount);
        tvItemAmount.setText(String.valueOf(amount));

        plusBtn = bottomSheetDialog.findViewById(R.id.btnPlus);
        minusBtn = bottomSheetDialog.findViewById(R.id.btnMinus);

        tvItemAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditAmountDialog();
            }
        });

        goods_no.setText(goodInfo.goods.goodsSn);
        option_name1.setText(goodInfo.specInfoMap.goodsSpecName1);
        option_name2.setText(goodInfo.specInfoMap.goodsSpecName2);

        try{
            Picasso.get()
                    .load(goodInfo.goods.goodsPic).resize(500,500).into(imgItem);

            String mPrice = goodInfo.goodsPrice.price;
            int idx = mPrice.indexOf(".");
            tvItemPrice.setText(mPrice.substring(0, idx)+".");
            tvPrice_decimal_places.setText(mPrice.substring(idx+1));
            tvItemDetail.setText(goodInfo.goodsPrice.goods_spec);

        }catch (Exception e){ }

        String full_key = "";
        String init_select_option1="";
        String init_select_option2="";
        for(int i=0; i<goodInfo.specInfoMap.goodsSpecMap.size(); i++){
            if(goodInfo.specInfoMap.goodsSpecMap.get(i).value.goodsSpec.equals(goodInfo.goodsPrice.goods_spec)){
                full_key = goodInfo.specInfoMap.goodsSpecMap.get(i).key;
            }
        }
        try{
            if(goodInfo.specInfoMap.goodsSpecName2 == null || goodInfo.specInfoMap.goodsSpecName2.equals("")){
                init_select_option1 = full_key.substring(1, full_key.length()-1);
            }else{
                init_select_option1 = full_key.substring(1, full_key.indexOf("]["));
                init_select_option2 = full_key.substring(full_key.indexOf("][")+2, full_key.length()-1);
            }
        }catch (Exception e){}

        initSetGoodSpec(init_select_option1,init_select_option2);

        FlowLayout layout1 = bottomSheetDialog.findViewById(R.id.select_product_option);
        ArrayList<TextView> selectOptionList = new ArrayList<TextView>();
        for (int i=0; i<goodsSpecValue1List.size(); i++) {
            TextView tv = new TextView(this);
            tv.setText(goodsSpecValue1List.get(i).toString());
            tv.setBackgroundResource(R.drawable.round_gray_solid_4);
            tv.setTextSize(12);
            tv.setTextColor(Color.parseColor("#555555"));
            if(goodsSpecValue1List.get(i).toString().equals(init_select_option1)){
                tv.setTextColor(Color.parseColor("#F53F3F"));
                tv.setBackgroundResource(R.drawable.round_gray_red_border);
            }
            tv.setIncludeFontPadding(false);
            tv.setPadding(50, 20, 50, 20);

            String specKey = "";
            String key1 = "[" + goodsSpecValue1List.get(i).toString() + "]";
            String key2 = "";
            int sub_stock = 0;
            if(goodsSpecValue2List.size()>0){
                for(int j=0; j<goodsSpecValue2List.size(); j++){
                    key2 = "[" + goodsSpecValue2List.get(j).toString() + "]";
                    specKey = key1 + key2;

                    for(int pi=0; pi<goodInfo.specInfoMap.goodsSpecMap.size(); pi++){
                        if(goodInfo.specInfoMap.goodsSpecMap.get(pi).key.equals(specKey)){
                            selectSpecInf = goodInfo.specInfoMap.goodsSpecMap.get(pi).value;
                            sub_stock = sub_stock + selectSpecInf.stock;
                        }
                    }
                }
                if(sub_stock == 0) tv.setTextColor(Color.parseColor("#86909C"));
                else tv.setTextColor(Color.parseColor("#555555"));

            }else {
                specKey = key1;
                for(int pi=0; pi<goodInfo.specInfoMap.goodsSpecMap.size(); pi++){
                    if(goodInfo.specInfoMap.goodsSpecMap.get(pi).key.equals(specKey)){
                        selectSpecInf = goodInfo.specInfoMap.goodsSpecMap.get(pi).value;
                    }
                }
                if(selectSpecInf.stock == 0) tv.setTextColor(Color.parseColor("#86909C"));
                else  tv.setTextColor(Color.parseColor("#555555"));
            }

            FlowLayout.MarginLayoutParams margin_params = new FlowLayout.MarginLayoutParams(
                        FlowLayout.MarginLayoutParams.WRAP_CONTENT, FlowLayout.MarginLayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(margin_params);

            layout1.addView(tv);
            selectOptionList.add(tv);
        }

        FlowLayout layout2 = bottomSheetDialog.findViewById(R.id.select_product_size);
        ArrayList<TextView> selectSizeList = new ArrayList<TextView>();
        for (int i=0; i<goodsSpecValue2List.size(); i++) {
            TextView tv = new TextView(this);
            tv.setText(goodsSpecValue2List.get(i).toString());
            tv.setBackgroundResource(R.drawable.round_gray_solid_4);
            tv.setTextColor(Color.parseColor("#555555"));
            tv.setTextSize(12);
            if(goodsSpecValue2List.get(i).toString().equals(init_select_option2)){
                tv.setTextColor(Color.parseColor("#F53F3F"));
                tv.setBackgroundResource(R.drawable.round_gray_red_border);
            }
            tv.setIncludeFontPadding(false);
            tv.setPadding(50, 20, 50, 20);

            FlowLayout.MarginLayoutParams margin_params = new FlowLayout.MarginLayoutParams(
                    FlowLayout.MarginLayoutParams.WRAP_CONTENT, FlowLayout.MarginLayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(margin_params);

            layout2.addView(tv);
            selectSizeList.add(tv);
        }

        for(int i=0; i<selectOptionList.size(); i++)
        {
            int position = i;
            TextView itemTv = selectOptionList.get(i);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmBtn.setEnabled(false);
                    if(itemTv.getCurrentTextColor() == Color.parseColor("#555555")) {
                        for (int j = 0; j < selectOptionList.size(); j++) {
                            TextView remindTv = selectOptionList.get(j);
                            remindTv.setTextColor(Color.parseColor("#555555"));
                            remindTv.setBackgroundResource(R.drawable.round_gray_solid_4);
                        }
                        selected_option1 = goodsSpecValue1List.get(position).toString();
                        itemTv.setTextColor(Color.parseColor("#F53F3F"));
                        itemTv.setBackgroundResource(R.drawable.round_gray_red_border);

                        for (int sj=0; sj<selectSizeList.size(); sj++){
                            String option2 = goodsSpecValue2List.get(sj).toString();
                            String selKey = "[" + selected_option1 + "][" + option2 + "]";
                            for(int pi=0; pi<goodInfo.specInfoMap.goodsSpecMap.size(); pi++){
                                if(goodInfo.specInfoMap.goodsSpecMap.get(pi).key.equals(selKey)){
                                    selectSpecInf = goodInfo.specInfoMap.goodsSpecMap.get(pi).value;
                                }
                            }
                            if(selectSpecInf.stock == 0)  selectSizeList.get(sj).setTextColor(Color.parseColor("#86909C"));
                            else  selectSizeList.get(sj).setTextColor(Color.parseColor("#555555"));
                        }

                        select_one = true;
                        if (goodInfo.specInfoMap.goodsSpecName2 == null || goodInfo.specInfoMap.goodsSpecName2.equals(""))
                            select_two = true;

                        if (select_one && select_two) {
                            setGoodsSpec();
                        }
                    }

                }
            });
        }

        for(int i=0; i<selectSizeList.size(); i++)
        {
            int position = i;
            TextView itemTv = selectSizeList.get(i);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmBtn.setEnabled(false);
                    if(itemTv.getCurrentTextColor() == Color.parseColor("#555555")) {
                        for (int j = 0; j < selectSizeList.size(); j++) {
                            TextView remindTv = selectSizeList.get(j);
                            remindTv.setTextColor(Color.parseColor("#555555"));
                            remindTv.setBackgroundResource(R.drawable.round_gray_solid_4);
                        }
                        selected_option2 = goodsSpecValue2List.get(position).toString();
                        itemTv.setTextColor(Color.parseColor("#F53F3F"));
                        itemTv.setBackgroundResource(R.drawable.round_gray_red_border);
                        select_two = true;
                        if (goodInfo.specInfoMap.goodsSpecName1 == null || goodInfo.specInfoMap.goodsSpecName1.equals(""))
                            select_one = true;

                        if (select_one && select_two) {
                            setGoodsSpec();
                        }
                    }
                }
            });
        }

        if(amount == product_stock){
            plusBtn.setEnabled(false);
            plusBtn.setImageResource(R.drawable.btn_plus_disable);
        }

        if(amount == 1){
            minusBtn.setEnabled(false);
            minusBtn.setImageResource(R.drawable.btn_minus_disable);
        }

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amount+1;
                userCart.goodsNum = amount;
                tvItemAmount.setText(String.valueOf(amount));
                if(amount < product_stock) {
                    minusBtn.setEnabled(true);
                    minusBtn.setImageResource(R.drawable.btn_minus);
                }
                else{
                    plusBtn.setEnabled(false);
                    plusBtn.setImageResource(R.drawable.btn_plus_disable);
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amount - 1;
                userCart.goodsNum = amount;
                tvItemAmount.setText(String.valueOf(amount));
                if(amount > 1) {
                    plusBtn.setEnabled(true);
                    plusBtn.setImageResource(R.drawable.btn_plus);
                }
                else {
                    minusBtn.setEnabled(false);
                    minusBtn.setImageResource(R.drawable.btn_minus_disable);
                }
            }
        });

        confirmBtn.setEnabled(true);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buy_now_sate) {

                    addedCart = false;
//                    SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);

                    if(userCart.goodsSpecId != null){
                        final ProgressHUD progressDialog = ProgressHUD.showMulti(view.getContext(), "添加购物车成功", "在购物车等你哦~", true, true, ProductDetailActivity.this);
                        model.addToCart(userCart);
                        model.getResult().observe(ProductDetailActivity.this, result -> {
                            if(result.equals("success")) {
                                progressDialog.stopSpine();
                                if(!addedCart){
                                    int cart_contentsNum = MyGlobals.getInstance().getNotify_cart() + 1;
                                    MyGlobals.getInstance().setNotify_cart(cart_contentsNum);
                                    cart_notify.setVisibility(View.VISIBLE);
                                    cart_notify.setText("" + cart_contentsNum);

                                    MainActivity MA =  (MainActivity) MainActivity.MActivity;
                                    MA.refresh_badge();
                                    addedCart = true;
                                }

                            }
//                        CommonUtils.dismissProgress(progressDialog);
                            progressDialog.stopSpine();
                        });
                        bottomSheetDialog.dismiss();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "选择一个选项。", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    if(userCart.goodsSpecId != null){
                        bottomSheetDialog.dismiss();
                        Intent bnI = new Intent(getApplicationContext(), BuyNowActivity.class);
                        try{
                            bnI.putExtra("goodsName", goodInfo.goods.goodsName);
                            bnI.putExtra("goodsPic", goodInfo.goods.goodsPic);
                            bnI.putExtra("price", goodInfo.goods.price);
                            bnI.putExtra("goodsSpecId", userCart.goodsSpecId);
                            bnI.putExtra("goodsSpec", selected_goodsSpec);
                            bnI.putExtra("bizClientId", goodInfo.goods.bizClientId);
                            bnI.putExtra("auditStatus", goodInfo.goods.auditStatus);
                            bnI.putExtra("canFavorite", goodInfo.canFavorite);
                            bnI.putExtra("category1Id", goodInfo.goods.category1Id);
                            bnI.putExtra("category2Id", goodInfo.goods.category2Id);
                            bnI.putExtra("category3Id", goodInfo.goods.category3Id);
                            bnI.putExtra("goodsPicPreferred", goodInfo.goods.goodsPicPreferred);
                            bnI.putExtra("goodsSn", goodInfo.goods.goodsSn);
                            bnI.putExtra("isOnSale", goodInfo.goods.isOnSale);
                            bnI.putExtra("pkId", goodInfo.goods.pkId);
                            bnI.putExtra("shopId", goodInfo.goods.shopId);
                            bnI.putExtra("shopName", goodInfo.goodsShop.shopName);
                            bnI.putExtra("Shop_kId", goodInfo.goodsShop.pkId);
                            bnI.putExtra("province", goodInfo.goodsShop.province);
                            bnI.putExtra("amount", amount);
                        }catch(Exception e){}
                        startActivity(bnI);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "选择一个选项。", Toast.LENGTH_SHORT).show();
                    }

//                    model.addToOrderList(userCart);
                }
            }
        });

        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);

        int si = 0;
//        while (si < selectOptionList.size()){
//            if(goodsSpecList.get(si).stock > 0) {
//                TextView itemTv = selectOptionList.get(si);
//                itemTv.performClick();
//                si = selectOptionList.size();
//            }
//            else{   si++;   }
//        }

    }

    public void initSetGoodSpec(String option1, String option2){
        String key = "[" + option1 + "][" + option2 + "]";
        if(option2.equals("")) key = "[" + option1 + "]";
        if(option1.equals("")) key = "[" + option2 + "]";
        for(int pi=0; pi<goodInfo.specInfoMap.goodsSpecMap.size(); pi++){
            if(goodInfo.specInfoMap.goodsSpecMap.get(pi).key.equals(key)){
                selectSpecInf = goodInfo.specInfoMap.goodsSpecMap.get(pi).value;
            }
        }

        product_stock = selectSpecInf.stock;
        amount = 1;
        Item_inventory.setText("(" + getResources().getString(R.string.up_to_max)
                + product_stock
                + getResources().getString(R.string.piece) + ")");
        userCart.goodsSpecId = selectSpecInf.pkId;
        confirmBtn.setEnabled(true);

        if(product_stock > 0) {
            select_one = true;
            select_two = true;
        }
        if(amount == product_stock){
            plusBtn.setEnabled(false);
            plusBtn.setImageResource(R.drawable.btn_plus_disable);
        }
    }

    public void setGoodsSpec(){
        String key = "[" + selected_option1 + "][" + selected_option2 + "]";
        if(selected_option2.equals("")) key = "[" + selected_option1 + "]";
        if(selected_option1.equals("")) key = "[" + selected_option2 + "]";

        for(int pi=0; pi<goodInfo.specInfoMap.goodsSpecMap.size(); pi++){
            if(goodInfo.specInfoMap.goodsSpecMap.get(pi).key.equals(key)){
                selectSpecInf = goodInfo.specInfoMap.goodsSpecMap.get(pi).value;
            }
        }
        try{
            Picasso.get()
                    .load(selectSpecInf.goodsSpecImg)
                    .resize(500,500).into(imgItem);
            String mPrice = selectSpecInf.price;
            int idx = mPrice.indexOf(".");
            tvItemPrice.setText(mPrice.substring(0, idx)+".");
            tvPrice_decimal_places.setText(mPrice.substring(idx+1));
            tvItemDetail.setText(selectSpecInf.goodsSpec);

            product_stock = selectSpecInf.stock;
            if(product_stock > 0){
                amount = 1;
                tvItemAmount.setText("" + amount);

                Item_inventory.setText("(" + getResources().getString(R.string.up_to_max)
                        + product_stock
                        + getResources().getString(R.string.piece) + ")");

                userCart.goodsSpecId = selectSpecInf.pkId;
                confirmBtn.setEnabled(true);
            }
            else Item_inventory.setText("");

        }catch (Exception e){}

        if(amount == product_stock){
            plusBtn.setEnabled(false);
            plusBtn.setImageResource(R.drawable.btn_plus_disable);
        }
    }

    public void showEditAmountDialog(){
        final Dialog SheetEditDialog = new Dialog(this);
        SheetEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SheetEditDialog.setContentView(R.layout.sheet_edit_amount);
        SheetEditDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton plus_Btn = SheetEditDialog.findViewById(R.id.btnPlus);
        ImageButton minus_Btn = SheetEditDialog.findViewById(R.id.btnMinus);

        if(amount > 1){
            minus_Btn.setEnabled(true);
            minus_Btn.setImageResource(R.drawable.btn_minus);
        }else {
            minus_Btn.setEnabled(false);
            minus_Btn.setImageResource(R.drawable.btn_minus_disable);
        }
        if(amount >= product_stock){
            plus_Btn.setEnabled(false);
            plus_Btn.setImageResource(R.drawable.btn_plus_disable);
        }

        EditText edit_mount = SheetEditDialog.findViewById(R.id.edit_mount);
        edit_mount.setText(String.valueOf(amount));

        edit_mount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    amount = Integer.parseInt(edit_mount.getText().toString());
                }catch (Exception e){}

                if(amount > 1){
                    minus_Btn.setEnabled(true);
                    minus_Btn.setImageResource(R.drawable.btn_minus);
                }else {
                    minus_Btn.setEnabled(false);
                    minus_Btn.setImageResource(R.drawable.btn_minus_disable);
                }

                if(amount < product_stock){
                    plus_Btn.setEnabled(true);
                    plus_Btn.setImageResource(R.drawable.btn_plus);
                }else {
                    plus_Btn.setEnabled(false);
                    plus_Btn.setImageResource(R.drawable.btn_plus_disable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        TextView explain = SheetEditDialog.findViewById(R.id.explain);
        explain.setText(getResources().getString(R.string.notify_amount) + product_stock);

        plus_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amount+1;
                edit_mount.setText(String.valueOf(amount));
            }
        });

        minus_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amount - 1;
                edit_mount.setText(String.valueOf(amount));
            }
        });

        Button cancel = SheetEditDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SheetEditDialog.dismiss();
            }
        });

        Button confirm = SheetEditDialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    amount = Integer.parseInt(edit_mount.getText().toString());
                    if(amount > 0 && amount <= product_stock){
                        userCart.goodsNum = amount;
                        tvItemAmount.setText(edit_mount.getText().toString());
                        if (amount > 1) {
                            minusBtn.setEnabled(true);
                            minusBtn.setImageResource(R.drawable.btn_minus);
                        } else {
                            minusBtn.setEnabled(false);
                            minusBtn.setImageResource(R.drawable.btn_minus_disable);
                        }

                        if (amount < product_stock) {
                            plusBtn.setEnabled(true);
                            plusBtn.setImageResource(R.drawable.btn_plus);
                        } else {
                            plusBtn.setEnabled(false);
                            plusBtn.setImageResource(R.drawable.btn_plus_disable);
                        }
                        SheetEditDialog.dismiss();
                    }

                }catch (Exception e){}
            }
        });

        Rect displayRectangle = new Rect();
        Window window = SheetEditDialog.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        SheetEditDialog.getWindow().setLayout(CommonUtils.getPixelValue(this, 316), ViewGroup.LayoutParams.WRAP_CONTENT);

        SheetEditDialog.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        decorView.setSystemUiVisibility(
//                 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//    }

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