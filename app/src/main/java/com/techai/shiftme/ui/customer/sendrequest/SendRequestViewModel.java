package com.techai.shiftme.ui.customer.sendrequest;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.SingleLiveEvent;
import com.techai.shiftme.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
    public MutableLiveData<String> noOfMovers = new MutableLiveData<>("");
    public MutableLiveData<String> errorNoOfMovers= new MutableLiveData<>("");
    public boolean isAllFieldsValid = true;
    public Request request = null;
    public SingleLiveEvent<Request> shiftRequest = new SingleLiveEvent<Request>();

    private final Calendar myCalendar = Calendar.getInstance();

    public SendRequestViewModel(@NonNull Application application) {
        super(application);
    }

    public void onSendRequestClick(View view) {
        isAllFieldsValid = true;
        isItemsValid();
        isDateValid();
        isTimeValid();
        isNoOfMoversValid();
        if (isAllFieldsValid) {
            request = new Request((ArrayList<String>) itemList.getValue(),0.0,0.0,0.0,
                    0.0,date.getValue()+Constants.DATE_TIME_SEPARATOR +time.getValue(),"Small",
                    "10 $",Integer.valueOf(Objects.requireNonNull(noOfMovers.getValue())), Constants.PENDING_REQUEST,
                    SharedPrefUtils.getObject(view.getContext(),Constants.SIGN_UP_MODEL, SignUpModel.class));
            shiftRequest.postValue(request);
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

    public void isNoOfMoversValid(){
        Pair<Boolean, Integer> result = ValidationUtils.isFieldValid(noOfMovers.getValue());
        if (result.getFirst()) {
            errorNoOfMovers.postValue("");
        } else {
            isAllFieldsValid = false;
            errorNoOfMovers.postValue(String.format(getApplication().getString(result.getSecond()), "no. of movers"));
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

    public void clearAllFields(){
        date.postValue("");
        errorDate.postValue("");
        time.postValue("");
        errorTime.postValue("");
        pickLocation.postValue("");
        errorPickLocation.postValue("");
        destinationLocation.postValue("");
        errorDestinationLocation.postValue("");
        vehicle.postValue("");
        itemList.postValue(new ArrayList<>());
        errorItemList.postValue("");
        noOfMovers.postValue("");
        errorNoOfMovers.postValue("");
        isAllFieldsValid = false;
    }

}