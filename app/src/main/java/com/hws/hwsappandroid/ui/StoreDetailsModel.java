package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.ShopInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class StoreDetailsModel extends ViewModel {
    private final MutableLiveData<ShopInfo> mShopInfo = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ShopInfo> getShopInfo() {
        return mShopInfo;
    }

    public LiveData<ArrayList<Good>> getGoods() {
        return mGoods;
    }

    public void loadData(String shopId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("shopId", shopId);
                params.put("pageNum", 1);
                String url = "/bizShop/queryShopInfo";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                obj = list.getJSONObject(0);

                                JSONObject shopJson = obj.getJSONObject("shop");
                                ShopInfo shop = new ShopInfo();

                                shop.address = shopJson.optString("address", "");
                                shop.bizClientId = shopJson.optString("bizClientId", "");
                                shop.bizScope = shopJson.optString("bizScope", "");
                                shop.city = shopJson.optString("city", "");
                                shop.district = shopJson.optString("district", "");
                                shop.gmtCreate = shopJson.optString("gmtCreate", "");
                                shop.gmtModified = shopJson.optString("gmtModified", "");
                                shop.linkmanName = shopJson.optString("linkmanName", "");
                                shop.linkmanPhone = shopJson.optString("linkmanPhone", "");
                                shop.mainIndustry = shopJson.optString("mainIndustry", "");
                                shop.modifyTime = shopJson.optInt("modifyTime", 1);
                                shop.operatorId = shopJson.optString("operatorId", "");
                                shop.pkId = shopJson.optString("pkId", "");
                                shop.province = shopJson.optString("province", "");
                                shop.shopLogoPic = shopJson.optString("shopLogoPic", "");
                                shop.shopName = shopJson.optString("shopName", "");

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

                                shop.goods = goodArr;
                                mShopInfo.postValue(shop);

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

    public void loadMoreGoods(String shopId, int pageNum) {
        if (isLoading) return;
        isLoading = true;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.d("Home", "Trying to load more...");
                RequestParams params = new RequestParams();
                params.put("shopId", shopId);
                params.put("pageNum", pageNum);
                ArrayList<Good> goodArr = mGoods.getValue();
//                if (goodArr.size() > 0) {
//                    params.put("pageNum", goodArr.size()/20 + 1);
//                }

                String url = "/bizShop/queryShopInfo";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                obj = list.getJSONObject(0);

                                JSONObject shopJson = obj.getJSONObject("shop");
                                ShopInfo shop = new ShopInfo();

                                shop.address = shopJson.optString("address", "");
                                shop.bizClientId = shopJson.optString("bizClientId", "");
                                shop.city = shopJson.optString("city", "");
                                shop.district = shopJson.optString("district", "");
                                shop.gmtCreate = shopJson.optString("gmtCreate", "");
                                shop.gmtModified = shopJson.optString("gmtModified", "");
                                shop.mainIndustry = shopJson.optString("mainIndustry", "");
                                shop.modifyTime = shopJson.optInt("modifyTime", 1);
                                shop.operatorId = shopJson.optString("operatorId", "");
                                shop.pkId = shopJson.optString("pkId", "");
                                shop.province = shopJson.optString("province", "");
                                shop.shopLogoPic = shopJson.optString("shopLogoPic", "");
                                shop.shopName = shopJson.optString("shopName", "");

                                JSONArray goodsJson = obj.getJSONObject("goods").getJSONArray("list");
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

                                shop.goods = goodArr;
                                mShopInfo.postValue(shop);

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
}
