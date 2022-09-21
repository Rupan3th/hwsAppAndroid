package com.hws.hwsappandroid.ui.me;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.FavoriteGood;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodOfShoppingCart;
import com.hws.hwsappandroid.model.MyOrderModel;
import com.hws.hwsappandroid.model.MyOrderNumByStatus;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.RefundModel;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class MeViewModel extends ViewModel {

    int pending_payment, pending_shipment, pending_receipt, complete, canceled;

    private final MutableLiveData<String> mText;
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private final MutableLiveData<MyOrderNumByStatus> MyOrderNum = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<RefundModel>> mRefundList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<FavoriteGood>> mFavoriteGoods = new MutableLiveData<>();

    public LiveData<String> getResult() {
        return mText;
    }
    public LiveData<ArrayList<Good>> getGoods() {
        return mGoods;
    }
    public LiveData<MyOrderNumByStatus> getOrderNum() {
        return MyOrderNum;
    }
    public LiveData<ArrayList<RefundModel>> getRefundList() {
        return mRefundList;
    }
    public LiveData<ArrayList<FavoriteGood>> getFavoriteList() {
        return mFavoriteGoods;
    }

    private boolean isLoading = false;

    public MeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Me fragment");
    }

    public LiveData<String> getText() {
        return mText;
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

    public void GetOrderByStatus(){
        pending_payment = 0;
        pending_shipment = 0;
        pending_receipt = 0;
        complete = 0;
        canceled = 0;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("pageNum", 1);
                    jsonParams.put("pageSize", "50");
                    jsonParams.put("status", "-1");
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

                                for (int i=0; i<list.length(); i++) {
                                    obj = list.getJSONObject(i);

                                    int orderStatus = obj.optInt("orderStatus", 0);
                                    if(orderStatus == 0) pending_payment ++;
                                    if(orderStatus == 3) pending_shipment ++;
                                    if(orderStatus == 4) pending_receipt ++;
                                    if(orderStatus == 5) complete ++;
                                    if(orderStatus == 99) canceled ++;

                                }
                                MyOrderNumByStatus myOrderNum = new MyOrderNumByStatus();
                                myOrderNum.pending_payment = pending_payment;
                                myOrderNum.pending_shipment = pending_shipment;
                                myOrderNum.pending_receipt = pending_receipt;
                                myOrderNum.complete = complete;
                                myOrderNum.canceled = canceled;
                                myOrderNum.all_num = pending_payment + pending_shipment + pending_receipt + complete + canceled;

                                MyOrderNum.postValue(myOrderNum);

                            } else {
                                Log.d("Home request", response.toString());
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

    public void loadRefundList(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();

                String url = "/bizGoodsRefund/queryRefundList";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("refundInfoList");
                                ArrayList<RefundModel> refundList = new ArrayList<>();
                                for(int i=0; i<list.length(); i++){
                                    JSONObject refundObj = list.getJSONObject(i);
                                    RefundModel refund = new RefundModel();
                                    refund.billPayCode = refundObj.optString("billPayCode", "");
                                    refund.bizClientId = refundObj.optString("bizClientId", "");
                                    refund.bizClientUserId = refundObj.optString("clientUserId", "");
                                    refund.customerName = refundObj.optString("customerName", "");
                                    refund.customerPhone = refundObj.optString("customerPhone", "");
                                    refund.detailDescription = refundObj.optString("detailDescription", "");
                                    refund.gmtCreate = refundObj.optString("gmtCreate", "");
                                    refund.gmtModified = refundObj.optString("gmtModified", "");
                                    refund.goodsId = refundObj.optString("goodsId", "");
                                    refund.goodsName = refundObj.optString("goodsName", "");
                                    refund.goodsNum = refundObj.optInt("goodsNum", 1);
                                    refund.goodsPic = refundObj.optString("goodsPic", "");
                                    refund.goodsPrice = refundObj.optString("goodsPrice", "");
                                    refund.goodsSpec = refundObj.optString("goodsSpec", "");
                                    refund.operatorId = refundObj.optString("operatorId", "");
                                    refund.orderCode = refundObj.optString("orderCode", "");
                                    refund.orderGoodsId = refundObj.optString("orderGoodsId", "");
                                    refund.orderId = refundObj.optString("orderId", "");
                                    refund.pkId = refundObj.optString("pkId", "");
                                    refund.receivingStatus = refundObj.optInt("receivingStatus", 1);
                                    refund.refundApplyStatus = refundObj.optInt("refundApplyStatus", 1);
                                    refund.refundApplyTime = refundObj.optString("refundApplyTime", "");
                                    refund.refundCode = refundObj.optString("refundCode", "");
                                    refund.refundMoney = refundObj.optString("refundMoney", "");
                                    refund.refundMoneyType = refundObj.optInt("refundMoneyType", 1);
                                    refund.refundPhone = refundObj.optString("refundPhone", "");
                                    refund.refundReason = refundObj.optInt("refundReason", 1);
                                    refund.refundType = refundObj.optInt("refundType", 1);
                                    refund.shopId = refundObj.optString("shopId", "");
                                    refund.shopName = refundObj.optString("shopName", "");
                                    refund.userId = refundObj.optString("userId", "");

                                    refund.goodsImg = refundObj.optString("goodsImg", "");
                                    refund.refundCompleteTime = refundObj.optString("refundCompleteTime", "");
                                    refundList.add(refund);
                                }

                                mRefundList.postValue(refundList);


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
                        Log.d("Home request", "" + statusCode);
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

    public void loadFavoriteList(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                String url = "/appUserFavorite/queryByUserId";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray FavoriteJson = obj.getJSONArray("list");
                                ArrayList<FavoriteGood> favoriteGoods = new ArrayList<>();
                                for (int i=0; i<FavoriteJson.length(); i++) {
                                    JSONObject json = FavoriteJson.getJSONObject(i);
                                    FavoriteGood favoriteGood = new FavoriteGood();
                                    favoriteGood.gmtModified = json.optString("gmtModified", "");
                                    favoriteGood.pkId = json.optString("pkId", "");
                                    favoriteGood.goodsId = json.optString("goodsId", "");
                                    favoriteGood.goodsName = json.optString("goodsName", "");
                                    favoriteGood.price = json.optString("price", "");
                                    favoriteGood.gmtCreate = json.optString("gmtCreate", "");
                                    favoriteGood.goodsSpecImg = json.optString("goodsSpecImg", "");
                                    favoriteGood.operatorId = json.optString("operatorId", "");
                                    favoriteGood.userId = json.optString("userId", "");
                                    favoriteGood.goodsSpec = json.optString("goodsSpec", "");
                                    favoriteGoods.add(favoriteGood);
                                }
                                mFavoriteGoods.postValue(favoriteGoods);

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

    public void updateUserInfo(String pkId, String account, String avatarPic, String birthday, String gender, String name){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("pkId", pkId);
                    jsonParams.put("account", account);
                    jsonParams.put("avatarPic", avatarPic);
                    jsonParams.put("birthday", birthday);
                    jsonParams.put("gender", gender);
                    jsonParams.put("name", name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String url = "/appUser/update";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {

                                mText.postValue("Success");

                            } else {
                                Log.d("Home request", response.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Home request", ""+statusCode);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Home request", responseString);
                    }
                });
            }
        });
    }
}