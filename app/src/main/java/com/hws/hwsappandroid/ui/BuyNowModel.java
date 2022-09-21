package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.OrderInfoVO;
import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.SaveOrderShop;
import com.hws.hwsappandroid.model.SubmitOrder;
import com.hws.hwsappandroid.model.aoListItem;
import com.hws.hwsappandroid.model.shippingAdr;
import com.hws.hwsappandroid.model.submitOrderGoodsInfo;
import com.hws.hwsappandroid.model.submitOrderInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BuyNowModel extends ViewModel {

    private final MutableLiveData<shippingAdr> mDefaultAddress = new MutableLiveData<>();
    private final MutableLiveData<SubmitOrder> mSubmitOrder = new MutableLiveData<>();
    private final MutableLiveData<String> mResult = new MutableLiveData<>();

    private boolean isLoading = false;

    public LiveData<shippingAdr> getAddress() {
        return mDefaultAddress;
    }
    public LiveData<SubmitOrder> getSubmitOrder() {
        return mSubmitOrder;
    }
    public LiveData<String> getResult() { return mResult;   }

    public void loadData() {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                String url = "/appUserAddress/queryByUserId";
//                params.put("goodsId", goodsId);
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject json = response.getJSONObject("data");
                                shippingAdr shippingAddress = new shippingAdr();

                                shippingAddress.address = json.optString("address", "");
                                shippingAddress.addressDefault = json.optString("addressDefault", "0");
                                shippingAddress.city = json.optString("city", "");
                                shippingAddress.consignee = json.optString("consignee", "");
                                shippingAddress.consigneeXb = json.optString("consigneeXb", "1");
                                shippingAddress.country = json.optString("country", "");
                                shippingAddress.district = json.optString("district", "");
                                shippingAddress.gmtCreate = json.optString("gmtCreate", "");
                                shippingAddress.gmtModified = json.optString("gmtModified", "");
                                shippingAddress.operatorId = json.optString("operatorId", "");
                                shippingAddress.phone = json.optString("phone", "");
                                shippingAddress.pkId = json.optString("pkId", "");
                                shippingAddress.province = json.optString("province", "");
                                shippingAddress.userId = json.optString("userId", "");

                                mDefaultAddress.postValue(shippingAddress);

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

    public void saveOrder(OrderInfoVO orderInfoVO) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("addressId", orderInfoVO.addressId);
                    jsonParams.put("totalMoney", orderInfoVO.totalMoney);
                    jsonParams.put("payType", orderInfoVO.payType);
                    Gson gson = new Gson();
                    String listString = gson.toJson(
                            orderInfoVO.saveOrderShop,
                            new TypeToken<ArrayList<SaveOrderShop>>() {}.getType());
                    JSONArray saveOrderShop =  new JSONArray(listString);
                    jsonParams.put("saveOrderShop", saveOrderShop);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/saveOrder";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject json = response.getJSONObject("data");
                                String cartOrderCode = json.optString("cartOrderCode", "");

                                mResult.postValue(cartOrderCode);

                            } else {
                                Log.d("Home request", response.toString());
                                mResult.postValue("failed");
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

    public void submit_order(ArrayList<aoListItem> aoList) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    Gson gson = new Gson();
                    String listString = gson.toJson(
                            aoList,
                            new TypeToken<ArrayList<aoListItem>>() {}.getType());
                    JSONArray aoListJson =  new JSONArray(listString);
                    jsonParams.put("aoList", aoListJson);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizOrder/submit/orderInfo";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject json = response.getJSONObject("data");
                                SubmitOrder submitOrders = new SubmitOrder();

                                JSONObject defaultAddressJson = json.getJSONObject("defaultAddress");
                                shippingAdr defaultAddress = new shippingAdr();
                                defaultAddress.address = defaultAddressJson.optString("address", "");
                                defaultAddress.city = defaultAddressJson.optString("city", "");
                                defaultAddress.consignee = defaultAddressJson.optString("consignee", "");
                                defaultAddress.country = defaultAddressJson.optString("country", "");
                                defaultAddress.district = defaultAddressJson.optString("district", "");
                                defaultAddress.phone = defaultAddressJson.optString("phone", "");
                                defaultAddress.pkId = defaultAddressJson.optString("pkId", "");
                                defaultAddress.province = defaultAddressJson.optString("province", "");

                                submitOrders.defaultAddress = defaultAddress;
                                submitOrders.isFreeOfCharge = json.optString("isFreeOfCharge","");

                                JSONArray submitOrderInfoListJson = json.getJSONArray("submitOrderInfoList");
                                ArrayList<submitOrderInfo> submitOrderInfoList = new ArrayList<>();
                                for(int i=0; i<submitOrderInfoListJson.length(); i++){
                                    JSONObject submitOrderInfoJson = submitOrderInfoListJson.getJSONObject(i);
                                    submitOrderInfo mSubmitOrderInfo = new submitOrderInfo();
                                    mSubmitOrderInfo.shippingFee = submitOrderInfoJson.optString("shippingFee", "");
                                    mSubmitOrderInfo.shopFee = submitOrderInfoJson.optString("shopFee", "");
                                    mSubmitOrderInfo.shopId = submitOrderInfoJson.optString("shopId", "");
                                    mSubmitOrderInfo.shopName = submitOrderInfoJson.optString("shopName", "");

                                    JSONArray submitOrderGoodsInfoListJson = submitOrderInfoJson.getJSONArray("submitOrderGoodsInfoList");
                                    ArrayList<submitOrderGoodsInfo> submitOrderGoodsInfoList = new ArrayList<>();
                                    for(int j=0; j<submitOrderGoodsInfoListJson.length(); j++){
                                        JSONObject submitOrderGoodsInfoJson = submitOrderGoodsInfoListJson.getJSONObject(j);
                                        submitOrderGoodsInfo mSubmitOrderGoodsInfo = new submitOrderGoodsInfo();
                                        mSubmitOrderGoodsInfo.goodsId = submitOrderGoodsInfoJson.optString("goodsId", "");
                                        mSubmitOrderGoodsInfo.goodsName = submitOrderGoodsInfoJson.optString("goodsName", "");
                                        mSubmitOrderGoodsInfo.goodsSn = submitOrderGoodsInfoJson.optString("goodsSn", "");
                                        mSubmitOrderGoodsInfo.goodsNum = submitOrderGoodsInfoJson.optInt("goodsNum", 1);
                                        mSubmitOrderGoodsInfo.goodsPic = submitOrderGoodsInfoJson.optString("goodsPic", "");
                                        mSubmitOrderGoodsInfo.goodsPrice = submitOrderGoodsInfoJson.optString("goodsPrice", "");
                                        mSubmitOrderGoodsInfo.goodsSpec = submitOrderGoodsInfoJson.optString("goodsSpec", "");
                                        mSubmitOrderGoodsInfo.goodsSpecId = submitOrderGoodsInfoJson.optString("goodsSpecId", "");
                                        submitOrderGoodsInfoList.add(mSubmitOrderGoodsInfo);
                                    }
                                    mSubmitOrderInfo.submitOrderGoodsInfoList = submitOrderGoodsInfoList;
                                    submitOrderInfoList.add(mSubmitOrderInfo);
                                }

                                submitOrders.submitOrderInfoList = submitOrderInfoList;
                                submitOrders.totalMoney = json.optString("totalMoney","");
                                mSubmitOrder.postValue(submitOrders);
//                                mResult.postValue("success");

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
