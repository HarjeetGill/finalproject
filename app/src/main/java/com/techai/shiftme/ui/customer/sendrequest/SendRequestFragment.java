package com.techai.shiftme.ui.customer.sendrequest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.validator.ChipifyingNachoValidator;
import com.techai.shiftme.R;
import com.techai.shiftme.databinding.FragmentSendRequestBinding;

import java.util.Calendar;

public class SendRequestFragment extends Fragment implements View.OnClickListener {

    private SendRequestViewModel sendRequestViewModel;
    private FragmentSendRequestBinding binding;
    private final Calendar myCalendar = Calendar.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendRequestViewModel = new ViewModelProvider(this).get(SendRequestViewModel.class);

        binding = FragmentSendRequestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSendRequest.setOnClickListener(this);
        setUpItemsText();
        binding.etDate.setOnClickListener(v -> openDatePicker());
        binding.etTime.setOnClickListener(v -> openTimePicker());
    }

    private void openTimePicker() {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker= new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                binding.etTime.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.show();
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    binding.etDate.setText(selectedDate);

                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void setUpItemsText() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSendRequest) {
        }
    }
}