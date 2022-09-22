package com.hws.hwsappandroid.ui.cart;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.AddToCart;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.UserCartItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class ShoppingCartModel extends ViewModel {
    private final MutableLiveData<ArrayList<UserCartItem>> mCarts = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<GoodOfShoppingCart>> mGoodsPerShop = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private final MutableLiveData<String> mResult = new MutableLiveData<>();
    private boolean isLoading = false;
    private MutableLiveData<Float> selectedPrice = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedGoodsNum = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsAllSelected = new MutableLiveData<>();

    public LiveData<ArrayList<UserCartItem>> getMyCart() {
        return mCarts;
    }
    public LiveData<ArrayList<GoodOfShoppingCart>> getGoodsPerShop() {
        return mGoodsPerShop;
    }
    public LiveData<ArrayList<Good>> getGoods() {
        return mGoods;
    }
    public LiveData<String> getResult() {
        return mResult;
    }
    public LiveData<Float> getSelectedTotalPrice() { return selectedPrice; }
    public LiveData<Integer> getSelectedGoodsNum() { return selectedGoodsNum; }
    public LiveData<Boolean> isAllSelected() { return mIsAllSelected; }

    public void loadData() {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                String url = "/appUserCart/queryByUserId";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");

                                ArrayList<UserCartItem> myCart = new ArrayList<>();
                                for (int i=0; i<list.length(); i++) {
                                    obj = list.getJSONObject(i);
                                    UserCartItem mCartItem = new UserCartItem();

                                    JSONArray goodsList = obj.getJSONArray("goods");
                                    ArrayList<GoodOfShoppingCart> goodsPerShop = new ArrayList<>();
                                    boolean allSelected = true;
                                    for (int j=0; j<goodsList.length(); j++) {
                                        JSONObject goodJson = goodsList.getJSONObject(j);

                                        int isOnSale = goodJson.optInt("isOnSale", 1);
                                        if (isOnSale == 0) continue;

                                        GoodOfShoppingCart good = new GoodOfShoppingCart();
                                        good.gmtCreate = goodJson.optString("gmtCreate", "");
                                        good.gmtModified = goodJson.optString("gmtModified", "");
                                        good.goodsId = goodJson.optString("goodsId", "");
                                        good.goodsName = goodJson.optString("goodsName", "");
                                        good.goodsNum = goodJson.optInt("goodsNum", 0);
                                        good.goodsPic = goodJson.optString("goodsSpecPic", "");
                                        good.goodsPrice = goodJson.optString("goodsPrice", "");
                                        good.goodsSn = goodJson.optString("goodsSn", "");
                                        good.goodsSpec = goodJson.optString("goodsSpec", "");
                                        good.goodsSpecId = goodJson.optString("goodsSpecId", "");
                                        good.isCheck = goodJson.optString("isCheck", "");
                                        good.isOnSale = goodJson.optInt("isOnSale", 1);
                                        good.operatorId = goodJson.optString("operatorId", "");
                                        good.pkId = goodJson.optString("pkId", "");
                                        good.shopId = goodJson.optString("shopId", "");
                                        good.shopName = goodJson.optString("shopName", "");
                                        good.userId = goodJson.optString("userId", "");
                                        good.stock = goodJson.optString("stock", "");
                                        good.selected = good.isCheck.equals("1");
                                        good.canFavorite = goodJson.optString("canFavorite", "no");
                                        goodsPerShop.add(good);

                                        allSelected = allSelected && good.selected;
                                    }

                                    if (goodsPerShop.size() > 0) {
                                        mCartItem.goods = goodsPerShop;
                                        mCartItem.bizClientUserId = obj.optString("bizClientUserId", "");
                                        mCartItem.shopId = obj.optString("shopId", "");
                                        mCartItem.shopName = obj.optString("shopName", "");
                                        mCartItem.selected = allSelected;
                                        myCart.add(mCartItem);
                                    }
                                }
//                                mCarts.postValue(myCart);
                                calcAutoValues(myCart);
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

    public void loadRecommendData() {
//        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                String url = "/appBanner/queryBannerAndAll";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                obj = list.getJSONObject(0);

                                JSONArray goodsJson = obj.getJSONObject("goods").getJSONArray("list");
                                ArrayList<Good> goodArr = new ArrayList<>();
                                for (int i=0; i<goodsJson.length(); i++) {
                                    JSONObject json = goodsJson.getJSONObject(i);
                                    Good good = new Good();
                                    good.pkId = json.optString("pkId", "");
                                    good.goodsName = json.optString("goodsName", "");
                                    good.goodsPic = json.optString("goodsPic", "");
                                    good.goodsPicPreferred = json.optString("goodsPicPreferred", "");
                                    good.goodsSn = json.optString("goodsSn", "");
                                    good.goodsSpecId = json.optString("goodsSpecId", "");
                                    good.price = json.optString("price", "1.00");
                                    good.salesNum = json.optInt("salesNum", 1);
                                    good.isPreferred = json.optInt("isPreferred", 1);
                                    good.shopId = json.optString("shopId", "");
                                    good.operatorId = json.optString("operatorId", "");
                                    good.bizClientId = json.optString("bizClientId", "");
                                    good.onSaleTime = json.optString("onSaleTime", "");
                                    good.outSaleTime = json.optString("outSaleTime", "");
                                    good.gmtCreate = json.optString("gmtCreate", "");
                                    good.gmtModified = json.optString("gmtModified", "");

                                    ArrayList<Params> goodsParam = new ArrayList<>();
                                    JSONObject goodsParamJson = new JSONObject();
                                    try {
                                        goodsParamJson = new JSONObject(json.optString("goodsParam", ""));
                                    } catch (Throwable tx) {
                                        Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                                    }

//                                    JSONObject goodsParamJson = json.getJSONObject("goodsParam");
                                    Iterator<String> keys = goodsParamJson.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        Params goodParam = new Params();
                                        goodParam.key = key;
                                        goodParam.value = goodsParamJson.optString(key, "");
                                        goodsParam.add(goodParam);
                                    }
//                                    good.goodsParam = json.optString("goodsParam", "");
                                    good.goodsParam = goodsParam;

                                    goodArr.add(good);
                                }
                                mGoods.postValue(goodArr);

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

    public void loadMoreGoods() {
        if (isLoading) return;
        isLoading = true;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.d("Home", "Trying to load more...");
//                RequestParams params = new RequestParams();
                JSONObject jsonParams = new JSONObject();
                ArrayList<Good> goodArr = mGoods.getValue();
                if (goodArr.size() > 0) {
                    Good aGood = goodArr.get(goodArr.size()-1);
//                    params.add("pkId", aGood.pkId);
                    try {
                        jsonParams.put("pkId", aGood.pkId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String url = "/bizGoods/queryPreferredInfo";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray goodsJson = obj.getJSONArray("list");
                                ArrayList<Good> goodArr = mGoods.getValue();
                                for (int i=0; i<goodsJson.length(); i++) {
                                    JSONObject json = goodsJson.getJSONObject(i);
                                    Good good = new Good();
                                    good.pkId = json.optString("pkId", "");
                                    good.goodsName = json.optString("goodsName", "");
                                    good.goodsPic = json.optString("goodsPic", "");
                                    good.goodsPicPreferred = json.optString("goodsPicPreferred", "");
                                    good.goodsSn = json.optString("goodsSn", "");
                                    good.goodsSpecId = json.optString("goodsSpecId", "");
                                    good.price = json.optString("price", "1.00");
                                    good.salesNum = json.optInt("salesNum", 1);
                                    good.isPreferred = json.optInt("isPreferred", 1);
                                    good.shopId = json.optString("shopId", "");
                                    good.operatorId = json.optString("operatorId", "");
                                    good.bizClientId = json.optString("bizClientId", "");
                                    good.onSaleTime = json.optString("onSaleTime", "");
                                    good.outSaleTime = json.optString("outSaleTime", "");
                                    good.gmtCreate = json.optString("gmtCreate", "");
                                    good.gmtModified = json.optString("gmtModified", "");

                                    ArrayList<Params> goodsParam = new ArrayList<>();
                                    JSONObject goodsParamJson = new JSONObject();
                                    try {
                                        goodsParamJson = new JSONObject(json.optString("goodsParam", ""));
                                    } catch (Throwable tx) {
                                        Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                                    }

//                                    JSONObject goodsParamJson = json.getJSONObject("goodsParam");
                                    Iterator<String> keys = goodsParamJson.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        Params goodParam = new Params();
                                        goodParam.key = key;
                                        goodParam.value = goodsParamJson.optString(key, "");
                                        goodsParam.add(goodParam);
                                    }
//                                    good.goodsParam = json.optString("goodsParam", "");
                                    good.goodsParam = goodsParam;

                                    goodArr.add(good);
                                }
                                mGoods.postValue(goodArr);
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
                        Log.d("Home request", errorResponse.toString());
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

    public void updateCheckStatus (ArrayList<String> pkIds, boolean isChecked) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < pkIds.size(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("pkId", pkIds.get(i));
                        obj.put("isCheck", isChecked ? "1" : "0");
                        jsonArray.put(obj);
                    }
                    jsonParams.put("pkIdList", jsonArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserCart/updateBySpecIds";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                calcAutoValues(mCarts.getValue());
                            } else {
                                Log.d("Shopping Cart selection", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void calcAutoValues(ArrayList<UserCartItem> myCart) {
        if (myCart == null) return;

        float t_price = 0.0f;
        int t_num = 0;
        boolean allSelected = true;

        for(int i=0; i<myCart.size(); i++) {
            boolean cartSelected = true;
            for(int j=0; j<myCart.get(i).goods.size(); j++){
                if(myCart.get(i).goods.get(j).selected){
                    t_price =  t_price+ Float.parseFloat(myCart.get(i).goods.get(j).goodsPrice)*myCart.get(i).goods.get(j).goodsNum;
                    t_num = t_num + myCart.get(i).goods.get(j).goodsNum;
                } else {
                    allSelected = false;
                    cartSelected = false;
                }
            }
            myCart.get(i).selected = cartSelected;
        }
        selectedGoodsNum.postValue(t_num);
        selectedPrice.postValue(t_price);
        mIsAllSelected.postValue(allSelected);
        mCarts.postValue(myCart);
    }

    public void delFromCart(String pkId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();

                String url = "/appUserCart/deleteById/" + pkId;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {

                                mResult.postValue("success");
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

}


