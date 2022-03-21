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

import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.fragments.HomeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

    AtomicReference<LatLng> current_latLng;
    private HashMap<String,SportActivity> activities=new HashMap<>();
    public  HashMap<String,SportActivity>getActivities() {
        return activities;
    }

    public void setActivities( HashMap<String,SportActivity> activities) {
        this.activities = activities;
    }

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
        getAllActivities(mMap);
    }

    public LatLng stringToLatLng(String string){
        String res = string.substring(string.indexOf("(")+1, string.indexOf(")"));
        String[] latlong =  res.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        return new LatLng(latitude,longitude);
    }

    public void getAllActivities(GoogleMap mMap) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("activities");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> map=new HashMap<>();//Creating HashMap
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Object act = ds.getValue();    //Static types are wanky here
                    map.put(ds.getKey(),act);
                }
                setLocationPoints(map,mMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersRef.addValueEventListener(eventListener);
    }

    private void setLocationPoints(HashMap<String,Object>  activities, GoogleMap mMap) {
        this.setActivities(new HashMap<String,SportActivity>());

        for (String key : activities.keySet()) {

            HashMap act=  (HashMap) activities.get(key);
            String sport = (String)act.get("sport");
            String description = (String)act.get("description");
            String date = (String)act.get("date");
            String hour = (String)act.get("hour");
            String uuidOrganiser = (String)act.get("uuidOrganiser");
            String coords = (String)act.get("coords");
            ArrayList<String> uuids = (ArrayList<String>)act.get("uuids");

            SportActivity current = new SportActivity(sport,description,date,hour,uuidOrganiser,coords);
            Log.d("PING", current.toString());
            Log.d("PING", current.getDescription());
            Log.d("current sport social", description);
            Log.d("current sport social", "hey");

            current.setUuids(uuids);
            getActivities().put(key,current);

            //TODO ADD ICON MANAGEMENT
            MarkerOptions marker = new MarkerOptions();

            Log.d("DEBUG",""+ sport);
           // Toast.makeText(view.getContext(), "hello "+ sport,Toast.LENGTH_SHORT).show();

            BitmapDescriptor icon = checkIcon(sport);
            if (icon != null) {
                mMap.addMarker(marker.position(stringToLatLng(current.getCoords())).title(key).icon(icon));
            } else {
                mMap.addMarker(marker.position(stringToLatLng(current.getCoords())).title(key));
            }
        }

        mMap.setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            String title = (marker.getTitle());
            SportActivity clicked = getActivities().get(title);
            Log.d("Clicked",clicked.getDescription());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            //Using position get Value from arraylist
            return true;
        });

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
            case "Football":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_football);
                break;
            case "Tennis":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_tennis);
                break;
            case "Volleyball":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_volley);
                break;
            case "Soccer":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_soccer);
                break;
            case "Basketball":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_basket);
                break;
            case "Handball":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_hand);
                break;
            case "Running":
                icon = bitmapDescriptorFromVector(activity, R.drawable.img_map_run);
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
