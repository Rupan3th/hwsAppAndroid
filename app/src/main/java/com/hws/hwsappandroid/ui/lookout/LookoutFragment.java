package com.hws.hwsappandroid.ui.lookout;

import android.content.Intent;
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
        model.loadData();
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
        Good productInfo = mAdapter.getGoodInfo(position);
        String good_name = productInfo.goodsName;
        String good_sn = productInfo.goodsSn;
//        String imageName = courseModelArrayList.get(position-1).getCourse_name();
//        String productInfo = courseModelArrayList.get(position-1).getProductInfo();
//        String price = courseModelArrayList.get(position-1).getPrice();

//        detailImage.putExtra("imageName", imageName);

        startActivity(detailProduct);

    }

//    void setAppBarLayoutOffset() {
//        float dip = 0 ;
//        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
//        ObjectAnimator animator = ObjectAnimator.ofFloat(toolbar, "translationY", px);
//        animator.setDuration(100);
//        animator.start();
//    }

//    private void CreateModelArrayList(){
//        courseModelArrayList.clear();
//        File dir_image = new File(path + "images");
//        if(dir_image.exists()) {
//            try{
//                for (int i = 0; i < dir_image.listFiles().length; i++) {
//                    String image_name = path + "images/" + dir_image.listFiles()[i].getName();
//                    Bitmap bitmap = BitmapFactory.decodeFile(image_name);
//                    courseModelArrayList.add(new CourseModel(image_name, bitmap, "拓普特轮胎 防滑 冬季", "￥489.00"));
//                }
//            }catch (Exception e){
//
//            }
//
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}