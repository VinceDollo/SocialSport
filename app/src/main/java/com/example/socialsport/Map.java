package com.example.socialsport;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialsport.fragments.HomeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Map {

    private final GoogleMap mMap;
    private final Activity activity;
    private final View view;

    private final FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = HomeFragment.class.getSimpleName();

    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;

    private Location lastKnownLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085); // Sydney

    AtomicReference<LatLng> current_latLng;

    public Map(GoogleMap googleMap, Activity activity, View view) {
        mMap = googleMap;
        this.activity = activity;
        this.view = view;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        current_latLng = new AtomicReference<>(defaultLocation);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void searchPlaceListener() {
        // Create searching localisation method
        EditText et_localisation = view.findViewById(R.id.et_search_city);
        et_localisation.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //Save the answer
                String localisation = et_localisation.getText().toString();
                Geocoder gc = new Geocoder(activity.getApplicationContext());
                try {
                    List<Address> addresses = gc.getFromLocationName(localisation, 1);
                    if (addresses.isEmpty())
                        Toast.makeText(activity, "Error: the searched place doesn't exist", Toast.LENGTH_SHORT).show();
                    else {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();
                        LatLng placeToFind = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeToFind, 12));
                    }
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                    e.printStackTrace();
                }

                //Delete text from edit text
                et_localisation.setText("");

                return true;
            }
            return false;
        });
    }

    public void addActivityMarker(String sport) {
        MarkerOptions marker = new MarkerOptions();

        BitmapDescriptor icon = checkIcon(sport);

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(marker.position(latLng).title("Position you choose"));
            current_latLng.set(latLng);
        });

        if (icon != null) {
            mMap.addMarker(marker.position(current_latLng.get()).title("default").icon(icon));
        } else {
            mMap.addMarker(marker.position(current_latLng.get()).title("default"));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_latLng.get()));
    }

    private BitmapDescriptor checkIcon(String sport) {
        assert sport != null;
        BitmapDescriptor icon = null;
        switch (sport) {
            case "football":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.img_football);
                break;
            case "tennis":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.img_tennis);
                break;
            case "volley":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.img_volley);
                break;
            case "Soccer":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_soccer_map);
                break;
        }
        return icon;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public AtomicReference<LatLng> getCurrent_latLng() {
        return current_latLng;
    }

}
