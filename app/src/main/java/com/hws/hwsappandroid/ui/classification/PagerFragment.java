package com.hws.hwsappandroid.ui.classification;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.model.Children_level_1;
import com.hws.hwsappandroid.model.RecyclerViewType;
import com.hws.hwsappandroid.util.SectionRecyclerViewAdapter;

import java.util.ArrayList;

public class PagerFragment extends Fragment {

    private int pagerId = 0;
    private Category mcategory;

    private static final String KEY_PAGE_ID = "key_page_id";

    private RecyclerViewType recyclerViewType = RecyclerViewType.GRID;
    private RecyclerView recyclerView;

    private View view;
    private Context mContext;
    private Resources res;

    /**設置初始化接口，並將指定要載入的資料存起來*/
    public static PagerFragment newInstance(String s, Category category, int pageId){
        /**以Bundle存放要被載入的資料，稍後再onCreate中取出*/
        Bundle args = new Bundle();
        args.putParcelable("category", category);
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
        mcategory = bundle.getParcelable("category");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**找到View, 並設置裡面的UI要做的事*/
        view = inflater.inflate(R.layout.page_item,container,false);

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
        ArrayList<Children_level_1> childrenLvl1 =  mcategory.childrenList;

        SectionRecyclerViewAdapter adapter = new SectionRecyclerViewAdapter(getContext(), recyclerViewType, childrenLvl1);
        recyclerView.setAdapter(adapter);
    }

}
