package com.hws.hwsappandroid.ui.classification;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentClassificationBinding;
import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.model.Category_level_1;
import com.hws.hwsappandroid.model.Category_level_2;
import com.hws.hwsappandroid.model.Category_level_3;
import com.hws.hwsappandroid.model.Children_level_1;
import com.hws.hwsappandroid.model.Children_level_2;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.SearchActivity;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.MyGlobals;
import com.hws.hwsappandroid.util.SectionRecyclerViewAdapter;
import com.squareup.picasso.Picasso;
import com.walnutlabs.android.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClassificationFragment extends Fragment implements DialogInterface.OnCancelListener{

    private FragmentClassificationBinding binding;
    TabsAdapter tabAdapter;
    public SectionRecyclerViewAdapter adapter;
    ViewPagerAdapter pagerAdapter;
    LinearLayout search_bar;
    TextView search_bar_txt;
    RecyclerView recyclerView, sectionRecycler;
    NestedScrollView mainScrollView;
    private RecyclerViewType recyclerViewType = RecyclerViewType.GRID;
    ClassificationViewModel Model;
    String listString = "";
    SharedPreferences pref;

    ArrayList<Category> mCategory = new ArrayList<>();

    ArrayList<Category_level_1> Category_level1 = new ArrayList<>();
    ArrayList<Category_level_2> Category_level2 = new ArrayList<>();
    ArrayList<Category_level_3> Category_level3 = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        View view = inflater.inflate(R.layout.fragment_classification, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        binding = FragmentClassificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Model = new ViewModelProvider(this).get(ClassificationViewModel.class);
        if(NetworkCheck())  Model.loadData();

//        final ProgressHUD progressDialog = ProgressHUD.show(root.getContext(), "", true, false, ClassificationFragment.this);
//        final ProgressHUD progressDialog = ProgressHUD.showMulti(view.getContext(), "添加购物车成功", "在购物车等你哦~", true, true, ClassificationFragment.this);

        String category_name = "";
        if (getArguments() != null)
        {
            category_name = getArguments().getString("categoryName");
        }

        mainScrollView = binding.mainScrollView;
        search_bar = binding.searchBar;
        search_bar_txt = binding.editTextTextPersonName;
        search_bar_txt.setText(category_name);
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ChooseCategoryActivity.class);
                startActivity(i);
            }
        });
        search_bar_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ChooseCategoryActivity.class);
                startActivity(i);
            }
        });

        pref = getActivity().getSharedPreferences("user_info",MODE_PRIVATE);
        listString = pref.getString("categoryList","");
        if(!listString.equals("")){
            StringToArrayList(listString);
        }

        adapter = new SectionRecyclerViewAdapter(view.getContext(), recyclerViewType);

        recyclerView = binding.recyclerView;
        tabAdapter = new TabsAdapter(recyclerView);
        tabAdapter.onTabClick = new TabsAdapter.OnTabClick() {
            @Override
            public void onTabClick(int position) {
                tabAdapter.setCurrentPage(position);

                adapter.setData(mCategory.get(position).childrenList);
                MyGlobals.getInstance().setSelect_CategoryIdx(position);
                Model.loadCategoryBanner(mCategory.get(position).pkId);

                sectionRecycler.post(() -> {
                    mainScrollView.scrollTo(0,0);
                });

//                Model.loadCategory_l2(2, Category_level1.get(position).pkId);
//                Model.getCategory_level2().observe(ClassificationFragment.this, category_level2 -> {
//
//        //            if(mCategoryTree.size()>0) progressDialog.dismiss();
//        //            else CommonUtils.dismissProgress(progressDialog);
////                    CommonUtils.dismissProgress(progressDialog);
//                    adapter.setData(category_level2);
//                });

            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(tabAdapter);

        if(!listString.equals("")){
            tabAdapter.setData(mCategory);

            sectionRecycler = binding.sectionedRecyclerView;
            sectionRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
            sectionRecycler.setLayoutManager(linearLayoutManager);
            sectionRecycler.setAdapter(adapter);

            if(MyGlobals.getInstance().getSelect_CategoryName() != ""){
                for(int i=0; i<mCategory.size(); i++){
                    if(MyGlobals.getInstance().getSelect_CategoryName().equals("all") || MyGlobals.getInstance().getSelect_CategoryName().equals("首页") )
                        MyGlobals.getInstance().setSelect_CategoryIdx(0);
                    if(mCategory.get(i).categoryName.equals(MyGlobals.getInstance().getSelect_CategoryName())){
                        MyGlobals.getInstance().setSelect_CategoryIdx(i);
                    }
                }
            }
            tabAdapter.setCurrentPage(MyGlobals.getInstance().getSelect_CategoryIdx());
            recyclerView.smoothScrollToPosition(MyGlobals.getInstance().getSelect_CategoryIdx());
            adapter.setData(mCategory.get(MyGlobals.getInstance().getSelect_CategoryIdx()).childrenList);
            Model.loadCategoryBanner(mCategory.get(MyGlobals.getInstance().getSelect_CategoryIdx()).pkId);
        }

//        final ProgressHUD progressDialog = ProgressHUD.show(root.getContext(), "", true, false, ClassificationFragment.this);

        Model.getCategoryTree().observe(this, categoryTree -> {
//            if(mCategoryTree.size()>0) progressDialog.dismiss();
//            else CommonUtils.dismissProgress(progressDialog);
//            CommonUtils.dismissProgress(progressDialog);

            Gson gson = new Gson();
            listString = gson.toJson(
                    categoryTree,
                    new TypeToken<ArrayList<Category>>() {}.getType());
            pref = getActivity().getSharedPreferences("user_info",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("categoryList", listString);
            editor.commit();

            mCategory = categoryTree;
            tabAdapter.setData(categoryTree);

            sectionRecycler = binding.sectionedRecyclerView;
            sectionRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
            sectionRecycler.setLayoutManager(linearLayoutManager);
            sectionRecycler.setAdapter(adapter);

            if(MyGlobals.getInstance().getSelect_CategoryName() != ""){
                for(int i=0; i<mCategory.size(); i++){
                    if(MyGlobals.getInstance().getSelect_CategoryName().equals("all") || MyGlobals.getInstance().getSelect_CategoryName().equals("首页") )
                        MyGlobals.getInstance().setSelect_CategoryIdx(0);
                    if(mCategory.get(i).categoryName.equals(MyGlobals.getInstance().getSelect_CategoryName())){
                        MyGlobals.getInstance().setSelect_CategoryIdx(i);
                    }
                }
            }
            tabAdapter.setCurrentPage(MyGlobals.getInstance().getSelect_CategoryIdx());
            recyclerView.smoothScrollToPosition(MyGlobals.getInstance().getSelect_CategoryIdx());
            adapter.setData(categoryTree.get(MyGlobals.getInstance().getSelect_CategoryIdx()).childrenList);
            Model.loadCategoryBanner(categoryTree.get(MyGlobals.getInstance().getSelect_CategoryIdx()).pkId);

//            CommonUtils.dismissProgress(progressDialog);

//            Category_level1 = category_level1;
//            Model.loadCategory_l2(2, Category_level1.get(0).pkId);
//            Model.getCategory_level2().observe(this, category_level2 -> {
//                Category_level2 = category_level2;
//
//                for(int i=0; i<category_level2.size(); i++){
//                    String pkId = Category_level2.get(i).pkId;
//                    Model.loadCategory_l3(3, pkId);
//                    int po = i;
//                    Model.getCategory_level3().observe(this, category_level3 -> {
//                        Category_level2.get(po).category_level3 = category_level3;
//                    });
//                }
//
//
//                CommonUtils.dismissProgress(progressDialog);
//                adapter.setData(Category_level2);
//            });
        });

        ImageView bannerView = binding.categoryBanner;
        Model.getCategoryBanner().observe(getViewLifecycleOwner(), banner -> {
            if (banner == null || banner.enableStatus == 0) {
                bannerView.setVisibility(View.GONE);
            } else {
                try{
                    Picasso.get()
                            .load(banner.bannerPic)
//                    .placeholder(R.drawable.cart)
                            .fit()
                            .centerCrop()
                            .into(bannerView);
                }
                catch (Exception e) {}
                bannerView.setVisibility(View.VISIBLE);
                bannerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (URLUtil.isValidUrl(banner.gotoContent)) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.gotoContent));
                            startActivity(browserIntent);
                        }
//                                Intent pd = new Intent(getActivity(), ProductDetailActivity.class);
//                                String pkId = banners.get(position).pkId;
//                                pd.putExtra("pkId", pkId);
//                                startActivity(pd);
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public void StringToArrayList(String categoryList){
        try {
            JSONArray list =  new JSONArray(categoryList);
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

                JSONArray Children = CategoryJson.getJSONArray("childrenList");
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

                    JSONArray SubChildren = ChildrenJson.getJSONArray("childrenList");
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

            mCategory = CategoryArr;

        }catch (Exception e){}

    }

    public void NoInternet(){
        binding.noInternet.setVisibility(View.VISIBLE);

        binding.toolBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.mainScrollView.setVisibility(View.GONE);

        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public boolean NetworkCheck(){
        boolean isConnected = isNetworkConnected(getContext());
        if (!isConnected)
        {
            NoInternet();
        }
        return isConnected;
    }

    public boolean isNetworkConnected(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean bwimax = false;
        if (wimax != null)
        {
            bwimax = wimax.isConnected();
        }
        if (mobile != null)
        {
            if (mobile.isConnected() || wifi.isConnected() || bwimax)
            {
                return true;
            }
        }
        else
        {
            if (wifi.isConnected() || bwimax)
            {
                return true;
            }
        }
        return false;
    }
}