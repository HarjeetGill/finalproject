package com.techai.shiftme.ui.agency.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

public class RequestsViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> updated = new MutableLiveData<>();

    public RequestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}
