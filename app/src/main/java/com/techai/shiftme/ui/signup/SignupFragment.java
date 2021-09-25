package com.techai.shiftme.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.BR;
import com.techai.shiftme.databinding.FragmentSignUpBinding;
import com.techai.shiftme.ui.model.SignUpModel;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

public class SignupFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private SignupViewModel viewModel;
    private CollectionReference collectionRef = null;
    private FirebaseFirestore db = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignupViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setVariable(BR.viewModel, viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        viewModel.navigate.observe(getViewLifecycleOwner(), new Observer<SignUpModel>() {
            @Override
            public void onChanged(SignUpModel signUpModel) {
                if(signUpModel != null){
                    checkIfUserAlreadyExists(signUpModel);
                }
            }
        });

        binding.tvGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });


        binding.spinnerUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                String selectedRole = binding.spinnerUserRole.getSelectedItem().toString();
                viewModel.userRole.setValue(selectedRole);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void checkIfUserAlreadyExists(SignUpModel signUpModel){
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        collectionRef = db.collection(Constants.USERS);
        collectionRef
                .whereEqualTo("emailId", signUpModel.getEmailId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if(task.isSuccessful()){
                            if(task.getResult().size() >= 1){
                                ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "User already exists.");
                            } else {
                                collectionRef
                                        .whereEqualTo("phoneNumber", signUpModel.getPhoneNumber())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    if(task.getResult().size() >= 1){
                                                        ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "User already exists.");
                                                    } else{
                                                        Bundle bundle = new Bundle();
                                                        bundle.putParcelable(Constants.SIGN_UP_MODEL, signUpModel);
                                                        //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signupFragment_to_verifyOtpFragment, bundle);
                                                    }
                                                } else {
                                                    ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Could not check if user exists. Probable reason: " + task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
//                            Bundle bundle = new Bundle();
//                            bundle.putParcelable(Constants.SIGN_UP_MODEL, signUpModel);
//                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signupFragment_to_verifyOtpFragment, bundle);
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Could not check if user exists. Probable reason: " + task.getException());
                        }
                    }
                });
    }

}
