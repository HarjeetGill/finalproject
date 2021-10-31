package com.techai.shiftme.ui.agency.track;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techai.shiftme.R;
import com.techai.shiftme.data.model.LocationUpdates;
import com.techai.shiftme.data.model.Request;
import com.techai.shiftme.databinding.ActivityTrackBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityTrackBinding binding;
    private GoogleMap mMap;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private LocationRequest locationRequest;
    private LocationUpdates locationUpdates;
    private DatabaseReference dbReference;
    private String userId;
    private Request request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        parseIntentExtras();
        setUpGoogleMap();
        setUpDb();
    }

    private void parseIntentExtras() {
        request = getIntent().getParcelableExtra(Constants.SEND_REQUEST_TO_TRACK);
    }

    private void setUpDb() {
        userId = SharedPrefUtils.getStringData(this, Constants.FIREBASE_ID);
        dbReference = FirebaseDatabase.getInstance().getReference(Constants.LOCATIONS);
    }

    private void setUpGoogleMap() {
        // Construct a FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
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

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // Location updates in here
                updateLocationOnServer(locationResult.getLastLocation());
                super.onLocationResult(locationResult);
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        }, Looper.getMainLooper());

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

    private void updateLocationOnServer(Location location) {
        if (locationUpdates == null) {
            locationUpdates = new LocationUpdates();
        }
        locationUpdates.setLatitude(location.getLatitude());
        locationUpdates.setLongitude(location.getLongitude());
        dbReference.child(userId).setValue(locationUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

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
        setResult(RESULT_OK, returnIntent);
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

    /**
     * Prompts the user for permission to use the device location.
     */
    /* private void getLocationPermission() {

     *//*Request location permission, so that we can get the location of the
      device. The result of the permission request is handled by a callback,
      onRequestPermissionsResult.*//*

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }*/


}
