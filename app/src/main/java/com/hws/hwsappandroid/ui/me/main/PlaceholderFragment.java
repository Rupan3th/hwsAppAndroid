package com.hws.hwsappandroid.ui.me.main;

import static com.hws.hwsappandroid.util.CommonUtils.getDpValue;
import static com.hws.hwsappandroid.util.CommonUtils.getPixelValue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentMyOrderBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.ui.me.MeViewModel;
import com.hws.hwsappandroid.util.CommonUtils;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.MyOrderSectionListAdapter;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;
import com.walnutlabs.android.ProgressHUD;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements DialogInterface.OnCancelListener, ItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private OrderViewModel orderViewModel;
    MeViewModel model;
    private FragmentMyOrderBinding binding;
    public MyOrderSectionListAdapter mAdapter;

    private RecyclerView recommended_products;
    private RecyclerViewAdapter mRecomendAdapter;
    int index;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMyOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ProgressHUD progressDialog = ProgressHUD.show(root.getContext(), "", true, false, PlaceholderFragment.this);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.setIndex(index);
        orderViewModel.loadData();

        RecyclerView myOrderList = binding.myOrderList;
        myOrderList.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mAdapter = new MyOrderSectionListAdapter(root.getContext(), index);
        myOrderList.setAdapter(mAdapter);

        orderViewModel.getMyOrders().observe(getViewLifecycleOwner(), myOrders -> {

            mAdapter.setData(myOrders);

            RelativeLayout none_order = binding.noneOrder;
            if(myOrders.size()>0)
            {
                progressDialog.dismiss();
                none_order.setVisibility(View.GONE);
            }
            else{
                CommonUtils.dismissProgress(progressDialog);
                none_order.setVisibility(View.VISIBLE);
            }

        });

//        EditText edit_text_search = getActivity().findViewById(R.id.edit_text_search);
//        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    String keyword = edit_text_search.getText().toString();
//                    int orderStatus = index;
//
//                    orderViewModel.loadData(keyword, orderStatus);
//                    orderViewModel.getMyOrders().observe(PlaceholderFragment.this, searchOrders -> {
//                        mAdapter.setData(searchOrders);
//                        RelativeLayout none_order = binding.noneOrder;
//                        if(searchOrders.size()>0)   none_order.setVisibility(View.GONE);
//                        else  none_order.setVisibility(View.VISIBLE);
//                    });
//                    return true;
//                }
//                return false;
//            }
//        });

        recommended_products = binding.recommendedProducts;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recommended_products.setLayoutManager(staggeredGridLayoutManager);
        mRecomendAdapter = new RecyclerViewAdapter(getContext(), true, 2);
        mRecomendAdapter.setClickListener(this);
        recommended_products.setAdapter(mRecomendAdapter);

        model = new ViewModelProvider(this).get(MeViewModel.class);
        model.loadData();
        model.getGoods().observe(this, goods -> {
            mRecomendAdapter.setData(goods);
            CommonUtils.dismissProgress(progressDialog);
        });

        binding.mainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {
//                    Log.i(TAG, "Scroll DOWN");
                }
                if (scrollY < oldScrollY) {
//                    Log.i(TAG, "Scroll UP");
                }
                if (scrollY == 0) {
//                    Log.i(TAG, "TOP SCROLL");
                }
                if (scrollY >= 48) {
//                    binding.toolbarHome.setVisibility(View.VISIBLE);
                }
                if (scrollY >= ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 10 )) {
//                    Log.i("Home", ""+scrollY+","+v.getMeasuredHeight()+","+v.getChildAt(0).getMeasuredHeight());
                    model.loadMoreGoods();
                }
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

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mRecomendAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }
}