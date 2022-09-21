package com.hws.hwsappandroid.ui.me.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.ui.classification.PagerFragment;

import java.util.ArrayList;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.all, R.string.wait_pay, R.string.wait_send, R.string.wait_get, R.string.completed};
    private final Context mContext;
    boolean getInst;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 5;
    }

    /**此功能在本範例中未使用*/
    /**若要做即時資料更新時便可以使用此方法*/
    public void updateData(String keyword){
        OrderViewModel.setKeyword(keyword);
        notifyDataSetChanged();
    }

    /**此功能在本範例中未使用*/
    /**若有即時資料更新的需求，除了使用notifyDataSetChanged()，也必須要在這邊做getItemPosition的複寫*/
    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof PlaceholderFragment){
            return PagerAdapter.POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}