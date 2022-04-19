package com.example.socialsport;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMap {

    private static final String TAG = MyMap.class.getSimpleName();

    private final GoogleMap mMap;
    private final Activity activity;
    private final View view;

    private final FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;

    private Location lastKnownLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085); // Sydney

    private LatLng currentLatLng;
    private final Map<String, SportActivity> sportActivities = new HashMap<>();

    public Map<String, SportActivity> getSportActivities() {
        return sportActivities;
    }

    public MyMap(GoogleMap googleMap, Activity activity, View view) {
        mMap = googleMap;
        this.activity = activity;
        this.view = view;
        Log.d(TAG, view.toString());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        currentLatLng = defaultLocation;

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
                    SportActivity act = ds.getValue(SportActivity.class);
                    assert act != null;

                    String sport = act.getSport();
                    String description = act.getDescription();
                    String date = act.getDate();
                    String hour = act.getTime();
                    String uuidOrganiser = act.getUuidOrganiser();
                    String coords = act.getCoords();
                    ArrayList<String> uuids = (ArrayList<String>) act.getUuids();

                    SportActivity newActivity = new SportActivity(sport, description, date, hour, uuidOrganiser, coords);
                    newActivity.setUuids(uuids);

                    sportActivities.put(ds.getKey(), newActivity);
                }
                setLocationPoints();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view.getContext(), "database error",
                        Toast.LENGTH_SHORT).show();
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
                            currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            Log.d(TAG, "Location inside device location: " + currentLatLng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
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

                Utils.hideKeyboard(view.getContext(), view);

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
            currentLatLng =latLng;
        });

        if (icon != null) {
            mMap.addMarker(marker.position(currentLatLng).title("default").icon(icon));
        } else {
            mMap.addMarker(marker.position(currentLatLng).title("default"));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
    }

    //TODO: doesn't work with current position => callback in getDeviceLocation()
    public void drawRoute(LatLng origin, SportActivity activityDestination) {
        LatLng destination = Utils.stringToLatLng(activityDestination.getCoords());

        currentLatLng = origin;
        BitmapDescriptor icon = Utils.getBitmapDescriptor(activity, "Start");
        if (icon != null) {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("origin").icon(icon));
        } else {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("origin"));
        }

        //Define list to get all LatLng for the route
        List<LatLng> path = new ArrayList<>();

        //Execute Directions API request
        String apiKey = "";
        try {
            ApplicationInfo app = activity.getApplicationContext().getPackageManager().getApplicationInfo(activity.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        Log.d(TAG, Utils.latLngToString(origin));
        Log.d(TAG, Utils.latLngToString(destination));
        DirectionsApiRequest req = DirectionsApi.getDirections(context, Utils.latLngToString(origin), Utils.latLngToString(destination));
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs != null) {
                    drawRouteLegs(route, path);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Cannot get a route for these points");
            Toast.makeText(activity.getApplicationContext(), "Cannot get a route for these points", Toast.LENGTH_LONG).show();
        }

        //Draw the polyline
        if (!path.isEmpty()) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }
    }

    private void drawRouteLegs(DirectionsRoute route, List<LatLng> path){
        for (int i = 0; i < route.legs.length; i++) {
            DirectionsLeg leg = route.legs[i];
            if (leg.steps != null) {
                for (int j = 0; j < leg.steps.length; j++) {
                    DirectionsStep step = leg.steps[j];
                    drawRouteSteps(step, path);
                }
            }
        }
    }

    private void drawRouteSteps(DirectionsStep step, List<LatLng> path){
        if (step.steps != null && step.steps.length > 0) {
            for (int k = 0; k < step.steps.length; k++) {
                DirectionsStep step1 = step.steps[k];
                EncodedPolyline points = step1.polyline;
                if (points != null) {
                    addPointsToRouteCoordinates(points, path);
                }
            }
        } else {
            EncodedPolyline points = step.polyline;
            if (points != null) {
                addPointsToRouteCoordinates(points, path);
            }
        }
    }

    /**
     * Decode polyline and add points to list of route coordinates
     */
    private void addPointsToRouteCoordinates(EncodedPolyline points, List<LatLng> path){
        List<com.google.maps.model.LatLng> coords = points.decodePath();
        for (com.google.maps.model.LatLng coord : coords) {
            path.add(new LatLng(coord.lat, coord.lng));
        }
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

}
