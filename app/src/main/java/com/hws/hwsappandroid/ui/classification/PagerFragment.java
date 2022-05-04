package com.hws.hwsappandroid.ui.classification;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.api.APIManager;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.model.SectionModel;
import com.hws.hwsappandroid.util.SectionRecyclerViewAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PagerFragment extends Fragment {

    private int pagerId = 0;
    private String info = "";
    private static final String KEY_PAGE_ID = "key_page_id";
    private static final String KEY_INFO = "key_Info";

    private RecyclerViewType recyclerViewType = RecyclerViewType.GRID;
    private RecyclerView recyclerView;

    private View view;
    private Context mContext;
    private Resources res;

    /**設置初始化接口，並將指定要載入的資料存起來*/
    public static PagerFragment newInstance(String s, int pageId){
        /**以Bundle存放要被載入的資料，稍後再onCreate中取出*/
        Bundle args = new Bundle();
        args.putString(KEY_INFO,s);
        args.putInt(KEY_PAGE_ID,pageId);
        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        res = mContext.getResources();
        super.onCreate(savedInstanceState);
        /**取出儲存的資料*/
        Bundle bundle = getArguments();
        pagerId = bundle.getInt(KEY_PAGE_ID);
        info = bundle.getString(KEY_INFO);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**找到View, 並設置裡面的UI要做的事*/
        view = inflater.inflate(R.layout.page_item,container,false);
//        TextView tvTag = view.findViewById(R.id.image_home_category);
//        TextView tvInfo = view.findViewById(R.id.text_home_category);
//
//        tvTag.setText("第"+(pagerId+1)+"頁");
//        tvInfo.setText(info);

        getClassificationList();
        ////
        setUpRecyclerView();
        populateRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
//        recyclerView = binding.sectionedRecyclerView;
        recyclerView = (RecyclerView) view.findViewById(R.id.sectioned_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void populateRecyclerView() {
        ArrayList<SectionModel> sectionModelArrayList = new ArrayList<>();
        //for loop for sections
        for (int i = 1; i <= 5; i++) {
            ArrayList<String> itemArrayList = new ArrayList<>();
            //for loop for items
            for (int j = 1; j <= 10; j++) {
                itemArrayList.add("Item " + j);
            }

            //add the section and items to array list
            sectionModelArrayList.add(new SectionModel("Section " + i, itemArrayList));
        }

        SectionRecyclerViewAdapter adapter = new SectionRecyclerViewAdapter(getContext(), recyclerViewType, sectionModelArrayList);
        recyclerView.setAdapter(adapter);
    }

    private void getClassificationList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                final ProgressHUD progressDialog = ProgressHUD.show(mContext, res.getString(R.string.processing), true, false, DoctorListFragment.this);

                RequestParams params = new RequestParams();
                params.put("pagerId", "");
                params.put("info", 0);

                String url = "getCategory";
                APIManager.post(url, params, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getInt("code") == 0) {

                            } else {
                                JSONArray list = response.getJSONArray("list");
                                for(int i=0; i<list.length(); i++) {
                                    JSONObject item = list.getJSONObject(i);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        progressDialog.dismiss();
                        Toast.makeText(mContext, res.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        progressDialog.dismiss();
                        Toast.makeText(mContext, res.getString(R.string.error_db), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 500);
    }
}
