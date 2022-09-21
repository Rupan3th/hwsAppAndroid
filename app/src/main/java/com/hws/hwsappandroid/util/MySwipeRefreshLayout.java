package com.hws.hwsappandroid.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    private Runnable onActionDown, onActionUp;

    public MySwipeRefreshLayout(final Context context) {
        super(context);
    }

    public void setOnActionDown(Runnable onActionDown) {
        this.onActionDown = onActionDown;
    }

    public void setOnActionUp(Runnable onActionUp) {
        this.onActionUp = onActionUp;
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        if (ev.getAction() == ev.ACTION_DOWN) {
            if (onActionDown != null) {
                onActionDown.run();
            }
        } else if (ev.getAction() == ev.ACTION_UP) {
            if (onActionUp != null) {
                onActionUp.run();
            }
        }
        return super.onTouchEvent(ev);
    }
}