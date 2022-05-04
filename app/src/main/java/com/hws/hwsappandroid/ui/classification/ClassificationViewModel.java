package com.hws.hwsappandroid.ui.classification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClassificationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ClassificationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Classification fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}