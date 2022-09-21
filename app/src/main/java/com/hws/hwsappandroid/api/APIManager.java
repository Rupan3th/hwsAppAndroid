package com.hws.hwsappandroid.api;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.nio.charset.Charset;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

public class APIManager {
    public static final String Sever_URL = "http://hws.com";
//    private static final String URL = "http://192.168.2.15:8080/hwsAppService/v1";
    private static final String URL = "http://47.108.233.4:9083/hwsAppService/v1";
    public static final String IMAGE_URL = "http://app.automedicalbio.com/assets/img/";

    private static Context mContext;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String authToken = "";

    public static void setContext(Context context) {
        mContext = context;

        client.setTimeout(3000);
        client.setConnectTimeout(3000);
        client.setResponseTimeout(3000);

    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Header[] headers = {
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("Authorization", authToken)
        };
        client.get(mContext, getUrl(url), headers, params, responseHandler);
    }

    public static void post(String url, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler) {
        Header[] headers = {
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("Authorization", authToken)
        };

        client.post(mContext, getUrl(url), headers, params, contentType, responseHandler);
    }

    public static void postJson(String url, JSONObject jsonParams, AsyncHttpResponseHandler responseHandler) {
        Header[] headers = {
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("Content-Type","application/json; charset=utf-8"),
                new BasicHeader("Authorization", authToken)
        };

        StringEntity entity;
        try {
            entity = new StringEntity(jsonParams.toString(), Charset.forName("UTF-8"));
            client.post(mContext, getUrl(url), headers, entity, "application/json", responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postUpload(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader("Authorization", authToken);
        httpClient.post(getUrl(url), params, responseHandler);
    }


    private static String getUrl(String relativeUrl) {
        return URL + relativeUrl;
    }

}
