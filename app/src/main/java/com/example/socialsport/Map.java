package com.example.socialsport;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.fragments.HomeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    AtomicReference<LatLng> currentLatLng;
    private final HashMap<String, SportActivity> sportActivities = new HashMap<>();

    public HashMap<String, SportActivity> getSportActivities() {
        return sportActivities;
    }

    public Map(GoogleMap googleMap, Activity activity, View view) {
        mMap = googleMap;
        this.activity = activity;
        this.view = view;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        currentLatLng = new AtomicReference<>(defaultLocation);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        getAllActivities();
    }

    public void getAllActivities() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activitiesRef = rootRef.child("activities");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap act = (HashMap) ds.getValue(); //Static types are wanky here
                    assert act != null;
                    Log.d("Firebase_activity", act.toString());

                    String sport = (String) act.get("sport");
                    String description = (String) act.get("description");
                    String date = (String) act.get("date");
                    String hour = (String) act.get("hour");
                    String uuidOrganiser = (String) act.get("uuidOrganiser");
                    String coords = (String) act.get("coords");
                    ArrayList<String> uuids = (ArrayList<String>) act.get("uuids");

                    SportActivity newActivity = new SportActivity(sport, description, date, hour, uuidOrganiser, coords);
                    newActivity.setUuids(uuids);

                    sportActivities.put(ds.getKey(), newActivity);
                }
                setLocationPoints();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //
            }
        };
        activitiesRef.addValueEventListener(eventListener);
    }

    private void setLocationPoints() {
        for (java.util.Map.Entry<String, SportActivity> currentActivity : sportActivities.entrySet()) {
            MarkerOptions marker = new MarkerOptions();
            assert currentActivity != null;
            BitmapDescriptor icon = Utils.getBitmapDescriptor(activity, currentActivity.getValue().getSport());
            if (icon != null) {
                mMap.addMarker(marker.position(Utils.stringToLatLng(currentActivity.getValue().getCoords())).title(currentActivity.getKey()).icon(icon));
            } else {
                mMap.addMarker(marker.position(Utils.stringToLatLng(currentActivity.getValue().getCoords())).title(currentActivity.getKey()));
            }
        }
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
            Log.e(TAG, e.getMessage(), e);
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
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void searchPlaceListener() {
        // Create searching localisation method
        EditText etLocalisation = view.findViewById(R.id.et_search_city);
        etLocalisation.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //Save the answer
                String localisation = etLocalisation.getText().toString();
                Geocoder gc = new Geocoder(activity.getApplicationContext());
                try {
                    List<Address> addresses = gc.getFromLocationName(localisation, 1);
                    if (addresses.isEmpty())
                        Toast.makeText(activity, "Error: the searched place doesn't exist", Toast.LENGTH_SHORT).show();
                    else {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();
                        LatLng placeToFind = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeToFind, 15));
                    }
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                    e.printStackTrace();
                }

                //Delete text from edit text
                etLocalisation.setText("");

                return true;
            }
            return false;
        });
    }

    public void addActivityMarker(String sport) {
        MarkerOptions marker = new MarkerOptions();

        BitmapDescriptor icon = Utils.getBitmapDescriptor(activity, sport);

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(marker.position(latLng).title("Position you choose"));
            currentLatLng.set(latLng);
        });

        if (icon != null) {
            mMap.addMarker(marker.position(currentLatLng.get()).title("default").icon(icon));
        } else {
            mMap.addMarker(marker.position(currentLatLng.get()).title("default"));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng.get()));
    }

    public AtomicReference<LatLng> getCurrentLatLng() {
        return currentLatLng;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

}
