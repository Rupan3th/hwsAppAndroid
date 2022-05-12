package com.hws.hwsappandroid.ui.classification;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.model.Children_level_1;
import com.hws.hwsappandroid.model.Children_level_2;
import com.hws.hwsappandroid.model.Good;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ClassificationViewModel extends ViewModel {

    private boolean isLoading = false;
    private final MutableLiveData<ArrayList<Category>> mCategoryTree = new MutableLiveData<>();

    public LiveData<ArrayList<Category>> getCategoryTree() {  return mCategoryTree;   }

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
                                obj = list.getJSONObject(0);

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

                                    JSONArray Children = CategoryJson.getJSONArray("children");
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

                                        JSONArray SubChildren = ChildrenJson.getJSONArray("children");
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
}