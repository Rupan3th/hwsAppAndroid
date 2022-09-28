package com.hws.hwsappandroid.ui.classification;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Banner;
import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.model.Category_level_1;
import com.hws.hwsappandroid.model.Category_level_2;
import com.hws.hwsappandroid.model.Category_level_3;
import com.hws.hwsappandroid.model.Children_level_1;
import com.hws.hwsappandroid.model.Children_level_2;
import com.hws.hwsappandroid.model.Good;

import com.hws.hwsappandroid.model.Params;
import com.hws.hwsappandroid.model.SaveOrderShop;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class ClassificationViewModel extends ViewModel {

    private boolean isLoading = false;
    private final MutableLiveData<ArrayList<Category>> mCategoryTree = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Category_level_1>> mCategory_level1 = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Category_level_2>> mCategory_level2 = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Category_level_3>> mCategory_level3 = new MutableLiveData<>();
    private final MutableLiveData<Banner> mBanner = new MutableLiveData<>();

    public LiveData<ArrayList<Category>> getCategoryTree() {  return mCategoryTree;   }
    public LiveData<ArrayList<Category_level_1>> getCategory_level1() {  return mCategory_level1;   }
    public LiveData<ArrayList<Category_level_2>> getCategory_level2() {  return mCategory_level2;   }
    public LiveData<ArrayList<Category_level_3>> getCategory_level3() {  return mCategory_level3;   }
    public LiveData<Banner> getCategoryBanner() { return mBanner; }

    public void loadData() {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                JSONObject jsonParams = new JSONObject();
                String url = "/sysCategory/findTree";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<Category> CategoryArr = new ArrayList<>();
                                for (int i=0; i<list.length(); i++) {
                                    JSONObject CategoryJson = list.getJSONObject(i);
                                    Category category = new Category();
                                    category.sortValue = CategoryJson.optInt("sortValue", 0);
                                    category.gmtModified = CategoryJson.optString("gmtModified", "");
                                    category.pkId = CategoryJson.optString("pkId", "");
                                    category.level = CategoryJson.optInt("level", 0);
                                    category.isDelete = CategoryJson.optInt("isDelete", 0);
                                    category.categoryCode = CategoryJson.optString("categoryCode", "");
                                    category.gmtCreate = CategoryJson.optString("gmtCreate", "");
                                    category.categoryName = CategoryJson.optString("categoryName", "");
                                    category.categoryImg = CategoryJson.optString("categoryImg", "");
                                    category.operatorId = CategoryJson.optString("operatorId", "");
                                    category.parentId = CategoryJson.optInt("parentId", 0);

                                    JSONArray Children = CategoryJson.optJSONArray("children");
                                    if(Children != null){
                                        ArrayList<Children_level_1> ChildrenArr = new ArrayList<>();
                                        for (int j=0; j<Children.length(); j++) {
                                            JSONObject ChildrenJson = Children.getJSONObject(j);
                                            Children_level_1 children = new Children_level_1();
                                            children.sortValue = ChildrenJson.optInt("sortValue", 0);
                                            children.gmtModified = ChildrenJson.optString("gmtModified", "");
                                            children.pkId = ChildrenJson.optString("pkId", "");
                                            children.level = ChildrenJson.optInt("level", 0);
                                            children.isDelete = ChildrenJson.optInt("isDelete", 0);
                                            children.categoryCode = ChildrenJson.optString("categoryCode", "");
                                            children.gmtCreate = ChildrenJson.optString("gmtCreate", "");
                                            children.categoryName = ChildrenJson.optString("categoryName", "");
                                            children.categoryImg = ChildrenJson.optString("categoryImg", "");
                                            children.operatorId = ChildrenJson.optString("operatorId", "");
                                            children.parentId = ChildrenJson.optInt("parentId", 0);

                                            JSONArray SubChildren = ChildrenJson.optJSONArray("children");
                                            ArrayList<Children_level_2> SubChildrenArr = new ArrayList<>();
                                            for (int k=0; k<SubChildren.length(); k++) {
                                                JSONObject SubChildrenJson = SubChildren.getJSONObject(k);
                                                Children_level_2 sub_children = new Children_level_2();
                                                sub_children.sortValue = SubChildrenJson.optInt("sortValue", 0);
                                                sub_children.gmtModified = SubChildrenJson.optString("gmtModified", "");
                                                sub_children.pkId = SubChildrenJson.optString("pkId", "");
                                                sub_children.level = SubChildrenJson.optInt("level", 0);
                                                sub_children.isDelete = SubChildrenJson.optInt("isDelete", 0);
                                                sub_children.categoryCode = SubChildrenJson.optString("categoryCode", "");
                                                sub_children.gmtCreate = SubChildrenJson.optString("gmtCreate", "");
                                                sub_children.categoryName = SubChildrenJson.optString("categoryName", "");
                                                sub_children.categoryImg = SubChildrenJson.optString("categoryImg", "");
                                                sub_children.operatorId = SubChildrenJson.optString("operatorId", "");
                                                sub_children.parentId = SubChildrenJson.optInt("parentId", 0);
                                                SubChildrenArr.add(sub_children);
                                            }
                                            children.childrenList = SubChildrenArr;
                                            ChildrenArr.add(children);
                                        }
                                        category.childrenList = ChildrenArr;
                                        CategoryArr.add(category);
                                    }

                                }

                                mCategoryTree.postValue(CategoryArr);

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

    public void loadCategory_l1(int level) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("level", level);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/sysCategory/findTree";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<Category_level_1> categoryList_level1 = new ArrayList<>();
                                for(int i=0; i<list.length(); i++){
                                    JSONObject json = list.getJSONObject(i);
                                    Category_level_1 CategoryL1 = new Category_level_1();
                                    CategoryL1.gmtModified = json.optString("gmtModified", "");
                                    CategoryL1.pkId = json.optString("pkId", "");
                                    CategoryL1.level = json.optInt("level", 1);
                                    CategoryL1.isDelete = json.optInt("isDelete", 0);
                                    CategoryL1.categoryCode = json.optString("categoryCode", "");
                                    CategoryL1.gmtCreate = json.optString("gmtCreate", "");
                                    CategoryL1.categoryImg = json.optString("categoryImg", "");
                                    CategoryL1.categoryName = json.optString("categoryName", "");
                                    CategoryL1.parentId = json.optString("parentId", "0");
                                    CategoryL1.isAppIco = json.optBoolean("isAppIco", false);
                                    CategoryL1.appIcoSort = json.optString("appIcoSort", "");
                                    CategoryL1.sortValue = json.optInt("sortValue", 1);
                                    CategoryL1.operatorId = json.optString("operatorId", "");

                                    categoryList_level1.add(CategoryL1);
                                }
                                mCategory_level1.postValue(categoryList_level1);

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

    public void loadCategory_l2(int level, String parentId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("level", level);
                    jsonParams.put("parentId", parentId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/sysCategory/findTree";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<Category_level_2> categoryList_level2 = new ArrayList<>();
                                for(int i=0; i<list.length(); i++){
                                    JSONObject json = list.getJSONObject(i);
                                    Category_level_2 CategoryL2 = new Category_level_2();
                                    CategoryL2.pkId = json.optString("pkId", "");
                                    CategoryL2.level = json.optInt("level", 1);
                                    CategoryL2.isDelete = json.optInt("isDelete", 0);
                                    CategoryL2.categoryCode = json.optString("categoryCode", "");
                                    CategoryL2.gmtCreate = json.optString("gmtCreate", "");
//                                    CategoryL2.categoryImg = json.optString("categoryImg", "");
                                    CategoryL2.categoryName = json.optString("categoryName", "");
                                    CategoryL2.parentId = json.optString("parentId", "0");
                                    CategoryL2.isAppIco = json.optBoolean("isAppIco", false);
                                    CategoryL2.sortValue = json.optInt("sortValue", 1);
                                    CategoryL2.operatorId = json.optString("operatorId", "");

                                    categoryList_level2.add(CategoryL2);
                                }
                                mCategory_level2.postValue(categoryList_level2);

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

    public void loadCategory_l3(int level, String parentId) {
        if (isLoading) return;
        isLoading = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("level", level);
                    jsonParams.put("parentId", parentId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = "/sysCategory/findTree";
                APIManager.postJson(url, jsonParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                ArrayList<Category_level_3> categoryList_level3 = new ArrayList<>();
                                for(int i=0; i<list.length(); i++){
                                    JSONObject json = list.getJSONObject(i);
                                    Category_level_3 CategoryL3 = new Category_level_3();
                                    CategoryL3.pkId = json.optString("pkId", "");
                                    CategoryL3.level = json.optInt("level", 1);
                                    CategoryL3.isDelete = json.optInt("isDelete", 0);
                                    CategoryL3.categoryCode = json.optString("categoryCode", "");
                                    CategoryL3.gmtCreate = json.optString("gmtCreate", "");
//                                    CategoryL2.categoryImg = json.optString("categoryImg", "");
                                    CategoryL3.categoryName = json.optString("categoryName", "");
                                    CategoryL3.parentId = json.optString("parentId", "0");
                                    CategoryL3.isAppIco = json.optBoolean("isAppIco", false);
                                    CategoryL3.sortValue = json.optInt("sortValue", 1);
                                    CategoryL3.operatorId = json.optString("operatorId", "");

                                    categoryList_level3.add(CategoryL3);
                                }
                                mCategory_level3.postValue(categoryList_level3);

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

    public void loadCategoryBanner(String category1Id) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("category1Id", category1Id);
                String url = "/appBanner/queryCategoryBanner";
                APIManager.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject obj = response.getJSONObject("data");
                                JSONArray list = obj.getJSONArray("list");
                                if (list.length() > 0) {
                                    obj = list.getJSONObject(0);
                                    Banner banner = new Banner();

                                    banner.bannerPic = obj.optString("bannerPic", "");
                                    banner.category1Id = obj.optString("category1Id", "");
                                    banner.pkId = obj.optString("pkId", "");
                                    banner.bannerType = obj.optInt("bannerType", 0);
                                    banner.enableStatus = obj.optInt("enableStatus", 0);
                                    banner.gotoContent = obj.optString("gotoContent", "");
                                    banner.gotoType = obj.optInt("gotoType", 0);

                                    mBanner.postValue(banner);
                                } else {
                                    mBanner.postValue(null);
                                }

                            } else {
                                Log.d("Get category banner", response.toString());
                                mBanner.postValue(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mBanner.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Get category banner", ""+statusCode);
//                        progressDialog.dismiss();
//                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        mBanner.postValue(null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("Get category banner", responseString);
//                        progressDialog.dismiss();
                        mBanner.postValue(null);
                    }
                });
            }
        });
    }
}