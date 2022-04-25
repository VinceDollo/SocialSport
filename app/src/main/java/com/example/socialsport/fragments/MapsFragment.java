package com.example.socialsport.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialsport.utils.MyMap;
import com.example.socialsport.R;
import com.example.socialsport.entities.SportActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    private final OnMapReadyCallback callback;

    public MapsFragment() {
        this.callback = googleMap -> {
            MyMap myMap = new MyMap(googleMap, requireActivity(), requireView());

            myMap.searchPlaceListener(); // Enable search location listener

            myMap.getmMap().setOnMarkerClickListener(marker -> {
                Log.d("CLIQUER","CLIQUE1");
                marker.hideInfoWindow();
                myMap.getmMap().animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                String title = (marker.getTitle());
                SportActivity clicked = myMap.getSportActivities().get(title);

                Bundle result = new Bundle();
                if (clicked != null) {
                    Log.d("CLIQUER","CLIQUE2");
                    result.putString("sport", clicked.getSport());
                    result.putString("activityID", title);
                    result.putStringArrayList("participants", (ArrayList<String>) clicked.getUuids());
                    result.putString("organiserUuid", clicked.getUuidOrganiser());
                    result.putString("dateTime", clicked.getDate() + ", " + clicked.getTime());
                    result.putString("location", clicked.getCoords());

                    Fragment newF = new OverviewFragment();
                    newF.setArguments(result);
                    this.getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
                }
                return true;
            });
        };
    }

    public MapsFragment(OnMapReadyCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}