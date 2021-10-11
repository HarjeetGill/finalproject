package com.techai.shiftme.ui.customer.sendrequest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.techai.shiftme.BR;
import com.techai.shiftme.R;
import com.techai.shiftme.databinding.FragmentSendRequestBinding;
import com.techai.shiftme.utils.ShiftMeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SendRequestFragment extends Fragment implements View.OnClickListener {

    private SendRequestViewModel sendRequestViewModel;
    private FragmentSendRequestBinding binding;
    private final Calendar myCalendar = Calendar.getInstance();
    private AddItemsAdapter adapter;
    private List<String> itemList = new ArrayList<>();
    private String selectedVehicle;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendRequestViewModel = new ViewModelProvider(this).get(SendRequestViewModel.class);
        binding = FragmentSendRequestBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setVariable(BR.viewModel, sendRequestViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSendRequest.setOnClickListener(this);
        setUpViews();
        setUpAdapter();
        setUpClickListener();
    }

    private void setUpClickListener() {
        binding.ivAdd.setOnClickListener(this);
        binding.etDate.setOnClickListener(v -> sendRequestViewModel.openDatePicker(requireContext()));
        binding.etTime.setOnClickListener(v -> sendRequestViewModel.openTimePicker(requireContext()));
    }

    private void setUpAdapter() {
        binding.rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AddItemsAdapter();
        binding.rvItems.setAdapter(adapter);
    }


    private void setUpViews() {
        binding.ivAdd.setEnabled(false);
        binding.vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                selectedVehicle = binding.vehicleSpinner.getSelectedItem().toString();
                if (arg1 instanceof TextView) {
                    ((TextView) arg1).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorText));
                }
                sendRequestViewModel.vehicle.setValue(selectedVehicle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        binding.etAddItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int sat, int i1, int i2) {
                binding.ivAdd.setEnabled(charSequence.toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivAdd) {
            ShiftMeUtils.hideSoftKeyboard(requireActivity());
            itemList.add(Objects.requireNonNull(binding.etAddItems.getText()).toString());
            adapter.setItemList(itemList);
            binding.etAddItems.setText("");
            sendRequestViewModel.itemList.setValue(adapter.getItemList());
            sendRequestViewModel.errorItemList.setValue("");
        }
    }
}