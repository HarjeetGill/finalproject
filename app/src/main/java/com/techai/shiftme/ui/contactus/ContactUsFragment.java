package com.techai.shiftme.ui.contactus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.techai.shiftme.databinding.FragmentContactUsBinding;

public class ContactUsFragment extends Fragment {

    private FragmentContactUsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactUsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.txtContact1.setText("Name: Harjeet Gill\n"+"Email: gillharjeet140@gmail.com\n"+"Number: +14512356789");
        binding.txtContact2.setText("Name: Mohit Chandaliya\n"+"Email: mohitchandaliya@gmail.com\n"+"Number: +14512356789");
        binding.txtContact3.setText("Name: Ajay Singh\n"+"Email: ajaysingh.78540@gmail.com\n"+"Number: +14512356789");
        binding.txtContact4.setText("Name: Hetal Chaudhary\n"+"Email: hetalchaudhary787@gmail.com\n"+"Number: +14512356789");
    }
}
