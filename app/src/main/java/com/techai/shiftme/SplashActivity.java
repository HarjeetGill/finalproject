package com.techai.shiftme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.databinding.ActivitySplashBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.agency.AgencyActivity;
import com.techai.shiftme.ui.customer.CustomerActivity;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

public class SplashActivity extends AppCompatActivity {

    private FirebaseFirestore db = null;
    private CollectionReference collectionRef = null;
    private ActivitySplashBinding binding = null;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        collectionRef = db.collection(Constants.USERS);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPrefUtils.getBooleanData(SplashActivity.this, Constants.IS_LOGGED_IN)) {
                    SignUpModel signUpModel = SharedPrefUtils.getObject(SplashActivity.this, Constants.SIGN_UP_MODEL, SignUpModel.class);
                    getUserData(signUpModel.getFirebaseId(), signUpModel.getUserRole());
                } else {
                    startActivity(new Intent(SplashActivity.this, AuthenticationActivity.class));
                    finish();
                }
            }
        }, 2000);

    }

    private void getUserData(String firebaseId, String userRole) {
        collectionRef
                .whereEqualTo("firebaseId", firebaseId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() >= 1) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    SignUpModel newSignUpModel = document.toObject(SignUpModel.class);
                                    SharedPrefUtils.saveObject(SplashActivity.this, Constants.SIGN_UP_MODEL, newSignUpModel);
                                    if (userRole.equals(Constants.CUSTOMER_USER_ROLE)) {
                                        startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, AgencyActivity.class));
                                    }
                                }
                            }
                        } else {
                            ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, "Could not fetch data. Probable reason: " + task.getException());
                        }
                        finish();
                    }
                });
    }

}
