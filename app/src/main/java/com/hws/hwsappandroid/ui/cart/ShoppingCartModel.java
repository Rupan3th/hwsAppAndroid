package com.hws.hwsappandroid.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShoppingCartModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ShoppingCartModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ShoppingCart fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}