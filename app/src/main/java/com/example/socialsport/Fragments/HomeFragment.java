package com.example.socialsport.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        mMapFragment.getMapAsync(this);


        Button btn_add_activity = view.findViewById(R.id.btn_add_activity);
        btn_add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).commit();
            }
        });

        EditText et_localisation = (EditText) view.findViewById(R.id.et_search_city);
        et_localisation.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //Save the answer
                String localisation = et_localisation.getText().toString();
                Geocoder gc = new Geocoder(getContext());
                try {
                    List<Address> addresses = gc.getFromLocationName(localisation, 1);
                    double lattitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    Toast.makeText(getActivity(), lattitude + " " + longitude, Toast.LENGTH_SHORT).show();
                    LatLng placeToFind = new LatLng(lattitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(placeToFind).title("What you searched"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(placeToFind));

                } catch (IOException e) {
                    Log.d("TAG", e.toString());
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error place doesn't exist", Toast.LENGTH_SHORT).show();
                }
                //Delete text from edit text
                et_localisation.setText("");

                return true;
            }
            return false;
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

    }
}