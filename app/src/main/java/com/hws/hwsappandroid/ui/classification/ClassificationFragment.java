package com.hws.hwsappandroid.ui.classification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.hws.hwsappandroid.databinding.FragmentClassificationBinding;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.SearchActivity;
import com.hws.hwsappandroid.ui.home.HomeViewModel;

public class ClassificationFragment extends Fragment {

    private FragmentClassificationBinding binding;
    TabsAdapter tabAdapter;
    ViewPagerAdapter pagerAdapter;
    LinearLayout search_bar;
    TextView search_bar_txt;
    RecyclerView recyclerView;
    String[] strings = {"衣服","日用","食品","玩具"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_classification, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        ClassificationViewModel Model = new ViewModelProvider(this).get(ClassificationViewModel.class);
        Model.loadData();

        binding = FragmentClassificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String category_name = "";
        if (getArguments() != null)
        {
            category_name = getArguments().getString("categoryName");
        }

        search_bar = binding.searchBar;
        search_bar_txt = binding.editTextTextPersonName;
        search_bar_txt.setText(category_name);
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

        VerticalViewPager viewPager = binding.pager;
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        recyclerView = binding.recyclerView;
        tabAdapter = new TabsAdapter(recyclerView);
        tabAdapter.onTabClick = new TabsAdapter.OnTabClick() {
            @Override
            public void onTabClick(int position) {
                viewPager.setCurrentItem(position,true);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(tabAdapter);

        Model.getCategoryTree().observe(this, mCategoryTree -> {
            pagerAdapter.updateData(mCategoryTree);
            tabAdapter.setData(mCategoryTree);
        });

        return root;
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