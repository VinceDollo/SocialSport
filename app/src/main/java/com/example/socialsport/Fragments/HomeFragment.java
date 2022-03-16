package com.example.socialsport.Fragments;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        // Allow vertical scroll in map fragment
        ScrollView scroll = (ScrollView) view.findViewById(R.id.scrollView);
        ImageView transparent = (ImageView) view.findViewById(R.id.imagetrans);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        searchPlaceListener();
    }

    public void searchPlaceListener() {
        // Create searching localisation method
        EditText et_localisation = (EditText) view.findViewById(R.id.et_search_city);
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
                    Log.d("Place_error", e.toString());
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