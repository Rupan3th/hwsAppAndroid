package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.MyOrderModel;
import com.hws.hwsappandroid.model.OrderDetailModel;
import com.hws.hwsappandroid.model.OrderGoods;
import com.hws.hwsappandroid.model.Params;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class WaitingPaymentModel extends ViewModel {
    private boolean isLoading = false;
    private final MutableLiveData<OrderDetailModel> mOrderDetail = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private final MutableLiveData<String> mResult = new MutableLiveData<>();

    public LiveData<OrderDetailModel> getOrderDetail() {  return mOrderDetail;   }
    public LiveData<ArrayList<Good>> getGoods() {
        return mGoods;
    }
    public LiveData<String> getResult() {
        return mResult;
    }

    public void loadData(String orderId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("orderId", orderId);

                String url = "/bizOrder/queryOrderInfo";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                OrderDetailModel orderDetail = new OrderDetailModel();

                                orderDetail.address = obj.optString("address", "");
                                orderDetail.bizUserId = obj.optString("bizUserId", "");
                                orderDetail.cancelTime = obj.optString("cancelTime", "");
                                orderDetail.consignee = obj.optString("consignee", "");
                                JSONArray GoodsListJson = obj.getJSONArray("goodsInfoList");
                                ArrayList<OrderGoods> goodsInfoList = new ArrayList<>();
                                for(int i=0; i<GoodsListJson.length(); i++){
                                    JSONObject orderGoodJson = GoodsListJson.getJSONObject(i);
                                    OrderGoods orderGood = new OrderGoods();
                                    orderGood.goodsId = orderGoodJson.optString("goodsId", "");
                                    orderGood.goodsName = orderGoodJson.optString("goodsName", "");
                                    orderGood.goodsNum = orderGoodJson.optInt("goodsNum", 0);
                                    orderGood.goodsPic = orderGoodJson.optString("goodsPic", "");
                                    orderGood.goodsPrice = orderGoodJson.optString("goodsPrice", "");
                                    orderGood.goodsSpec = orderGoodJson.optString("goodsSpec", "");
                                    orderGood.goodsSpecId = orderGoodJson.optString("goodsSpecId", "");
                                    orderGood.isChargeback = orderGoodJson.optBoolean("isChargeback", false);
                                    orderGood.orderGoodsId = orderGoodJson.optString("orderGoodsId", "");
                                    orderGood.pkId = orderGoodJson.optString("pkId", "");
                                    orderGood.refundApplyStatus = orderGoodJson.optInt("refundApplyStatus", 0);

                                    goodsInfoList.add(orderGood);
                                }
                                orderDetail.goodsInfoList = goodsInfoList;
                                orderDetail.orderCode = obj.optString("orderCode", "");
                                orderDetail.orderStatus = obj.optInt("orderStatus", 0);
                                orderDetail.orderSurplusTime = obj.optInt("orderSurplusTime", 0);
                                orderDetail.orderTime = obj.optString("orderTime", "");
                                orderDetail.payTime = obj.optString("payTime", "");
                                orderDetail.payType = obj.optInt("payType", 1);
                                orderDetail.phone = obj.optString("phone", "");
                                orderDetail.pkId = obj.optString("pkId", "");
                                orderDetail.shopId = obj.optString("shopId", "");
                                orderDetail.shippingFee = obj.optString("shippingFee", "");
                                orderDetail.shopName = obj.optString("shopName", "");
                                orderDetail.totalMoney = obj.optString("totalMoney", "");

                                mOrderDetail.postValue(orderDetail);

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

    public void confirmOrder(String orderId){
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("orderId", orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/confirmOrder" ;
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
