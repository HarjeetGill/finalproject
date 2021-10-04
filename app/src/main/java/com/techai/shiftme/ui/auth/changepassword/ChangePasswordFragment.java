package com.techai.shiftme.ui.auth.changepassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techai.shiftme.AuthenticationActivity;
import com.techai.shiftme.BR;
import com.techai.shiftme.MainActivity;
import com.techai.shiftme.databinding.FragmentChangePasswordBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ToastUtils;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;
    private ChangePasswordViewModel viewModel;
    private SignUpModel signUpModel;
    private CollectionReference collectionRef = null;
    private FirebaseFirestore db = null;
    private Boolean fromHome = false;
    private String firebaseUserId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this, new LoginViewModelFactory(getActivity().getApplication())).get(ChangePasswordViewModel.class);
        binding.setVariable(BR.viewModel, viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (requireActivity() instanceof AuthenticationActivity) {
            signUpModel = ((AuthenticationActivity) requireActivity()).getSignUpData();
        }

//        signUpModel = getArguments().getParcelable(Constants.SIGN_UP_MODEL);

        if (signUpModel != null) {
            firebaseUserId = signUpModel.getFirebaseId();
        }

        if (requireActivity() instanceof AuthenticationActivity) {
            fromHome = ((AuthenticationActivity) requireActivity()).getFromHome();
        }

//        fromHome = getArguments().getBoolean(Constants.FROM_HOME);

        if (fromHome) {
            firebaseUserId = SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID);
        }

        db = FirebaseFirestore.getInstance();

        viewModel.newPassword.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && !s.isEmpty()) {
                    setNewPassword(s, firebaseUserId);
                }
            }
        });

    }

    private void setNewPassword(String pwd, String firebaseId) {
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        collectionRef = db.collection(Constants.USERS);
        collectionRef
                .document(firebaseId)
                .update("password", pwd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if (task.isSuccessful()) {
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Password changed in successfully");
                            SharedPrefUtils.saveData(requireContext(), Constants.IS_LOGGED_IN, true);
                            SharedPrefUtils.saveData(requireContext(), Constants.FIREBASE_ID, signUpModel.getFirebaseId());
                            startActivity(new Intent(requireContext(), MainActivity.class));
                            if (requireActivity() instanceof AuthenticationActivity) {
                                requireActivity().finish();
                            }
                        } else {
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Could not log in. Probable reason: " + task.getException());
                        }
                    }
                });
    }

}
