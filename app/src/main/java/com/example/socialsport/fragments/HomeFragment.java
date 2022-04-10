package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.MyMap;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.SportActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private Button btnAddActivity;
    private View view;
    private ScrollView scroll;
    private CircleImageView civProfile;
    private ImageView transparent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        // Change bottomBar when clicking on profile icon
        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(1, true);

        findViewById();

        btnAddActivity.setOnClickListener(view -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).addToBackStack(null).commit());
        civProfile.setOnClickListener(view1 -> {
            MeowBottomNavigation meowBottomNavigation = ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation();
            meowBottomNavigation.show(3, true);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new PersonFragment()).addToBackStack(null).commit();
        });

        // Allow vertical scroll in map fragment
        transparent.setOnTouchListener(this::mapOnTouchListener);

        return view;
    }

    private void findViewById() {
        civProfile = view.findViewById(R.id.civ_profil);
        btnAddActivity = view.findViewById(R.id.btn_add_activity);
        scroll = view.findViewById(R.id.scrollView);
        transparent = view.findViewById(R.id.imagetrans);
    }

    private boolean mapOnTouchListener(View v, MotionEvent event) {
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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MyMap map = new MyMap(googleMap, this.getActivity(), view);
        map.searchPlaceListener(); // Enable search location listener

        map.getmMap().setOnMarkerClickListener(marker -> {
            Bundle result = new Bundle();
            marker.hideInfoWindow();
            String title = (marker.getTitle());

            SportActivity clicked = map.getSportActivities().get(title);
            if (clicked != null) {
                Log.d("HomeFragment", clicked.getDescription());
                result.putString("sport", clicked.getSport());
                result.putString("activityID", title);
                result.putStringArrayList("participants", (ArrayList<String>) clicked.getUuids());
                result.putString("organiserUuid", clicked.getUuidOrganiser());
                result.putString("dateTime", clicked.getDate() + ", " + clicked.getHour());
                result.putString("location", clicked.getCoords());
                Fragment newF = new OverviewFragment();
                newF.setArguments(result);
                map.getmMap().animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                this.getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
            }
            return true;
        });
    }

}