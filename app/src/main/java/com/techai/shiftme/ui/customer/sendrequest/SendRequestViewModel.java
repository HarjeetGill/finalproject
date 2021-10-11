package com.techai.shiftme.ui.customer.sendrequest;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.techai.shiftme.utils.ValidationUtils;

import java.util.Calendar;
import java.util.List;

import kotlin.Pair;

public class SendRequestViewModel extends AndroidViewModel {

    public MutableLiveData<String> date = new MutableLiveData<>("");
    public MutableLiveData<String> errorDate = new MutableLiveData<>("");
    public MutableLiveData<String> time = new MutableLiveData<>("");
    public MutableLiveData<String> errorTime = new MutableLiveData<>("");
    public MutableLiveData<String> pickLocation = new MutableLiveData<>("");
    public MutableLiveData<String> errorPickLocation= new MutableLiveData<>("");
    public MutableLiveData<String> destinationLocation = new MutableLiveData<>("");
    public MutableLiveData<String> errorDestinationLocation = new MutableLiveData<>("");
    public MutableLiveData<String> vehicle =  new MutableLiveData<>("");
    public MutableLiveData<List<String>> itemList =  new MutableLiveData<>();
    public MutableLiveData<String> errorItemList =  new MutableLiveData<>("");
    public boolean isAllFieldsValid = true;

    private final Calendar myCalendar = Calendar.getInstance();

    public SendRequestViewModel(@NonNull Application application) {
        super(application);
    }

    public void onSendRequestClick(View view) {
        isAllFieldsValid = true;
        isItemsValid();
        isDateValid();
        isTimeValid();
        if (isAllFieldsValid) {
            Log.d("TAG", "onSendRequestClick: Valid Value");
        }
    }

    public void isItemsValid(){
        Pair<Boolean, Integer> result = ValidationUtils.isStringListValid(itemList.getValue());
        if (result.getFirst()) {
            errorItemList.postValue("");
        } else {
            isAllFieldsValid = false;
            errorItemList.postValue(String.format(getApplication().getString(result.getSecond()), "items to shift"));
        }
    }

    public void isDateValid() {
        Pair<Boolean, Integer> result = ValidationUtils.isFieldValid(date.getValue());
        if (result.getFirst()) {
            errorDate.postValue("");
        } else {
            isAllFieldsValid = false;
            errorDate.postValue(String.format(getApplication().getString(result.getSecond()), "date"));
        }
    }

    public void isTimeValid() {
        Pair<Boolean, Integer> result = ValidationUtils.isFieldValid(time.getValue());
        if (result.getFirst()) {
            errorTime.postValue("");
        } else {
            isAllFieldsValid = false;
            errorTime.postValue(String.format(getApplication().getString(result.getSecond()), "time"));
        }
    }

    public void isPickLocationValid() {
        Pair<Boolean, Integer> result = ValidationUtils.isFieldValid(pickLocation.getValue());
        if (result.getFirst()) {
            errorPickLocation.postValue("");
        } else {
            isAllFieldsValid = false;
            errorPickLocation.postValue(String.format(getApplication().getString(result.getSecond()), "pick location"));
        }
    }

    public void isDestinationLocationValid() {
        Pair<Boolean, Integer> result = ValidationUtils.isFieldValid(destinationLocation.getValue());
        if (result.getFirst()) {
            errorDestinationLocation.postValue("");
        } else {
            isAllFieldsValid = false;
            errorDestinationLocation.postValue(String.format(getApplication().getString(result.getSecond()), "distance location"));
        }
    }
    public void openDatePicker(Context context) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            date.setValue(selectedDate);
            errorDate.postValue("");

        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void openTimePicker(Context context) {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.postValue(selectedHour + ":" + selectedMinute);
                errorTime.postValue("");
            }
        }, hour, minute, true);
        mTimePicker.show();
    }

}