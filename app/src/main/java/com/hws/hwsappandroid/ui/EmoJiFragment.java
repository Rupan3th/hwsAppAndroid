package com.hws.hwsappandroid.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.util.emoji.CircleIndicator;
import com.hws.hwsappandroid.util.emoji.EmoJiContainerAdapter;
import com.hws.hwsappandroid.util.emoji.EmoJiHelper;

public class EmoJiFragment extends Fragment {
    private int type = 0;
    private EditText et_input_container;
    private CircleIndicator circleIndicator;

    public EmoJiFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = View.inflate(getContext(), R.layout.fragment_emoji, null);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MultiEmotionActivity mainActivity = (MultiEmotionActivity) getActivity();
        if (mainActivity != null) {
            et_input_container = mainActivity.getEt_input_container();
        }
        initView();
    }

    private void initView() {
        type = getArguments().getInt("EmoJiType");
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewPager);
        CircleIndicator circleIndicator = (CircleIndicator) getView().findViewById(R.id.circleIndicator);
        EmoJiHelper emojiHelper = new EmoJiHelper(type, getContext(), et_input_container);
        EmoJiContainerAdapter mAdapter = new EmoJiContainerAdapter(emojiHelper.getPagers());
        viewPager.setAdapter(mAdapter);
        circleIndicator.setViewPager(viewPager);
    }
}