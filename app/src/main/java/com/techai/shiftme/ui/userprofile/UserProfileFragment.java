package com.techai.shiftme.ui.userprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techai.shiftme.BR;
import com.techai.shiftme.R;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.databinding.FragmentUserProfileBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.ui.agency.AgencyActivity;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;
import com.techai.shiftme.ui.customer.CustomerActivity;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private UserProfileViewModel viewModel;
    private String firebaseId = "";
    private FirebaseFirestore db = null;
    private CollectionReference collectionRef = null;
    private SignUpModel signUpModel = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this, new LoginViewModelFactory(getActivity().getApplication())).get(UserProfileViewModel.class);
        binding.setVariable(BR.viewModel, viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseId = SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID);

        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        db = FirebaseFirestore.getInstance();
        collectionRef = db.collection(Constants.USERS);
        collectionRef
                .whereEqualTo("firebaseId", firebaseId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if (task.isSuccessful()) {
                            if (task.getResult().size() >= 1) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    signUpModel = new SignUpModel();
                                    signUpModel = document.toObject(SignUpModel.class);
                                }
                                viewModel.setData(signUpModel);
                            }
                        } else {
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Could not fetch data. Probable reason: " + task.getException());
                        }
                    }
                });

        viewModel.navigate.observe(this, new Observer<SignUpModel>() {
            @Override
            public void onChanged(SignUpModel signUpModel) {
                if (signUpModel != null) {
                    updateData(signUpModel);
                }
            }
        });

        getToolbarClick();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireActivity() instanceof CustomerActivity) {
            ((CustomerActivity) requireActivity()).showToolbarMenu(true);
        } else if (requireActivity() instanceof AgencyActivity) {
            ((AgencyActivity) requireActivity()).showToolbarMenu(true);
        }
    }

    @Override
    public void onDestroy() {
        if (requireActivity() instanceof CustomerActivity) {
            ((CustomerActivity) requireActivity()).showToolbarMenu(false);
        } else if (requireActivity() instanceof AgencyActivity) {
            ((AgencyActivity) requireActivity()).showToolbarMenu(false);
        }
        super.onDestroy();
    }

    private void getToolbarClick() {
        if (requireActivity() instanceof CustomerActivity) {
            ((CustomerActivity) requireActivity()).getToolbarView().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        viewModel.isEditIcon(viewModel.isEnabled.getValue());
                    }
                    return false;
                }
            });
        } else if (requireActivity() instanceof AgencyActivity) {
            ((AgencyActivity) requireActivity()).getToolbarView().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        viewModel.isEditIcon(viewModel.isEnabled.getValue());
                    }
                    return false;
                }
            });
        }
    }

    private void updateData(SignUpModel signUpModel) {
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        db.collection(Constants.USERS)
                .document(SharedPrefUtils.getStringData(requireContext(), Constants.FIREBASE_ID))
                .update("fullName", signUpModel.getFullName(), "address", signUpModel.getAddress(), "emailId", signUpModel.getEmailId(), "phoneNumber",
                        signUpModel.getPhoneNumber())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        if (task.isSuccessful()) {
                            SharedPrefUtils.saveObject(requireContext(), Constants.SIGN_UP_MODEL, signUpModel);
                            if (requireActivity() instanceof CustomerActivity) {
                                ((CustomerActivity) requireActivity()).updateProfileDetails();
                            } else if (requireActivity() instanceof AgencyActivity) {
                                ((AgencyActivity) requireActivity()).updateProfileDetails();
                            }
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Data saved successfully.");
                        } else {
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Could not save data. Probable reason: " + task.getException());
                        }
                    }
                });
    }

}
