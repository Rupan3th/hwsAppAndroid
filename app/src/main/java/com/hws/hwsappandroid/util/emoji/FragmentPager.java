package com.hws.hwsappandroid.util.emoji;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hws.hwsappandroid.ui.EmoJiFragment;

import java.util.List;

public class FragmentPager extends FragmentStatePagerAdapter {
    private final List<EmoJiFragment> fragments;

    public FragmentPager(FragmentManager fm, List<EmoJiFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}