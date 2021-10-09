package com.techai.shiftme.ui.customer.sendrequest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendRequestViewModel = new ViewModelProvider(this).get(SendRequestViewModel.class);

        binding = FragmentSendRequestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSendRequest.setOnClickListener(this);
        setUpViews();
        setUpAdapter();
        binding.ivAdd.setOnClickListener(this);
        binding.etDate.setOnClickListener(v -> openDatePicker());
        binding.etTime.setOnClickListener(v -> openTimePicker());
    }

    private void setUpAdapter() {
        binding.rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AddItemsAdapter();
        binding.rvItems.setAdapter(adapter);
    }

    private void openTimePicker() {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                binding.etTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        mTimePicker.show();
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            binding.etDate.setText(selectedDate);

        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void setUpViews() {
        binding.etAddItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int sat, int i1, int i2) {
                binding.ivAdd.setEnabled(charSequence.length() != 0);
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
            ShiftMeUtils.showSoftKeyboard(requireActivity());
            itemList.add(Objects.requireNonNull(binding.etAddItems.getText()).toString());
            adapter.setItemList(itemList);
        }
    }
}