package com.hws.hwsappandroid.ui.classification;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class VerticalViewPager extends ViewPager {
    public VerticalViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    /**設置頁面變為上下滑動*/
    private void init() {
        setPageTransformer(true, new PageTransformer());
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
    /**複寫ViewPager本身的的手勢操作*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean interceped = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev);
        return interceped;
    }
    /**複寫ViewPager在整體畫面中的手勢操作*/
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }
    /**決定畫面XY軸的變化*/
    private MotionEvent swapXY(MotionEvent ev) {
        float width, height, newX, newY;
        width = (float) getWidth();
        height = (float) getHeight();
        newX = ev.getX() ;
        newY = ev.getY() ;
        ev.setLocation(newX,newY);
        return ev;
    }
    /**設置ViewPager頁面跑動的方式，使原本左右的方式改為上下的方式*/
    private class PageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            if (position < 0) {
                page.setVisibility(View.VISIBLE);
            } else if (position <= 0) {
                page.setVisibility(View.VISIBLE);
                page.setTranslationX(page.getWidth() * position);
                Float y = -position * page.getHeight();
                page.setTranslationY(y);
            } else {
                page.setVisibility(View.INVISIBLE);
            }
        }
    }
}
