package com.hws.hwsappandroid.ui.home;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Good;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Banner>> mBanners = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<HomeCategory>> mCategories = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ArrayList<Banner>> getBanners() {
        return mBanners;
    }

    public LiveData<ArrayList<HomeCategory>> getCategories() {
        return mCategories;
    }

    public LiveData<ArrayList<Good>> getGoods() {
        return mGoods;
    }

    public void loadData() {
        if (isLoading) return;
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

                                JSONArray bannersJson = obj.getJSONArray("banners");
                                ArrayList<Banner> bannerArr = new ArrayList<>();
                                for (int i=0; i<bannersJson.length(); i++) {
                                    JSONObject bJson = bannersJson.getJSONObject(i);
                                    Banner banner = new Banner();
                                    banner.bannerPic = bJson.optString("bannerPic");
                                    banner.enableStatus = bJson.optInt("enableStatus", 1);
                                    banner.gotoContent = bJson.optString("gotoContent", "");
                                    banner.gotoType = bJson.optInt("gotoType", 1);
                                    banner.pkId = bJson.optString("pkId", "");
                                    banner.sort = bJson.optString("sort", "1");
                                    bannerArr.add(banner);
                                }
                                mBanners.postValue(bannerArr);

                                JSONArray categoriesJson = obj.getJSONArray("categorys");
                                ArrayList<HomeCategory> categoryArr = new ArrayList<>();
                                for (int i=0; i<categoriesJson.length(); i++) {
                                    JSONObject bJson = categoriesJson.getJSONObject(i);
                                    HomeCategory category = new HomeCategory();
                                    category.code = bJson.optString("categoryCode", "");
                                    category.name = bJson.optString("categoryName", "");
                                    category.img = bJson.optString("categoryImg", "https://picsum.photos/64/64");
                                    category.level = bJson.optInt("level", 1);
                                    category.operatorId = bJson.optString("operatorId", "");
                                    category.parentId = bJson.optInt("parentId", 1);
                                    category.pkId = bJson.optString("pkId", "");
                                    category.sortValue = bJson.optInt("sortValue", 1);
                                    categoryArr.add(category);
                                }
                                mCategories.postValue(categoryArr);

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
                                    good.price = json.optInt("price", 1);
                                    good.salesNum = json.optInt("salesNum", 1);
                                    good.isPreferred = json.optInt("isPreferred", 1);
                                    good.shopId = json.optString("shopId", "");
                                    good.operatorId = json.optString("operatorId", "");
                                    good.bizClientId = json.optString("bizClientId", "");
                                    good.onSaleTime = json.optString("onSaleTime", "");
                                    good.outSaleTime = json.optString("outSaleTime", "");
                                    good.gmtCreate = json.optString("gmtCreate", "");
                                    good.gmtModified = json.optString("gmtModified", "");
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
                                    good.price = json.optInt("price", 1);
                                    good.salesNum = json.optInt("salesNum", 1);
                                    good.isPreferred = json.optInt("isPreferred", 1);
                                    good.shopId = json.optString("shopId", "");
                                    good.operatorId = json.optString("operatorId", "");
                                    good.bizClientId = json.optString("bizClientId", "");
                                    good.onSaleTime = json.optString("onSaleTime", "");
                                    good.outSaleTime = json.optString("outSaleTime", "");
                                    good.gmtCreate = json.optString("gmtCreate", "");
                                    good.gmtModified = json.optString("gmtModified", "");
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
}

class Banner {
    public String bannerPic;
    public int enableStatus;
    public String gotoContent;
    public int gotoType;
    public String pkId;
    public String sort;
}

class HomeCategory {
    public String code;
    public String name;
    public String img;
    public int level;
    public String operatorId;
    public int parentId;
    public String pkId;
    public int sortValue;
}