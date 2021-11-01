package com.techai.shiftme.ui.customer.home.tabs.sendrequest;

import android.app.Activity;

import android.content.Intent;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.techai.shiftme.BR;
import com.techai.shiftme.R;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.databinding.FragmentSendRequestBinding;
import com.techai.shiftme.ui.auth.login.LoginViewModelFactory;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ShiftMeUtils;
import com.techai.shiftme.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SendRequestFragment extends Fragment implements View.OnClickListener {

    private SendRequestViewModel sendRequestViewModel;
    private FragmentSendRequestBinding binding;
    private AddItemsAdapter adapter;
    private List<String> itemList = new ArrayList<>();
    private String selectedVehicle;
    private FirebaseFirestore db = null;
    private int PICK_LOCATION_REQUEST_CODE = 11, DESTINATION_LOCATION_REQUEST_CODE = 21;
    private Double pickLatitude, pickLongitude, destinationLatitude, destinationLongitude;
    private String pickLocation, destinationLocation;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendRequestViewModel = new ViewModelProvider(requireActivity(), new LoginViewModelFactory(getActivity().getApplication())).get(SendRequestViewModel.class);
        binding = FragmentSendRequestBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setVariable(BR.viewModel, sendRequestViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        binding.btnSendRequest.setOnClickListener(this);
        setUpViews();
        setUpAdapter();
        setUpClickListener();
        setUpObserver();
    }

    private void setUpObserver() {
        sendRequestViewModel.shiftRequest.observe(getViewLifecycleOwner(), new Observer<Request>() {
            @Override
            public void onChanged(Request request) {
                if (request != null) {
                    saveShiftRequest(request);
                }
            }
        });
    }

    private void saveShiftRequest(Request request) {
        AppProgressUtil.INSTANCE.showOldProgressDialog(requireContext());
        DirectionsRoute[] directions = getDirections(new LatLng(request.getPickLatitude(), request.getPickLongitude()),
                new LatLng(request.getDestinationLatitude(), request.getDestinationLongitude()));
        request.setDistance(directions[0].legs[0].distance.humanReadable);
        request.setCostOfShifting((getCostOfShifting(request) *
                Double.parseDouble(directions[0].legs[0].distance.humanReadable.split(" km")[0])) + "$");
        db.collection(Constants.REQUESTS)
                .add(request)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        sendRequestViewModel.requestSent.postValue(true);
                        if (task.isSuccessful()) {
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, "Successfully Request Submitted");
                            adapter.clearList();
                            sendRequestViewModel.clearAllFields();
                        } else {
                            ToastUtils.longCustomToast(getLayoutInflater(), requireView(), 0, String.valueOf(task.getException()));
                        }
                    }
                });

    }

    private int getCostOfShifting(Request request) {
        String list[] = request.getVehicleType().split(" - ");
        list = list[1].split("\\$");
        return Integer.parseInt(list[0]);
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.maps_api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private DirectionsRoute[] getDirections(LatLng origin, LatLng destination) {
        com.google.maps.model.LatLng pickLatLng = new com.google.maps.model.LatLng(origin.latitude, origin.longitude);
        com.google.maps.model.LatLng destinationLatLng = new com.google.maps.model.LatLng(destination.latitude, destination.longitude);
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING)
                    .origin(pickLatLng)
                    .destination(destinationLatLng)
                    .await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DirectionsRoute[0];
    }

    private void setUpClickListener() {
        binding.ivAdd.setOnClickListener(this);
        binding.etDate.setOnClickListener(v -> sendRequestViewModel.openDatePicker(requireContext()));
        binding.etTime.setOnClickListener(v -> sendRequestViewModel.openTimePicker(requireContext()));
        binding.etPicLocation.setOnClickListener(v -> startActivityForResult(new Intent(requireActivity(), MapActivity.class), PICK_LOCATION_REQUEST_CODE));
        binding.etDestinationLocation.setOnClickListener(v -> startActivityForResult(new Intent(requireActivity(), MapActivity.class), DESTINATION_LOCATION_REQUEST_CODE));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get the location from intent
            if (data.getExtras() != null && data.hasExtra(Constants.MAP_ADDRESS)) {
                pickLocation = data.getExtras().getString(Constants.MAP_ADDRESS);
                pickLatitude = data.getExtras().getDouble(Constants.LOCATION_LATITUDE);
                pickLongitude = data.getExtras().getDouble(Constants.LOCATION_LONGITUDE);
                sendRequestViewModel.setPickLocation(pickLocation, pickLatitude, pickLongitude);
            }
        } else if (requestCode == DESTINATION_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get the location from intent
            if (data.getExtras() != null && data.hasExtra(Constants.MAP_ADDRESS)) {
                destinationLocation = data.getExtras().getString(Constants.MAP_ADDRESS);
                destinationLatitude = data.getExtras().getDouble(Constants.LOCATION_LATITUDE);
                destinationLongitude = data.getExtras().getDouble(Constants.LOCATION_LONGITUDE);
                sendRequestViewModel.setDestinationLocation(destinationLocation, destinationLatitude, destinationLongitude);
            }
        }
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