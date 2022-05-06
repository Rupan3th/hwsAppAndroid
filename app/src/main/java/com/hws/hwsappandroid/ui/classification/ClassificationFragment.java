package com.hws.hwsappandroid.ui.classification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.SearchActivity;
import com.hws.hwsappandroid.databinding.FragmentClassificationBinding;

public class ClassificationFragment extends Fragment {

    private FragmentClassificationBinding binding;
    TabsAdapter tabAdapter;
    ViewPagerAdapter pagerAdapter;
    LinearLayout search_bar;
    TextView search_bar_txt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_classification, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

//        ClassificationViewModel classificationViewModel =
//                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ClassificationViewModel.class);
//
//        binding = FragmentClassificationBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();

//        final TextView textView = binding.txtClassification;
//        classificationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        search_bar = view.findViewById(R.id.search_bar);
        search_bar_txt = view.findViewById(R.id.editTextTextPersonName);
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
            }
        });
        search_bar_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
            }
        });

        /**製作陣列*/
        String[] strings = {"衣服","日用","食品","玩具"};
        /**設置ViewPager*/
        final VerticalViewPager viewPager = view.findViewById(R.id.pager);
        /**綁定ViewPager給ViewPagerAdapter*/
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),strings);
        viewPager.setAdapter(pagerAdapter);
        /**監聽ViewPager被滑動時的事件處理*/
        viewPager.addOnPageChangeListener(onPageChangeListener);

        /**設置RecyclerView*/
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tabAdapter = new TabsAdapter(strings,recyclerView);
        recyclerView.setAdapter(tabAdapter);
        /**點擊RecyclerView的Tab時將ViewPager滾動至該頁*/
        tabAdapter.onTabClick = new TabsAdapter.OnTabClick() {
            @Override
            public void onTabClick(int position) {
                viewPager.setCurrentItem(position,true);
            }
        };

        return view;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener= new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        /**抓取到被滑動到的那一頁*/
        @Override
        public void onPageSelected(int position) {
            tabAdapter.setCurrentPage(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}