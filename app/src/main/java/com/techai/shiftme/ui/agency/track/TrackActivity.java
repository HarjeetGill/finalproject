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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
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
import com.techai.shiftme.utils.ToastUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


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
    private MarkerOptions markerOptions;
    private Marker marker;
    private Boolean isLocationAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpGoogleMap();
        setUpDb();
        parseIntentExtras();
    }

    private void parseIntentExtras() {
        request = getIntent().getParcelableExtra(Constants.SEND_REQUEST_TO_TRACK);
    }

    private void addPickDestinationMarker() {
        LatLng pickLatLng = new LatLng(request.getPickLatitude(), request.getPickLongitude());
        LatLng destinationLatLng = new LatLng(request.getDestinationLatitude(), request.getDestinationLongitude());
        mMap.addMarker(new MarkerOptions().position(pickLatLng).title(getString(R.string.pick_location)));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(getString(R.string.destination_location)));
        addPolyline(getDirections(pickLatLng, destinationLatLng, false));
        addPolyline(getDirections(pickLatLng, destinationLatLng, true));
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

    private DirectionsRoute[] getDirections(LatLng origin, LatLng destination, boolean isDestination) {
        com.google.maps.model.LatLng pickLatLng = new com.google.maps.model.LatLng(origin.latitude, origin.longitude);
        com.google.maps.model.LatLng destinationLatLng = new com.google.maps.model.LatLng(destination.latitude, destination.longitude);
        com.google.maps.model.LatLng currentLatLng = new com.google.maps.model.LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        if (isDestination) {
            try {
                return DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.DRIVING)
                        .origin(pickLatLng)
                        .destination(destinationLatLng)
                        .await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                return DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.DRIVING)
                        .origin(currentLatLng)
                        .destination(pickLatLng)
                        .await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DirectionsRoute[0];
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.maps_api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsRoute[] results) {
        List<LatLng> decodedPath = PolyUtil.decode(results[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
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

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 12f));
                    addPickDestinationMarker();
                    if(request.getStatus().equals(Constants.APPROVED_REQUEST)){
                        if(userId.equals(request.getAgencyFirebaseId())){
                            requestLocationUpdates();
                        } else {
                            valueChangeListener(request.getAgencyFirebaseId());
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // Location updates in here
                updateLocationOnServer(locationResult.getLastLocation());
                lastLocation = locationResult.getLastLocation();
                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title(getString(R.string.tv_current_location)));
                super.onLocationResult(locationResult);
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        }, Looper.getMainLooper());
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

    private void valueChangeListener(String id) {
        dbReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ToastUtils.longCustomToast(getLayoutInflater(), binding.getRoot(), 0, snapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        markerOptions = new MarkerOptions().position(latLng).title(getString(R.string.tv_current_location));
        marker = mMap.addMarker(markerOptions);
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
