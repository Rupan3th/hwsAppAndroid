package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.RefundModel;
import com.hws.hwsappandroid.model.ShopInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class AfterSalesListModel extends ViewModel {
    private final MutableLiveData<ArrayList<RefundModel>> mRefundList = new MutableLiveData<>();
    private final MutableLiveData<RefundModel> mRefund = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ArrayList<RefundModel>> getRefundList() {
        return mRefundList;
    }
    public LiveData<RefundModel> getRefund() {
        return mRefund;
    }

    public void loadData() {
        if (isLoading) return;
        isLoading = true;
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

    public void loadData(String pkId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("refundId", pkId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizGoodsRefund/queryRefundList";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("refundInfoList");
                                JSONObject refundObj = list.getJSONObject(0);

                                RefundModel refund = new RefundModel();
                                refund.billPayCode = refundObj.optString("billPayCode", "");
                                refund.bizClientId = refundObj.optString("bizClientId", "");
                                refund.detailDescription = refundObj.optString("detailDescription", "");
                                refund.gmtCreate = refundObj.optString("gmtCreate", "");
                                refund.operatorId = refundObj.optString("operatorId", "");
                                refund.orderGoodsId = refundObj.optString("orderGoodsId", "");
                                refund.orderId = refundObj.optString("orderId", "");
                                refund.pkId = refundObj.optString("pkId", "");
                                refund.receivingStatus = refundObj.optInt("receivingStatus", 1);
                                refund.refundApplyStatus = refundObj.optInt("refundApplyStatus", 1);
                                refund.refundApplyTime = refundObj.optString("refundApplyTime", "");
                                refund.refundCompleteTime = refundObj.optString("refundCompleteTime", "");
                                refund.refundCode = refundObj.optString("refundCode", "");
                                refund.refundMoney = refundObj.optString("refundMoney", "");
                                refund.refundMoneyType = refundObj.optInt("refundMoneyType", 1);
                                refund.refundReason = refundObj.optInt("refundReason", 1);
                                refund.refundType = refundObj.optInt("refundType", 1);
                                refund.userId = refundObj.optString("userId", "");
                                refund.goodsImg = refundObj.optString("goodsImg", "");
                                refund.bizClientUserId = refundObj.optString("bizClientUserId", "");
                                refund.shopId = refundObj.optString("shopId", "");
                                refund.shopName = refundObj.optString("shopName", "");
                                refund.goodsId = refundObj.optString("goodsId", "");
                                refund.goodsPic = refundObj.optString("goodsPic", "");
                                refund.goodsName = refundObj.optString("goodsName", "");
                                refund.goodsNum = refundObj.optInt("goodsNum", 1);
                                refund.goodsSpec = refundObj.optString("goodsSpec", "");
                                refund.goodsPrice = refundObj.optString("goodsPrice", "");

                                mRefund.postValue(refund);


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

    public void loadDataPK(String pkId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();

                String url = "/bizGoodsRefund/findById/" + pkId;
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                RefundModel refund = new RefundModel();
                                refund.billPayCode = obj.optString("billPayCode", "");
                                refund.bizClientId = obj.optString("bizClientId", "");
                                refund.detailDescription = obj.optString("detailDescription", "");
                                refund.gmtCreate = obj.optString("gmtCreate", "");
                                refund.operatorId = obj.optString("operatorId", "");
                                refund.orderGoodsId = obj.optString("orderGoodsId", "");
                                refund.orderId = obj.optString("orderId", "");
                                refund.pkId = obj.optString("pkId", "");
                                refund.receivingStatus = obj.optInt("receivingStatus", 1);
                                refund.refundApplyStatus = obj.optInt("refundApplyStatus", 1);
                                refund.refundApplyTime = obj.optString("refundApplyTime", "");
                                refund.refundCode = obj.optString("refundCode", "");
                                refund.refundMoney = obj.optString("refundMoney", "");
                                refund.refundMoneyType = obj.optInt("refundMoneyType", 1);
                                refund.refundReason = obj.optInt("refundReason", 1);
                                refund.refundType = obj.optInt("refundType", 1);
                                refund.userId = obj.optString("userId", "");
                                refund.goodsImg = obj.optString("goodsImg", "");
                                refund.bizClientUserId = obj.optString("bizClientUserId", "");
                                refund.shopId = obj.optString("shopId", "");
                                refund.shopName = obj.optString("shopName", "");
                                refund.goodsPic = obj.optString("goodsPic", "");
                                refund.goodsId = obj.optString("goodsId", "");
                                refund.goodsName = obj.optString("goodsName", "");
                                refund.goodsNum = obj.optInt("goodsNum", 1);
                                refund.goodsSpec = obj.optString("goodsSpec", "");
                                refund.goodsPrice = obj.optString("goodsPrice", "");

                                mRefund.postValue(refund);


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
}