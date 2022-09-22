package com.hws.hwsappandroid.ui.me.main;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.MyOrderModel;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class OrderViewModel extends ViewModel {
    private static String keyword = "";
    private boolean isLoading = false;
    private final MutableLiveData<ArrayList<MyOrderModel>> MyOrders = new MutableLiveData<>();

    public LiveData<ArrayList<MyOrderModel>> getMyOrders() {  return MyOrders;   }

    private int mIndex;

    public int status;

    public static void setKeyword(String word) {
        keyword = word;
    }

    public void setIndex(int index) {
//        mIndex.setValue(index);
        mIndex = index;
    }

    public void loadData() {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                mIndex = 2;
                JSONObject jsonParams = new JSONObject();
                try {
                    if (keyword != null && keyword.length() > 0) {
                        if(isOrderNO(keyword))  jsonParams.put("orderCode", keyword);
                        else jsonParams.put("goodsName", keyword);

                    }
//                    jsonParams.put("pageNum", 1);
//                    jsonParams.put("pageSize", "20");
                    if(mIndex == 1) {
                        jsonParams.put("orderStatus", "-1");
                    }else if(mIndex == 2){
                        jsonParams.put("orderStatus", "0");
                    }else {
                        jsonParams.put("orderStatus", mIndex);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String url = "/bizOrder/queryMyOrder";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<MyOrderModel> myOrders = new ArrayList<>();
                                for (int i=0; i<list.length(); i++) {
                                    obj = list.getJSONObject(i);
                                    MyOrderModel myOrder = new MyOrderModel();
                                    myOrder.bizClientId = obj.optString("bizClientId", "");
                                    myOrder.bizUserId = obj.optString("bizClientId", "");
                                    myOrder.isChargeback = obj.optInt("isChargeback", 0);
                                    myOrder.orderTime = obj.optString("orderTime", "");
                                    myOrder.orderCode = obj.optString("orderCode", "");
                                    myOrder.orderStatus = obj.optInt("orderStatus", 0);
                                    myOrder.pkId = obj.optString("pkId", "");
                                    myOrder.shopId = obj.optString("shopId", "");
                                    myOrder.shopName = obj.optString("shopName", "");
                                    myOrder.shippingFee = obj.optString("shippingFee", "");
                                    myOrder.totalMoney = obj.optString("totalMoney", "");

                                    JSONArray myGoodsListJson = obj.getJSONArray("myGoodsList");
                                    ArrayList<GoodOfShoppingCart> GoodsList = new ArrayList<>();
                                    for (int j=0; j<myGoodsListJson.length(); j++) {
                                        JSONObject myGoodsJson = myGoodsListJson.getJSONObject(j);
                                        GoodOfShoppingCart myGoods = new GoodOfShoppingCart();
                                        myGoods.goodsId = myGoodsJson.optString("goodsId", "");
                                        myGoods.goodsName = myGoodsJson.optString("goodsName", "");
                                        myGoods.goodsNum = myGoodsJson.optInt("goodsNum", 0);
                                        myGoods.goodsPic = myGoodsJson.optString("goodsPic", "");
                                        myGoods.goodsPrice = myGoodsJson.optString("goodsPrice", "");
                                        myGoods.goodsSpec = myGoodsJson.optString("goodsSpec", "");
                                        myGoods.goodsSpecId = myGoodsJson.optString("goodsSpecId", "");
                                        myGoods.goodsStatus = myGoodsJson.optInt("refundStatus", 0);


                                        GoodsList.add(myGoods);
                                    }
                                    myOrder.myGoodsList = GoodsList;
                                    myOrders.add(myOrder);
                                }
                                MyOrders.postValue(myOrders);

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }
                });
            }
        });
    }

    public void loadData(String keyword, int index) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                mIndex = 2;
                JSONObject jsonParams = new JSONObject();
                try {
//                    jsonParams.put("pageNum", 1);
//                    jsonParams.put("pageSize", "20");
                    if(isOrderNO(keyword))  jsonParams.put("orderCode", keyword);
                    else jsonParams.put("goodsName", keyword);
                    if(index == 1) {
                        jsonParams.put("orderStatus", "-1");
                    }else if(index == 2){
                        jsonParams.put("orderStatus", "0");
                    }else {
                        jsonParams.put("orderStatus", index);
                    }
                    jsonParams.put("orderStatus", -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String url = "/bizOrder/queryMyOrder";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<MyOrderModel> myOrders = new ArrayList<>();
                                for (int i=0; i<list.length(); i++) {
                                    obj = list.getJSONObject(i);
                                    MyOrderModel myOrder = new MyOrderModel();
                                    myOrder.bizClientId = obj.optString("bizClientId", "");
                                    myOrder.bizUserId = obj.optString("bizClientId", "");
                                    myOrder.isChargeback = obj.optInt("isChargeback", 0);
                                    myOrder.orderTime = obj.optString("orderTime", "");
                                    myOrder.orderCode = obj.optString("orderCode", "");
                                    myOrder.orderStatus = obj.optInt("orderStatus", 0);
                                    myOrder.pkId = obj.optString("pkId", "");
                                    myOrder.shopId = obj.optString("shopId", "");
                                    myOrder.shopName = obj.optString("shopName", "");
                                    myOrder.shippingFee = obj.optString("shippingFee", "");
                                    myOrder.totalMoney = obj.optString("totalMoney", "");

                                    JSONArray myGoodsListJson = obj.getJSONArray("myGoodsList");
                                    ArrayList<GoodOfShoppingCart> GoodsList = new ArrayList<>();
                                    for (int j=0; j<myGoodsListJson.length(); j++) {
                                        JSONObject myGoodsJson = myGoodsListJson.getJSONObject(j);
                                        GoodOfShoppingCart myGoods = new GoodOfShoppingCart();
                                        myGoods.goodsId = myGoodsJson.optString("goodsId", "");
                                        myGoods.goodsName = myGoodsJson.optString("goodsName", "");
                                        myGoods.goodsNum = myGoodsJson.optInt("goodsNum", 0);
                                        myGoods.goodsPic = myGoodsJson.optString("goodsPic", "");
                                        myGoods.goodsPrice = myGoodsJson.optString("goodsPrice", "");
                                        myGoods.goodsSpec = myGoodsJson.optString("goodsSpec", "");
                                        myGoods.goodsSpecId = myGoodsJson.optString("goodsSpecId", "");
                                        myGoods.goodsStatus = myGoodsJson.optInt("refundStatus", 0);

                                        GoodsList.add(myGoods);
                                    }
                                    myOrder.myGoodsList = GoodsList;
                                    myOrders.add(myOrder);
                                }
                                MyOrders.postValue(myOrders);

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }
                });
            }
        });
    }

    public static boolean isOrderNO(String word) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String NoRegex = "[D|:d][D|:d]\\d{3,15}";
        if (TextUtils.isEmpty(word))
            return false;
        else
            return word.matches(NoRegex);
    }
}



