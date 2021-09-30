package com.techai.shiftme.ui.customer.sendrequest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendRequestViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SendRequestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is send request fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}