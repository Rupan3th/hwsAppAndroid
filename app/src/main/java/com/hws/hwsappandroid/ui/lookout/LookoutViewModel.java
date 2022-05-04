package com.hws.hwsappandroid.ui.lookout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LookoutViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LookoutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Lookout fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}