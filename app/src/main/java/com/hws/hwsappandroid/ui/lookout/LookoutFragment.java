package com.hws.hwsappandroid.ui.lookout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hws.hwsappandroid.databinding.FragmentLookoutBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.ui.ImageDetailActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

public class LookoutFragment extends Fragment implements ItemClickListener {

    private FragmentLookoutBinding binding;
    View view;
    RecyclerView recyclerView;
    TextView toolbar;
    private RecyclerViewAdapter mAdapter;

//    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View decorView = requireActivity().getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        view = inflater.inflate(R.layout.fragment_lookout, container, false);

        binding = FragmentLookoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LookoutViewModel model = new ViewModelProvider(this).get(LookoutViewModel.class);

//        toolbar = (TextView) view.findViewById(R.id.toolbar_outlook);
        toolbar = binding.toolbarOutlook;

        recyclerView = binding.recyclerView;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), false, 0);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setClickListener(this);
        if(NetworkCheck())  model.loadData();
        model.getGoods().observe(this, goods -> {
            mAdapter.setData(goods);
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
                    toolbar.setVisibility(View.INVISIBLE);
                } else if (!recyclerView.canScrollVertically(1)) {
                    model.loadMoreGoods();
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

//        return view;
        return root;
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position-1);
        String pkId = productInfo.pkId;
        String good_price = productInfo.price;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void NoInternet() {
        binding.noInternet.setVisibility(View.VISIBLE);

        binding.recyclerView.setVisibility(View.GONE);
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