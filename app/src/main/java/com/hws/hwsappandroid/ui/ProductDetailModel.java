package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.AddToCart;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodInfo;
import com.hws.hwsappandroid.model.GoodsDetail;
import com.hws.hwsappandroid.model.GoodsPrice;
import com.hws.hwsappandroid.model.GoodsShop;
import com.hws.hwsappandroid.model.GoodsSpec;
import com.hws.hwsappandroid.model.GoodsSpecMap;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.SpecInfo;
import com.hws.hwsappandroid.model.UserCartItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class ProductDetailModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> mGoodImages = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Params>> mGoodsParam = new MutableLiveData<>();
    private final MutableLiveData<GoodInfo> mGoods = new MutableLiveData<>();
    private final MutableLiveData<String> mResult = new MutableLiveData<>();
    private final MutableLiveData<String> setResult = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ArrayList<String>> getGoodImages() {
        return mGoodImages;
    }
    public LiveData<ArrayList<Params>> getGoodsParam() {
        return mGoodsParam;
    }
    public LiveData<GoodInfo> getGoods() {
        return mGoods;
    }
    public LiveData<String> getResult() {
        return mResult;
    }
    public LiveData<String> getSetResult() {
        return setResult;
    }

    public void loadData(String goodsId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                String url = "/bizGoods/queryGoodsInfo";
                params.put("goodsId", goodsId);
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray goodsImg = obj.getJSONArray("goodsImg");
                                ArrayList<String> goodImages = new ArrayList<>();
                                for (int i=0; i<goodsImg.length(); i++) {
                                    String Images = goodsImg.getString(i);
                                    goodImages.add(Images);
                                }
                                mGoodImages.postValue(goodImages);

                                JSONObject goodsShopJson = obj.getJSONObject("goodsShop");
                                GoodsShop goodsShop = new GoodsShop();
                                goodsShop.address = goodsShopJson.optString("address", "");
                                goodsShop.bizClientId = goodsShopJson.optString("bizClientId", "");
                                goodsShop.bizScope = goodsShopJson.optString("bizScope", "");
                                goodsShop.city = goodsShopJson.optString("city", "");
                                goodsShop.district = goodsShopJson.optString("district", "");
                                goodsShop.gmtCreate = goodsShopJson.optString("gmtCreate", "");
                                goodsShop.gmtModified = goodsShopJson.optString("gmtModified", "");
                                goodsShop.linkmanName = goodsShopJson.optString("linkmanName", "");
                                goodsShop.linkmanPhone = goodsShopJson.optString("linkmanPhone", "");
                                goodsShop.mainIndustry = goodsShopJson.optString("mainIndustry", "");
                                goodsShop.chMerchandiseNum = goodsShopJson.optInt("chMerchandiseNum", 0);
                                goodsShop.chPutAwayNum = goodsShopJson.optInt("chPutAwayNum", 0);
                                goodsShop.chSoldOutNum = goodsShopJson.optInt("chSoldOutNum", 0);
                                goodsShop.operatorId = goodsShopJson.optString("operatorId", "");
                                goodsShop.pkId = goodsShopJson.optString("pkId", "");
                                goodsShop.province = goodsShopJson.optString("province", "");
                                goodsShop.shopLogoPic = goodsShopJson.optString("shopLogoPic", "");
                                goodsShop.shopName = goodsShopJson.optString("shopName", "");

                                GoodInfo goodInfo = new GoodInfo();
                                goodInfo.goodsImg = goodImages;
                                goodInfo.goodsShop = goodsShop;
                                goodInfo.canFavorite = obj.optBoolean("canFavorite", false);

                                JSONArray attributeList = obj.getJSONArray("attributeList");
                                ArrayList<String> mAttributeList = new ArrayList<>();
                                for (int i=0; i<attributeList.length(); i++) {
                                    String attribute = attributeList.getString(i);
                                    mAttributeList.add(attribute);
                                }
                                goodInfo.attributeList = mAttributeList;

                                JSONObject goodsPriceJson = obj.getJSONObject("goodsPrice");
                                GoodsPrice goodsPrice = new GoodsPrice();
                                goodsPrice.price = goodsPriceJson.optString("price", "");
                                goodsPrice.goods_spec = goodsPriceJson.optString("goods_spec", "");
                                goodInfo.goodsPrice = goodsPrice;

                                JSONObject goodsJson = obj.getJSONObject("goods");
                                Good goods = new Good();
                                goods.gmtModified = goodsJson.optString("gmtModified", "");
                                goods.pkId = goodsJson.optString("pkId", "");
                                goods.goodsSn = goodsJson.optString("goodsSn", "");
                                goods.price = obj.getJSONObject("goodsPrice").optString("price", "");
                                goods.category2Id = goodsJson.optString("category2Id", "");
                                goods.category1Id = goodsJson.optString("category1Id", "");

                                ArrayList<Params> goodsParam = new ArrayList<>();
                                JSONObject goodsParamJson = goodsJson.getJSONObject("goodsParam");
                                Iterator<String> keys = goodsParamJson.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    Params goodParam = new Params();
                                    goodParam.key = key;
                                    goodParam.value = goodsParamJson.optString(key, "");
                                    goodsParam.add(goodParam);
                                }
                                mGoodsParam.postValue(goodsParam);

                                goods.goodsParam = goodsParam;
                                goods.goodsPicPreferred = goodsJson.optString("goodsPicPreferred", "");
                                goods.category3Id = goodsJson.optString("category3Id", "");
                                goods.goodsPic = goodsJson.optString("goodsPic", "");
                                goods.shopId = goodsJson.optString("shopId", "");
                                goods.isOnSale = goodsJson.optInt("isOnSale", 1);
                                goods.goodsName = goodsJson.optString("goodsName", "");
                                goods.operatorId = goodsJson.optString("operatorId", "");
                                goods.outSaleTime = goodsJson.optString("outSaleTime", "");
                                goods.isDelete = goodsJson.optInt("isDelete", 0);
                                goods.salesNum = goodsJson.optInt("salesNum", 1);
                                goods.isPreferred = goodsJson.optInt("isPreferred", 1);
                                goods.gmtCreate = goodsJson.optString("gmtCreate", "");
                                goods.bizClientId = goodsJson.optString("bizClientId", "");
                                goods.goodsPreferredWeight = goodsJson.optInt("goodsPreferredWeight", 0);
                                goods.shippinTempletId = goodsJson.optString("shippinTempletId", "");
                                goods.onSaleTime = goodsJson.optString("onSaleTime", "");
                                goods.auditTime = goodsJson.optString("auditTime", "");
                                goods.isExeFromPos = goodsJson.optInt("isExeFromPos", 1);
                                goods.auditStatus = goodsJson.optInt("auditStatus", 0);
                                goods.isHot = goodsJson.optInt("isHot", 0);
                                goods.streetSort = goodsJson.optInt("streetSort", 0);

                                JSONArray goodsDetailListJson = goodsJson.getJSONArray("goodsDetail");
                                ArrayList<GoodsDetail> goodsDetails = new ArrayList<>();
                                for (int i=0; i<goodsDetailListJson.length(); i++) {
                                    JSONObject goodsDetailJson = goodsDetailListJson.getJSONObject(i);
                                    GoodsDetail goodsDetail = new GoodsDetail();
                                    goodsDetail.bizClientId = goodsDetailJson.optString("bizClientId", "");
                                    goodsDetail.gmtCreate = goodsDetailJson.optString("gmtCreate", "");
                                    goodsDetail.gmtModified = goodsDetailJson.optString("gmtModified", "");
                                    goodsDetail.goodsDetailImg = goodsDetailJson.optString("goodsDetailImg", "");
                                    goodsDetail.goodsId = goodsDetailJson.optString("goodsId", "");
                                    goodsDetail.operatorId = goodsDetailJson.optString("operatorId", "");
                                    goodsDetail.pkId = goodsDetailJson.optString("pkId", "");

                                    goodsDetails.add(goodsDetail);
                                }
                                goods.goodsDetail = goodsDetails;

                                goodInfo.goods = goods;

                                JSONObject specInfoMapJson = obj.getJSONObject("specInfoMap");
                                SpecInfo specInfoMap = new SpecInfo();
                                specInfoMap.goodsSpecName1 = specInfoMapJson.optString("goodsSpecName1", "");
                                specInfoMap.goodsSpecName2 = specInfoMapJson.optString("goodsSpecName2", "");

                                JSONArray goodsSpecValue1ListJson = specInfoMapJson.getJSONArray("goodsSpecValue1List");
                                ArrayList<String> goodsSpecValue1List = new ArrayList<>();
                                for (int i=0; i<goodsSpecValue1ListJson.length(); i++) {
                                    String goodsSpecValue1 = goodsSpecValue1ListJson.getString(i);
                                    goodsSpecValue1List.add(goodsSpecValue1);
                                }
                                specInfoMap.goodsSpecValue1List = goodsSpecValue1List;

                                JSONArray goodsSpecValue2ListJson = specInfoMapJson.getJSONArray("goodsSpecValue2List");
                                ArrayList<String> goodsSpecValue2List = new ArrayList<>();
                                for (int i=0; i<goodsSpecValue2ListJson.length(); i++) {
                                    String goodsSpecValue2 = goodsSpecValue2ListJson.getString(i);
                                    goodsSpecValue2List.add(goodsSpecValue2);
                                }
                                specInfoMap.goodsSpecValue2List = goodsSpecValue2List;

                                JSONObject goodsSpecMapJson = specInfoMapJson.getJSONObject("goodsSpecMap");
                                ArrayList<GoodsSpecMap> goodsSpecMapList = new ArrayList<>();
                                Iterator<String> mapKeys = goodsSpecMapJson.keys();
                                while (mapKeys.hasNext()) {
                                    String key = mapKeys.next();
                                    GoodsSpecMap goodsSpecMap = new GoodsSpecMap();
                                    goodsSpecMap.key = key;

                                    JSONObject goodsSpecJson = goodsSpecMapJson.optJSONObject(key);
                                    GoodsSpec goodsSpec = new GoodsSpec();
                                    goodsSpec.bizClientId = goodsSpecJson.optString("bizClientId", "");
                                    goodsSpec.gmtCreate = goodsSpecJson.optString("gmtCreate", "");
                                    goodsSpec.gmtModified = goodsSpecJson.optString("gmtModified", "");
                                    goodsSpec.goodsId = goodsSpecJson.optString("goodsId", "");
                                    goodsSpec.goodsSpec = goodsSpecJson.optString("goodsSpec", "");
                                    goodsSpec.goodsSpecImg = goodsSpecJson.optString("goodsSpecImg", "");
                                    goodsSpec.operatorId = goodsSpecJson.optString("operatorId", "");
                                    goodsSpec.pkId = goodsSpecJson.optString("pkId", "");
                                    goodsSpec.price = goodsSpecJson.optString("price", "");
                                    goodsSpec.specName1 = goodsSpecJson.optString("specName1", "");
                                    goodsSpec.specName2 = goodsSpecJson.optString("specName2", "");
                                    goodsSpec.specValue1 = goodsSpecJson.optString("specValue1", "");
                                    goodsSpec.specValue2 = goodsSpecJson.optString("specValue2", "");
                                    goodsSpec.stock = goodsSpecJson.optInt("stock", 1);

                                    goodsSpecMap.value = goodsSpec;
                                    goodsSpecMapList.add(goodsSpecMap);
                                }
                                specInfoMap.goodsSpecMap = goodsSpecMapList;
                                goodInfo.specInfoMap = specInfoMap;
                                goodInfo.bizUserId = obj.optString("bizUserId", "");

                                mGoods.postValue(goodInfo);

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

    public void addToCart(AddToCart userCart) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("goodsId", userCart.goodsId);       //  "goodsId": "商品id",
//                    jsonParams.put("goodsName", userCart.goodsName);   //  "goodsName": "商品名称",
                    jsonParams.put("goodsNum", userCart.goodsNum);     //  "goodsNum": "商品数量",
                    jsonParams.put("goodsSn", userCart.goodsSn);     //  "goodsNum": "商品数量",
//                    jsonParams.put("goodsPic", userCart.goodsPic);     //  "goodsPic": "商品图片",
//                    jsonParams.put("goodsPrice", userCart.goodsPrice); //  "goodsPrice": "商品单价",
//                    jsonParams.put("goodsSpec", userCart.goodsSpec);   //  "goodsSpec": "商品规格",
                    jsonParams.put("goodsSpecId", userCart.goodsSpecId);   //   "goodsSpecId": "商品规格id",
//                    jsonParams.put("isCheck", userCart.isCheck);   //        "isCheck": "是否选中 0 否 1是",
                    jsonParams.put("shopId", userCart.shopId);   //       "shopId": "商店id",
//                    jsonParams.put("shopName", userCart.shopName);   //        "shopName": "商店名称",
//                    jsonParams.put("userId", userCart.userId);   //        "userId": "用户id"
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserCart/saveCart";
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

    public void addToOrderList(UserCartItem userCart) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
//                    jsonParams.put("goodsId", userCart.goodsId);       //  "goodsId": "商品id",
//                    jsonParams.put("goodsName", userCart.goodsName);   //  "goodsName": "商品名称",
//                    jsonParams.put("goodsNum", userCart.goodsNum);     //  "goodsNum": "商品数量",
//                    jsonParams.put("goodsPic", userCart.goodsPic);     //  "goodsPic": "商品图片",
//                    jsonParams.put("goodsPrice", userCart.goodsPrice); //  "goodsPrice": "商品单价",
//                    jsonParams.put("goodsSpec", userCart.goodsSpec);   //  "goodsSpec": "商品规格",
//                    jsonParams.put("goodsSpecId", userCart.goodsSpecId);   //   "goodsSpecId": "商品规格id",
//                    jsonParams.put("isCheck", userCart.isCheck);   //        "isCheck": "是否选中 0 否 1是",
                    jsonParams.put("shopId", userCart.shopId);   //       "shopId": "商店id",
                    jsonParams.put("shopName", userCart.shopName);   //        "shopName": "商店名称",
                    jsonParams.put("userId", userCart.userId);   //        "userId": "用户id"
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserCart/saveBySpec";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {

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

    public void setFavorite(String pkId) {
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("goodsId", pkId);       //  "goodsId": "商品id",

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserFavorite/save" ;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                String pkId = response.optString("data", "");
                                setResult.postValue(pkId);
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

    public void cancelFavorite(String pkId) {
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("goodsId", pkId);       //  "goodsId": "商品id",

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserFavorite/deleteByGoodsId" ;
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

//{
//        "goodsId":"3fedc8d9b9364afc9bdbcdf565dd2005",
//        "goodsSpec":"灰色-185/2XL",
//        "goodsSpecId":"07484047ee514c1e8836eabb81fe0abb",
//        "goodsNum":1,
//        "addressId":"ce492b9ae5f640d692141acfae05db36"
//        }
//
//{
//    "aoList":[
//        {
//            "goodsId":"3fedc8d9b9364afc9bdbcdf565dd2005",
//            "goodsSpec":"灰色-185/2XL",
//            "goodsSpecId":"07484047ee514c1e8836eabb81fe0abb",
//            "goodsNum": 1,
//            "addressId":"ce492b9ae5f640d692141acfae05db36"
//        }
//    ]
//}