package com.hws.hwsappandroid.ui.home;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.databinding.FragmentSearchBinding;
import com.hws.hwsappandroid.model.Good;
import com.hws.hwsappandroid.model.GoodClass;
import com.hws.hwsappandroid.model.LiveChatContents;
import com.hws.hwsappandroid.ui.ChooseCategoryActivity;
import com.hws.hwsappandroid.ui.ProductDetailActivity;
import com.hws.hwsappandroid.ui.SearchActivity;
import com.hws.hwsappandroid.util.DbOpenHelper;
import com.hws.hwsappandroid.util.FlowLayout;
import com.hws.hwsappandroid.util.ItemClickListener;
import com.hws.hwsappandroid.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class SearchFragment extends Fragment implements ItemClickListener{
    private DbOpenHelper mDbOpenHelper;

    String histories = "'털보네이터' 제임스 하든이 한 단계 더 진화했다. " +
            "31경기 연속 30+득점(역대 2위), 경기당 평균 36.0득점(역대 7위) 등 ";
    String[] search_histories = histories.split(" ");
    ArrayList<String> search_history = new ArrayList<>();
    String edit_key_word;

    private FragmentSearchBinding binding;

    RecyclerView recyclerView;
    ImageView not_found;
    LinearLayout searchbar_home, keyword_area;
    EditText edit_text_home_collapsed;
    TextView keyword;
    private RecyclerViewAdapter mAdapter;
    ArrayList<Good> searchGoods = new ArrayList<>();
    LinearLayout rootView;
    HomeViewModel model;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        searchbar_home = binding.searchbarHome;

        ConstraintLayout search_layout = binding.searchLayout;
        search_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mDbOpenHelper = new DbOpenHelper(getContext());
        mDbOpenHelper.open();
        mDbOpenHelper.create();
//        mDbOpenHelper.insertSearchHistory("降温冰丝棉");
//        mDbOpenHelper.insertSearchHistory("英爵伦");
//        mDbOpenHelper.insertSearchHistory("短袖T恤男2022新款夏季");
//        mDbOpenHelper.insertSearchHistory("字母印花丝光棉体恤潮");
//        mDbOpenHelper.insertSearchHistory("轮胎");
//        mDbOpenHelper.insertSearchHistory("橡胶");
//        mDbOpenHelper.insertSearchHistory("款式细节");
        getDatabase();

        ImageButton btnBack = binding.buttonBack;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                View focusedView = getActivity().getCurrentFocus();
                if (focusedView != null) {
                    manager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

//                requireActivity().getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
            }
        });

        LinearLayout history_ctr = binding.historyCtr;
        FlowLayout search_history_list = binding.searchHistoryList;

        keyword_area = binding.keywordArea;
        keyword = binding.keyword;
        edit_text_home_collapsed = binding.editTextHomeCollapsed;
        not_found = binding.notFound;

        recyclerView = binding.recyclerView;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), true, 0);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        Button closeKeyword = binding.closeKeyword;
        closeKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.setText("");

                history_ctr.setVisibility(View.VISIBLE);
                search_history_list.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);
            }
        });

        Button btnCancel = binding.buttonSearchCancel;
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.setText("");

                history_ctr.setVisibility(View.VISIBLE);
                search_history_list.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);

//                requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);
//                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
            }
        });

        ArrayList<TextView> search_history_items = new ArrayList<TextView>();
//        for (String s : search_histories) {
        for (int i=0; i<search_history.size(); i++) {
            String s = search_history.get(i);
            TextView tv = new TextView(getContext());
            tv.setText(s);
            tv.setBackgroundResource(R.drawable.round_white_gray_solid_36);
            tv.setTextColor(Color.parseColor("#C9CDD4"));
            tv.setTextSize(12);
            tv.setIncludeFontPadding(false);
            tv.setPadding(50, 16, 50, 16);

            FlowLayout.MarginLayoutParams margin_params = new FlowLayout.MarginLayoutParams(
                    FlowLayout.MarginLayoutParams.WRAP_CONTENT, FlowLayout.MarginLayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(margin_params);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    edit_text_home_collapsed.setText(s);
                    edit_text_home_collapsed.setVisibility(View.INVISIBLE);
                    edit_key_word = s;
                    keyword.setText(s);
                    keyword_area.setVisibility(View.VISIBLE);

                    history_ctr.setVisibility(View.GONE);
                    search_history_list.setVisibility(View.GONE);

                    ///Search result Display
                    model.loadSearchGoods(s);
                    model.getGoods().observe(getViewLifecycleOwner(), goods -> {
                        if(goods.size() > 0){
                            mAdapter.setData(goods);
                            recyclerView.setVisibility(View.VISIBLE);
                            not_found.setVisibility(View.GONE);
                        }
                        else{
                            recyclerView.setVisibility(View.GONE);
                            not_found.setVisibility(View.VISIBLE);
                        }
                    });

                    mDbOpenHelper.deleteColumnSearchHistory(s);
                    mDbOpenHelper.insertSearchHistory(s);
                }
            });

            search_history_list.addView(tv);
            search_history_items.add(tv);
        }

        MaterialButton btn_clear_history = binding.buttonClearHistory;
        btn_clear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_history_list.removeAllViews();
                search_history.clear();
                mDbOpenHelper.deleteAllSearchHistory();
            }
        });


        ImageButton button_search = binding.buttonSearch;
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });

        edit_text_home_collapsed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //Perform your Actions here.
                    doSearch();
                    handled = true;
                }
                return handled;
            }
        });

        searchbar_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_home_collapsed.setVisibility(View.VISIBLE);
                keyword_area.setVisibility(View.GONE);
                edit_text_home_collapsed.requestFocus();
                edit_text_home_collapsed.setText(edit_key_word);
                edit_text_home_collapsed.setSelection(edit_text_home_collapsed.getText().length());
                InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                manager.showSoftInput(edit_text_home_collapsed, InputMethodManager.SHOW_IMPLICIT);
            }
        });

//        rootView = binding.rootView;
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                try{
//                    requireActivity().getWindow().getDecorView().setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                }catch (Exception e){}
//            }
//        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                View focusedView = getActivity().getCurrentFocus();
                if (focusedView != null) {
                    manager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

//                requireActivity().getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

//        return inflater.inflate(R.layout.fragment_search, container, false);
        return root;
    }

    void doSearch() {
        String s = edit_text_home_collapsed.getText().toString();
        if(!s.equals("") && !s.equals(" ")){
            edit_text_home_collapsed.setText("");
            edit_text_home_collapsed.setVisibility(View.INVISIBLE);
            keyword.setText(s);
            keyword_area.setVisibility(View.VISIBLE);

            binding.historyCtr.setVisibility(View.GONE);
            binding.searchHistoryList.setVisibility(View.GONE);

            ///Search result Display
            model.loadSearchGoods(s);
            model.getGoods().observe(getViewLifecycleOwner(), goods -> {
                if(goods.size() > 0){
                    mAdapter.setData(goods);
                    recyclerView.setVisibility(View.VISIBLE);
                    not_found.setVisibility(View.GONE);
                }
                else{
                    recyclerView.setVisibility(View.GONE);
                    not_found.setVisibility(View.VISIBLE);
                }
            });
            //add history into DB
            boolean newKey = true;
            for (int i=0; i<search_history.size(); i++) {
                if(s.equals(search_history.get(i))) newKey = false;
            }
            if(newKey){
                mDbOpenHelper.insertSearchHistory(s);
            }
        }
    }

    public void getDatabase(){
        Cursor iCursor = mDbOpenHelper.getSearchHistory();
        while(iCursor.moveToNext()){
            String keyword = iCursor.getString(1);
            search_history.add(keyword);
        }
        Collections.reverse(search_history);
    }

    @Override
    public void onClick(View view, int position) {
        Intent detailProduct = new Intent(getActivity(), ProductDetailActivity.class);
        Good productInfo = mAdapter.getGoodInfo(position);
        String pkId = productInfo.pkId;

        detailProduct.putExtra("pkId", pkId);
        startActivity(detailProduct);

    }

}