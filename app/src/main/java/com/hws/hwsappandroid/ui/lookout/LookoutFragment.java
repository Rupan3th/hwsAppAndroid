package com.hws.hwsappandroid.ui.lookout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentLookoutBinding;
import com.hws.hwsappandroid.ui.ImageDetailActivity;
import com.hws.hwsappandroid.model.CourseModel;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;


public class LookoutFragment extends Fragment implements ItemClickListener {

    private FragmentLookoutBinding binding;
    View view;
    RecyclerView recyclerView;
    TextView toolbar;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<CourseModel> courseModelArrayList = new ArrayList<CourseModel>();
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + "USBCamera/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_lookout, container, false);

        binding = FragmentLookoutBinding.inflate(inflater, container, false);
        toolbar = (TextView) view.findViewById(R.id.toolbar_outlook);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        CreateModelArrayList();

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new RecyclerViewAdapter(view.getContext(), courseModelArrayList, false);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setClickListener(this);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(-1)) {
                    toolbar.setVisibility(View.INVISIBLE);
                } else if (!recyclerView.canScrollVertically(1)) {

                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

//        LookoutViewModel lookoutViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(LookoutViewModel.class);
//        View root = binding.getRoot();
//        final TextView textView = binding.txtLookout;
//        lookoutViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return view;
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailImage = new Intent(getActivity(), ImageDetailActivity.class);

        String imageName = courseModelArrayList.get(position-1).getCourse_name();
        String productInfo = courseModelArrayList.get(position-1).getProductInfo();
        String price = courseModelArrayList.get(position-1).getPrice();

        detailImage.putExtra("imageName", imageName);

        startActivity(detailImage);

    }

    void setAppBarLayoutOffset() {
        float dip = 0 ;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        ObjectAnimator animator = ObjectAnimator.ofFloat(toolbar, "translationY", px);
        animator.setDuration(100);
        animator.start();
    }

    private void CreateModelArrayList(){
        courseModelArrayList.clear();
        File dir_image = new File(path + "images");
        if(dir_image.exists()) {
            try{
                for (int i = 0; i < dir_image.listFiles().length; i++) {
                    String image_name = path + "images/" + dir_image.listFiles()[i].getName();
                    Bitmap bitmap = BitmapFactory.decodeFile(image_name);
                    courseModelArrayList.add(new CourseModel(image_name, bitmap, "拓普特轮胎 防滑 冬季", "￥489.00"));
                }
            }catch (Exception e){

            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}