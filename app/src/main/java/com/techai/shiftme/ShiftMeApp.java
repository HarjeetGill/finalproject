package com.techai.shiftme;

import android.app.Application;
import android.content.SharedPreferences;

import com.techai.shiftme.preferences.SharedPrefUtils;

public class ShiftMeApp extends Application {

    static ShiftMeApp instance;
    static SharedPreferences.Editor sharedPrefUtils;

    public static ShiftMeApp the() {
        return instance;
    }

    public static SharedPreferences.Editor getSharedPref() {
        return sharedPrefUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPrefUtils = SharedPrefUtils.getSharedPrefEditor(getApplicationContext(), getString(R.string.app_name));
    }

}
