package com.hws.hwsappandroid.ui;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.shippingAdr;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class EditShippingAddressModel extends ViewModel {

    private final MutableLiveData<ArrayList<shippingAdr>> mShippingAddress = new MutableLiveData<>();
    private final MutableLiveData<String> mResult = new MutableLiveData<>();
    private final MutableLiveData<String> setResult = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ArrayList<shippingAdr>> getAddress() {
        return mShippingAddress;
    }
    public LiveData<String> getResult() {
        return mResult;
    }
    public LiveData<String> getSetResult() {
        return setResult;
    }

    public void loadData() {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                String url = "/appUserAddress/queryAllAddress";
//                params.put("goodsId", goodsId);
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray AddressJson = obj.getJSONArray("list");
                                ArrayList<shippingAdr> addressArr = new ArrayList<>();
                                for (int i=0; i<AddressJson.length(); i++) {
                                    JSONObject json = AddressJson.getJSONObject(i);
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
                                    addressArr.add(shippingAddress);
                                }
                                mShippingAddress.postValue(addressArr);

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

    public void updateData(shippingAdr Adr) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("address", Adr.address);
                    jsonParams.put("addressDefault", Adr.addressDefault);
                    jsonParams.put("city", Adr.city);
                    jsonParams.put("consignee", Adr.consignee);
                    jsonParams.put("consigneeXb", Adr.consigneeXb);
                    jsonParams.put("country", Adr.country);
                    jsonParams.put("district", Adr.district);
                    jsonParams.put("phone", Adr.phone);
                    jsonParams.put("province", Adr.province);
                    jsonParams.put("pkId", Adr.pkId);
                    jsonParams.put("userId", Adr.userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserAddress/update";
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

    public void saveData(shippingAdr Adr) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("address", Adr.address);
                    jsonParams.put("addressDefault", Adr.addressDefault);
                    jsonParams.put("city", Adr.city);
                    jsonParams.put("consignee", Adr.consignee);
                    jsonParams.put("consigneeXb", Adr.consigneeXb);
                    jsonParams.put("country", Adr.country);
                    jsonParams.put("district", Adr.district);
//                    jsonParams.put("gmtCreate", Adr.gmtCreate);
//                    jsonParams.put("gmtModified", Adr.gmtModified);
//                    jsonParams.put("operatorId", Adr.operatorId);
                    jsonParams.put("phone", Adr.phone);
//                    jsonParams.put("pkId", Adr.pkId);
                    jsonParams.put("province", Adr.province);
                    jsonParams.put("userId", Adr.userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/appUserAddress/save";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                String pkId = response.optString("data", "");
                                mResult.postValue(pkId);

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

    public void setDefaultAddress(String pkId) {
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();

                String url = "/appUserAddress/setDefaultAddress/" + pkId;
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {

                                setResult.postValue("default");
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

    public void delAddress(String pkId) {
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();

                String url = "/appUserAddress/deleteById/" + pkId;
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
