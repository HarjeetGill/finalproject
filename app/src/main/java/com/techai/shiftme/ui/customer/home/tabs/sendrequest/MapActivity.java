package com.techai.shiftme.ui.customer.home.tabs.sendrequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techai.shiftme.R;
import com.techai.shiftme.databinding.ActivityMapBinding;
import com.techai.shiftme.utils.AppProgressUtil;
import com.techai.shiftme.utils.Constants;
import com.techai.shiftme.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private ActivityMapBinding binding;
    private GoogleMap mMap;
    private Location lastLocation;
    private String selectedPlaceName;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Place.Field> placeFields;
    private PlacesClient placesClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpGoogleMap();
        setOnClickListener();
    }

    private void setOnClickListener() {
        binding.btnSaveLocation.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.MAP_ADDRESS, selectedPlaceName);
            returnIntent.putExtra(Constants.LOCATION_LATITUDE, lastLocation.getLatitude());
            returnIntent.putExtra(Constants.LOCATION_LONGITUDE, lastLocation.getLatitude());
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }

    private void setUpGoogleMap() {
        placeFields = new ArrayList<>();
        placeFields.add(Place.Field.ID);
        placeFields.add(Place.Field.LAT_LNG);
        placeFields.add(Place.Field.ADDRESS);

        // Construct a PlacesClient
        Places.initialize(this, getString(R.string.maps_api_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment!=null){
            mapFragment.getMapAsync(this);
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = ((AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment));
        // Specify the types of place data to return.
        List<Place.Field> list = new ArrayList<>();
        list.add(Place.Field.ID);
        list.add(Place.Field.NAME);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(list);

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    fetchPlaceDetails(place.getId(), place.getName());
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i("TAG", "An error occurred: " + status);
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void fetchPlaceDetails(String placeId, String name) {
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        AppProgressUtil.INSTANCE.showOldProgressDialog(this);
        placesClient.fetchPlace(request)
                .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        AppProgressUtil.INSTANCE.closeOldProgressDialog();
                        Place place = fetchPlaceResponse.getPlace();
                        if (lastLocation == null) {
                            lastLocation = new Location(LocationManager.GPS_PROVIDER);
                        }
                        lastLocation.setLatitude(Objects.requireNonNull(place.getLatLng()).latitude);
                        lastLocation.setLongitude(place.getLatLng().longitude);
                        selectedPlaceName=name;
                        mMap.clear();
                        addMarker(false, name);
                        //fromLocationGetAddress(lastLocation)
                        setAddress(place.getAddress());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppProgressUtil.INSTANCE.closeOldProgressDialog();
                if (e instanceof ApiException) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    Log.i("TAG", "onFailure: " + statusCode);
                }
            }
        });
    }

    private void setUpMap() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            afterLocationGivenSetup();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                //ToastUtils.longCustomToast(getLayoutInflater(), binding.constrainLayout, 0, "Error " + dexterError.name());
            }
        })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
       /* new MaterialAlertDialogBuilder(this)
                .setTitle()
                .title(R.string.title)
                .content(R.string.content)
                .positiveText(R.string.agree);

        MaterialDialog dialog = builder.build();
        dialog.show();
        MaterialDialog(this).show {
            title(R.string.app_name)
            message(R.string.warn_permission_denied_location)
            cancelOnTouchOutside(false)
            cancelable(false)
            positiveButton(R.string.tv_settings) { dialog ->
                    openSettings()
            }
            negativeButton(android.R.string.cancel) { dialog ->
                    cancel()
            }
        }*/
    }

    @SuppressLint("MissingPermission")
    private void afterLocationGivenSetup() {
        // 1
        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if (lastLocation != null) {
                        addMarker(true, "");
                        return;
                    }
                    lastLocation = location;
                    addMarker(true, "");
                    try {
                        fromLocationGetAddress(location);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fromLocationGetAddress(Location location) throws IOException {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(
                location.getLatitude(),
                location.getLongitude(),
                1
        ); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0);
        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        setAddress(address);
    }

    private void setAddress(String address) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.MAP_ADDRESS, address);
       // setResult(RESULT_OK, returnIntent);
        //finish();
    }

    private void addMarker(Boolean isCurrentLocation, String placeName) {
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        if (!isCurrentLocation) {
            if (!placeName.isEmpty()) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
            }
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.tv_current_location)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
    }
}
