package com.example.socialsport.Fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.entities.SportActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private GoogleMap mMap;
    private View view;

    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;

    private Location lastKnownLocation;
    private  List<SportActivity> activities = new ArrayList<>();

    public List<SportActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<SportActivity> activities) {
        this.activities = activities;
    }

    public String stringToLatLng(String string){
        String res = string.substring(string.indexOf("(")+1, string.indexOf(")"));
        return res;
    }

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085); // Sydney

    public void getAllActivities(GoogleMap mMap) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("activities");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Object> list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Object act = ds.getValue();    //Static types are wanky here
                    list.add(act);
                }
                Log.d("TAG", list.toString());
                setLocationPoints(list,mMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersRef.addValueEventListener(eventListener);
    }

    private void setLocationPoints(List<Object> activities, GoogleMap mMap) {

        for (Object activity : activities) {
            HashMap act=  (HashMap) activity;
            String description = (String)act.get("description");
            String date = (String)act.get("date");
            String hour = (String)act.get("hour");
            String uuidOrganiser = (String)act.get("uuidOrganiser");
            String coords = (String)act.get("coords");
            ArrayList<String> uuids = (ArrayList<String>)act.get("uuids");

            SportActivity current = new SportActivity(description,date,hour,uuidOrganiser,coords);
            current.setUuids(uuids);
            getActivities().add(current);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.img_tennis);   //TODO ADD ICON MANAGEMENT
            MarkerOptions marker = new MarkerOptions();
            Log.d("coords", stringToLatLng(current.getCoords()));
//            mMap.addMarker(marker.position(current.getCoords()).title("default").icon(icon));



        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);


        Button btn_add_activity = view.findViewById(R.id.btn_add_activity);
        btn_add_activity.setOnClickListener(view -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).addToBackStack(null).commit());

        // Allow vertical scroll in map fragment
        ScrollView scroll = view.findViewById(R.id.scrollView);
        ImageView transparent = view.findViewById(R.id.imagetrans);
        transparent.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    // Disallow ScrollView to intercept touch events.
                    scroll.requestDisallowInterceptTouchEvent(true);
                    // Disable touch on transparent view
                    return false;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    scroll.requestDisallowInterceptTouchEvent(false);
                    return true;
                default:
                    return true;
            }
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getAllActivities(mMap);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        searchPlaceListener();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(view.getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
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
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
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
                Geocoder gc = new Geocoder(getContext());
                try {
                    List<Address> addresses = gc.getFromLocationName(localisation, 1);
                    if (addresses.isEmpty())
                        Toast.makeText(getActivity(), "Error: the searched place doesn't exist", Toast.LENGTH_SHORT).show();
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

}