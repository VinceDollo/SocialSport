package com.example.socialsport.Fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.Map;
import com.example.socialsport.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

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
        Map map = new Map(googleMap, this.getActivity(), view);
        map.searchPlaceListener(); // Enable search location listener
    }

}