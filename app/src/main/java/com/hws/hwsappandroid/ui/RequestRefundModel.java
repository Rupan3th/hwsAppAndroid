package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.AddToCart;
import com.hws.hwsappandroid.model.RefundSubmitModel;
import com.hws.hwsappandroid.model.aoListItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class RequestRefundModel extends ViewModel {
    private final MutableLiveData<String> mResult = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<String> getResult() {
        return mResult;
    }

    public void submit(RefundSubmitModel refundSubmitData) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("detailDescription", refundSubmitData.detailDescription);

                    Gson gson = new Gson();
                    String listString = gson.toJson(
                            refundSubmitData.files,
                            new TypeToken<ArrayList<String>>() {}.getType());
                    JSONArray files =  new JSONArray(listString);
//                    jsonParams.put("files", files);
                    jsonParams.put("files", refundSubmitData.files.get(0));
                    jsonParams.put("goodsId", refundSubmitData.goodsId);
                    jsonParams.put("goodsSpecId", refundSubmitData.goodsSpecId);
                    jsonParams.put("orderId", refundSubmitData.orderId);
                    jsonParams.put("phone", refundSubmitData.phone);
                    jsonParams.put("receivingStatus", ""+ refundSubmitData.receivingStatus);
                    jsonParams.put("refundMoney", refundSubmitData.refundMoney);
                    jsonParams.put("refundReason", ""+ refundSubmitData.refundReason);
                    jsonParams.put("refundType", ""+ refundSubmitData.refundType);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/bizGoodsRefund/submit/refund";
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
