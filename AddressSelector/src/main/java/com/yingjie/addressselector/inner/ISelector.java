package com.yingjie.addressselector.inner;

import android.content.Context;

import com.yingjie.addressselector.api.OnSelectorListener;

public interface ISelector {

    void showSelector(Context context, int mType, String province, String city, String area, OnSelectorListener onRegionListener);
}
