package com.hws.hwsappandroid;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Good;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProductDetailModel extends ViewModel {
    private final MutableLiveData<ArrayList<Banner>> mBanners = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ArrayList<Banner>> getBanners() {
        return mBanners;
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
}

class Banner {
    public String bannerPic;
    public int enableStatus;
    public String gotoContent;
    public int gotoType;
    public String pkId;
    public String sort;
}
