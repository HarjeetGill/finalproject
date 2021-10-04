package com.techai.shiftme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.customer.CustomerActivity;
import com.techai.shiftme.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPrefUtils.getBooleanData(SplashActivity.this, Constants.IS_LOGGED_IN)) {
                    SignUpModel signUpModel=SharedPrefUtils.getObject(SplashActivity.this,Constants.SIGN_UP_MODEL, SignUpModel.class);
                    if(signUpModel.getUserRole().equals(Constants.CUSTOMER_USER_ROLE)){
                        startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
                }
                finish();
            }
        }, 2500);

    }
}
