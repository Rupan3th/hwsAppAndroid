package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.DeliveryResultListModel;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodsInfoVO;
import com.hws.hwsappandroid.model.LogisticsModel;
import com.hws.hwsappandroid.model.LogisticsStateList;
import com.hws.hwsappandroid.model.OrderGoods;
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

public class LogisticsInformationModel extends ViewModel {
    private final MutableLiveData<LogisticsModel> mLogisticsInfo = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Good>> mGoods = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<LogisticsModel> getLogisticsInfo() {
        return mLogisticsInfo;
    }

    public LiveData<ArrayList<Good>> getGoods() {
        return mGoods;
    }

    public void loadData(String orderId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("orderId", orderId);
                String url = "/bizOrderDelivery/lookLogistics";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                LogisticsModel logisticsInfo = new LogisticsModel();

                                logisticsInfo.consignee = obj.optString("consignee", "");
                                logisticsInfo.phone = obj.optString("phone", "");

                                JSONArray deliveryResultListJson = obj.getJSONArray("list");
                                ArrayList<DeliveryResultListModel> deliveryResultList = new ArrayList<>();
                                for(int i=0; i<deliveryResultListJson.length(); i++){
                                    JSONObject deliveryResultJson = deliveryResultListJson.getJSONObject(i);
                                    DeliveryResultListModel deliveryResult = new DeliveryResultListModel();

                                    deliveryResult.courier = deliveryResultJson.optString("courier", "");
                                    deliveryResult.courierPhone = deliveryResultJson.optString("courierPhone", "");
                                    deliveryResult.deliverystatus = deliveryResultJson.optString("deliverystatus", "");
                                    deliveryResult.expName = deliveryResultJson.optString("expName", "");
                                    deliveryResult.expPhone = deliveryResultJson.optString("expPhone", "");
                                    deliveryResult.expSite = deliveryResultJson.optString("expSite", "");
                                    deliveryResult.issign = deliveryResultJson.optString("issign", "");

                                    JSONArray goodsInfoVOJson = deliveryResultJson.getJSONArray("goodsInfoVO");
                                    ArrayList<GoodsInfoVO> goodsInfoVO = new ArrayList<>();
                                    for(int ii=0; ii<goodsInfoVOJson.length(); ii++){
                                        JSONObject goodsInfoJson = goodsInfoVOJson.getJSONObject(ii);
                                        GoodsInfoVO goodsInfo = new GoodsInfoVO();
                                        goodsInfo.goodsId = goodsInfoJson.optString("goodsId", "");
                                        goodsInfo.goodsName = goodsInfoJson.optString("goodsName", "");
                                        goodsInfo.goodsNum = goodsInfoJson.optInt("goodsNum", 1);
                                        goodsInfo.goodsPic = goodsInfoJson.optString("goodsPic", "");
                                        goodsInfo.goodsPrice = goodsInfoJson.optString("goodsPrice", "");
                                        goodsInfo.goodsSpec = goodsInfoJson.optString("goodsSpec", "");
                                        goodsInfo.goodsSpecId = goodsInfoJson.optString("goodsSpecId", "");
                                        goodsInfo.isChargeback = goodsInfoJson.optBoolean("goodsSpecId", false);
                                        goodsInfo.orderGoodsId = goodsInfoJson.optString("orderGoodsId", "");
                                        goodsInfo.refundApplyStatus = goodsInfoJson.optInt("refundApplyStatus", 1);
                                        goodsInfo.pkId = goodsInfoJson.optString("pkId", "");
                                        goodsInfo.shopId = goodsInfoJson.optString("shopId", "");
                                        goodsInfo.shopName = goodsInfoJson.optString("shopName", "");

                                        goodsInfoVO.add(goodsInfo);
                                    }
                                    deliveryResult.goodsInfoVO = goodsInfoVO;

                                    JSONArray listJson = deliveryResultJson.getJSONArray("list");
                                    ArrayList<LogisticsStateList> list = new ArrayList<>();
                                    for(int j=0; j<listJson.length(); j++){
                                        JSONObject stateJson = listJson.getJSONObject(j);
                                        LogisticsStateList logisticsState = new LogisticsStateList();

                                        logisticsState.status = stateJson.optString("status", "");
                                        logisticsState.time = stateJson.optString("time", "");
                                        list.add(logisticsState);
                                    }
                                    deliveryResult.list = list;

                                    deliveryResult.logo = deliveryResultJson.optString("logo", "");
                                    deliveryResult.number = deliveryResultJson.optString("number", "");
                                    deliveryResult.takeTime = deliveryResultJson.optString("takeTime", "");
                                    deliveryResult.type = deliveryResultJson.optString("type", "");
                                    deliveryResult.updateTime = deliveryResultJson.optString("updateTime", "");

                                    deliveryResultList.add(deliveryResult);
                                }

                                logisticsInfo.deliveryResultList = deliveryResultList;

                                mLogisticsInfo.postValue(logisticsInfo);

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
