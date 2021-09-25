package com.techai.shiftme.ui.verifyotp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.techai.shiftme.databinding.FragmentVerifyOtpBinding;


public class VerifyOtpFragment extends Fragment {

    private FragmentVerifyOtpBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyOtpBinding.inflate(inflater);
        return binding.getRoot();
    }

}
