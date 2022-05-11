package com.hws.hwsappandroid.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hws.hwsappandroid.model.shippingAdr;

import java.util.ArrayList;

public class ShippingAddressModel extends ViewModel {

    private final MutableLiveData<ArrayList<shippingAdr>> mShippingAddress = new MutableLiveData<>();
    private boolean isLoading = false;

    public LiveData<ArrayList<shippingAdr>> getAddress() {
        return mShippingAddress;
    }

    public void loadData() {

    }
}
