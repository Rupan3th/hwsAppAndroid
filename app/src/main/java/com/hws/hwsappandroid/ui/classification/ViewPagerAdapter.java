package com.hws.hwsappandroid.ui.classification;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.hws.hwsappandroid.model.Category;
import com.hws.hwsappandroid.model.Good;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ArrayList<Category> CategoryTree;

    /**建構子初始化*/
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.CategoryTree = new ArrayList<>();
    }

    /**此功能在本範例中未使用*/
    /**若要做即時資料更新時便可以使用此方法*/
    public void updateData(ArrayList<Category> list){
        CategoryTree = list;
        notifyDataSetChanged();
    }

    /**此功能在本範例中未使用*/
    /**若有即時資料更新的需求，除了使用notifyDataSetChanged()，也必須要在這邊做getItemPosition的複寫*/
    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof PagerFragment){
            return PagerAdapter.POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    /**設置每個Page的內容*/
    @NonNull
    @Override
    public Fragment getItem(int position) {
        PagerFragment pagerFragment = PagerFragment.newInstance(
                CategoryTree.get(position).categoryName, CategoryTree.get(position), position);

        return pagerFragment;
    }
    /**決定要設置幾個Page*/
    @Override
    public int getCount() {
        return CategoryTree.size();
    }
}
