package com.techai.shiftme;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.techai.shiftme.data.model.SignUpModel;

public class AuthenticationActivity extends AppCompatActivity {

    private SignUpModel signUpModel;
    private Boolean isFromHome;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_auth);
    }

    public void setChangePassword(SignUpModel signUpData, Boolean fromHome){
        signUpModel = signUpData;
        isFromHome = fromHome;
    }

    public SignUpModel getSignUpData(){
        return signUpModel;
    }

    public Boolean getFromHome(){
        return isFromHome;
    }

}