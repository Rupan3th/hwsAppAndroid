package com.hws.hwsappandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.OrderGoods;
import com.hws.hwsappandroid.model.OrderInfoVO;
import com.hws.hwsappandroid.model.SaveOrderShop;
import com.hws.hwsappandroid.model.UserCartItem;
import com.hws.hwsappandroid.model.aoListItem;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ListAdapter;
import com.hws.hwsappandroid.util.MyGlobals;
import com.hws.hwsappandroid.util.ShoppingCartListAdapter;
import com.walnutlabs.android.ProgressHUD;

import java.util.ArrayList;

public class CartSettlementActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    public static Activity CSActivity;
    public ArrayList<UserCartItem> models = new ArrayList<>();
    public ArrayList<GoodOfShoppingCart> models_settle = new ArrayList<>();

    private RecyclerView recyclerView;
    ListAdapter adapter;

    LinearLayout exists_address, none_address;
    TextView client_Name, phone_Number, client_Address, totalPrice, total_num, delivery_Method, freight;
    Button toSettleBtn;

    public BuyNowModel model;
    private shippingAdr defaultAdr;
    public ArrayList<aoListItem> aoList = new ArrayList<>();
    boolean submitSuccess;
    private static final int REQUEST_CODE_ADDRESS = 1;

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

        setContentView(R.layout.activity_cart_settlement);
        CSActivity = CartSettlementActivity.this;

        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        model = new ViewModelProvider(this).get(BuyNowModel.class);
        defaultAdr = new shippingAdr();

        SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
        String receiver_name = pref.getString("receiver_name","");

        exists_address = findViewById(R.id.exists_address);
        none_address = findViewById(R.id.none_address);

        client_Name = findViewById(R.id.client_Name);
        phone_Number = findViewById(R.id.phone_Number);
        client_Address = findViewById(R.id.client_Address);

        exists_address.setVisibility(View.GONE);
        none_address.setVisibility(View.VISIBLE);

        delivery_Method = findViewById(R.id.delivery_Method);
        freight = findViewById(R.id.freight);

        model.loadData();
        model.getAddress().observe(this, defaultAddress -> {
            defaultAdr = defaultAddress;

            if(defaultAdr.province != null){
                exists_address.setVisibility(View.VISIBLE);
                none_address.setVisibility(View.GONE);

                client_Name.setText(defaultAdr.consignee);
                try{
                    phone_Number.setText(defaultAdr.phone.substring(0,3)+"****"+defaultAdr.phone.substring(7));
                }catch (Exception e){}
                client_Address.setText(defaultAdr.province + defaultAdr.city + defaultAdr.district + defaultAdr.address);

                submitOrder();
            }
        });


        ImageButton btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView gotoAddress_Btn = findViewById(R.id.goto_edit_adr);
        gotoAddress_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CartSettlementActivity.this, ShippingAddressActivity.class);
                i.putExtra("purpose", "choose");
                startActivityForResult(i, REQUEST_CODE_ADDRESS);
//                startActivityForResult(i, 0);
            }
        });

        models = MyGlobals.getInstance().getMyShoppingCart();
        for(int i=0; i<models.size(); i++){
            for(int j=0; j<models.get(i).goods.size(); j++){
                if(models.get(i).goods.get(j).selected){
                    models_settle.add(models.get(i).goods.get(j)) ;
                }
            }
        }

        recyclerView = findViewById(R.id.cart_settlement_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ListAdapter(this, true, 3);
        recyclerView.setAdapter(adapter);
        adapter.setDataSettle(models_settle);

        float t_price = 0.0f;
        int t_num = 0;
        for(int i=0; i<models_settle.size(); i++){
            t_price =  t_price+ Float.parseFloat(models_settle.get(i).goodsPrice)*models_settle.get(i).goodsNum;
            t_num = t_num + models_settle.get(i).goodsNum;
        }

        totalPrice = findViewById(R.id.totalPrice);
//        totalPrice.setText(String.format("%.2f", t_price));

        total_num= findViewById(R.id.pieces);
        total_num.setText(""+t_num);

        toSettleBtn = findViewById(R.id.toSettleBtn);
//        toSettleBtn.setText(getResources().getString(R.string.submit_order) + " ￥" + String.format("%.2f", t_price));
        toSettleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(defaultAdr.pkId == null){
                    Toast.makeText(CartSettlementActivity.this, getResources().getString(R.string.please_select_shipping_address), Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i=0; i<models_settle.size(); i++){
                    aoListItem aoItem = new aoListItem();
                    aoItem.addressId = defaultAdr.pkId;
                    aoItem.goodsId = models_settle.get(i).goodsId;
                    aoItem.goodsSpecId = models_settle.get(i).goodsSpecId;
                    aoItem.goodsSpec = models_settle.get(i).goodsSpec;
                    aoItem.goodsNum = models_settle.get(i).goodsNum;
                    aoList.add(aoItem);
                }
                final ProgressHUD progressDialog = ProgressHUD.show(CartSettlementActivity.this, "", true, false, CartSettlementActivity.this);

                submitSuccess = false;
                model.submit_order(aoList);
                model.getSubmitOrder().observe(CartSettlementActivity.this, submitOrder -> {
                    if(submitOrder.submitOrderInfoList.size() > 0)  {
                        OrderInfoVO orderInfoVO = new OrderInfoVO();
                        orderInfoVO.addressId = submitOrder.defaultAddress.pkId;

                        ArrayList<SaveOrderShop> saveOrderShop = new ArrayList<>();
                        for(int i=0; i<submitOrder.submitOrderInfoList.size(); i++){
                            SaveOrderShop saveOrderShopItem = new SaveOrderShop();
                            ArrayList<OrderGoods> orderGoodsList = new ArrayList<>();
                            for(int j=0; j<submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.size(); j++){
                                OrderGoods orderGoods = new OrderGoods();
                                orderGoods.goodsId = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsId;
                                orderGoods.goodsName = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsName;
                                orderGoods.goodsSn = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsSn;
                                orderGoods.goodsNum = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsNum;
                                orderGoods.goodsPrice = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsPrice;
                                orderGoods.goodsSpecId = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsSpecId;
                                orderGoodsList.add(orderGoods);
                            }
                            saveOrderShopItem.orderGoodsList = orderGoodsList;
                            saveOrderShopItem.shippingFee = submitOrder.submitOrderInfoList.get(i).shippingFee;
                            saveOrderShopItem.shopFee = submitOrder.submitOrderInfoList.get(i).shopFee;
                            saveOrderShopItem.shopId = submitOrder.submitOrderInfoList.get(i).shopId;
                            saveOrderShopItem.shopName = submitOrder.submitOrderInfoList.get(i).shopName;
                            saveOrderShop.add(saveOrderShopItem);
                        }
                        orderInfoVO.saveOrderShop = saveOrderShop;
                        orderInfoVO.totalMoney = submitOrder.totalMoney;

                        MyGlobals.getInstance().setOrderInfoVO(orderInfoVO);

                        progressDialog.dismiss();
                        if(!submitSuccess){
                            Intent i = new Intent(CartSettlementActivity.this, MerchantCashierActivity.class);
                            i.putExtra("totalPrice", orderInfoVO.totalMoney);
                            startActivity(i);
                            submitSuccess = true;
                        }

                    }
                    CommonUtils.dismissProgress(progressDialog);
                });
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void submitOrder(){
        if(defaultAdr.pkId != null){
            for(int i=0; i<models_settle.size(); i++){
                aoListItem aoItem = new aoListItem();
                aoItem.addressId = defaultAdr.pkId;
                aoItem.goodsId = models_settle.get(i).goodsId;
                aoItem.goodsSpecId = models_settle.get(i).goodsSpecId;
                aoItem.goodsSpec = models_settle.get(i).goodsSpec;
                aoItem.goodsNum = models_settle.get(i).goodsNum;
                aoList.add(aoItem);
            }

            final ProgressHUD progressDialog = ProgressHUD.show(CartSettlementActivity.this, "", true, false, CartSettlementActivity.this);

            submitSuccess = false;
            model.submit_order(aoList);
            model.getSubmitOrder().observe(CartSettlementActivity.this, submitOrder -> {
                if(submitOrder.submitOrderInfoList.size() > 0)  {
                    OrderInfoVO orderInfoVO = new OrderInfoVO();
                    orderInfoVO.addressId = submitOrder.defaultAddress.pkId;

                    ArrayList<SaveOrderShop> saveOrderShop = new ArrayList<>();
                    for(int i=0; i<submitOrder.submitOrderInfoList.size(); i++){
                        SaveOrderShop saveOrderShopItem = new SaveOrderShop();
                        ArrayList<OrderGoods> orderGoodsList = new ArrayList<>();
                        for(int j=0; j<submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.size(); j++){
                            OrderGoods orderGoods = new OrderGoods();
                            orderGoods.goodsId = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsId;
                            orderGoods.goodsName = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsName;
                            orderGoods.goodsSn = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsSn;
                            orderGoods.goodsNum = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsNum;
                            orderGoods.goodsPrice = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsPrice;
                            orderGoods.goodsSpecId = submitOrder.submitOrderInfoList.get(i).submitOrderGoodsInfoList.get(j).goodsSpecId;
                            orderGoodsList.add(orderGoods);
                        }
                        saveOrderShopItem.orderGoodsList = orderGoodsList;
                        saveOrderShopItem.shippingFee = submitOrder.submitOrderInfoList.get(i).shippingFee;
                        saveOrderShopItem.shopFee = submitOrder.submitOrderInfoList.get(i).shopFee;
                        saveOrderShopItem.shopId = submitOrder.submitOrderInfoList.get(i).shopId;
                        saveOrderShopItem.shopName = submitOrder.submitOrderInfoList.get(i).shopName;
                        saveOrderShop.add(saveOrderShopItem);


                        freight.setText("￥" + saveOrderShopItem.shippingFee);
                    }
                    orderInfoVO.saveOrderShop = saveOrderShop;
                    orderInfoVO.totalMoney = submitOrder.totalMoney;

                    delivery_Method.setText(submitOrder.isFreeOfCharge);
                    totalPrice.setText("￥" + orderInfoVO.totalMoney);
                    toSettleBtn.setText(getResources().getString(R.string.submit_order) + " ￥" + orderInfoVO.totalMoney);

                    MyGlobals.getInstance().setOrderInfoVO(orderInfoVO);

                    progressDialog.dismiss();
                }
                CommonUtils.dismissProgress(progressDialog);
            });
        }

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            refreshView();
        } else if (requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                try{
                    defaultAdr.address = data.getStringExtra("address");
                    defaultAdr.addressDefault = data.getStringExtra("addressDefault");
                    defaultAdr.city = data.getStringExtra("city");
                    defaultAdr.consignee = data.getStringExtra("consignee");
                    defaultAdr.consigneeXb = data.getStringExtra("consigneeXb");
                    defaultAdr.country = data.getStringExtra("country");
                    defaultAdr.district = data.getStringExtra("district");
                    defaultAdr.gmtCreate = data.getStringExtra("gmtCreate");
                    defaultAdr.gmtModified = data.getStringExtra("gmtModified");
                    defaultAdr.operatorId = data.getStringExtra("operatorId");
                    defaultAdr.userId = data.getStringExtra("userId");
                    defaultAdr.phone = data.getStringExtra("phone");
                    defaultAdr.pkId = data.getStringExtra("pkId");
                    defaultAdr.province = data.getStringExtra("province");

                    if(defaultAdr.province != null){
                        exists_address.setVisibility(View.VISIBLE);
                        none_address.setVisibility(View.GONE);
                    }
                }catch (Exception e){}

                client_Name.setText(defaultAdr.consignee);
                try{
                    phone_Number.setText(defaultAdr.phone.substring(0,3)+"****"+defaultAdr.phone.substring(7));
                }catch (Exception e){}
                client_Address.setText(defaultAdr.province + defaultAdr.city + defaultAdr.district + defaultAdr.address);

                submitOrder();
            }
        }
    }

    public void refreshView() {
        SharedPreferences pref = getSharedPreferences("user_info", MODE_PRIVATE);
        String consignee = pref.getString("default_consignee", "");
        String default_phone = pref.getString("default_phone", "");
        String default_adr = pref.getString("default_adr", "");

        if(consignee.equals("")){
            exists_address.setVisibility(View.GONE);
            none_address.setVisibility(View.VISIBLE);
        }
        else{
            exists_address.setVisibility(View.VISIBLE);
            none_address.setVisibility(View.GONE);
        }

        client_Name.setText(consignee);
        try{
            phone_Number.setText(default_phone.substring(0,3)+"****"+default_phone.substring(7));
        }catch (Exception e){}
        client_Address.setText(default_adr);
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