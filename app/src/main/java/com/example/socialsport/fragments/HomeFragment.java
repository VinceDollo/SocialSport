package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.MyMap;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentHomeBinding;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private View view;
    //ajout mez
    private MyMap map;
    private FragmentHomeBinding binding;

    //ajout mez
    private TableRow trsoccer,trhand,trbasket,trvolley;
    private TextView tvact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        view = binding.getRoot();
        //ajout mez
        trsoccer = binding.activity1;
        trbasket = binding.activity2;
        trvolley = binding.activity3;
        trhand = binding.activity4;
        tvact= binding.activities;

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(1, true);

        setListeners();

        User user = ((PrincipalPageActivity) requireActivity()).getUser();
        if (user.getProfileImage() != null)
            binding.civProfile.setImageBitmap(user.getProfileImage());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Ajout mez
        map = new MyMap(googleMap, requireActivity(), view);

        map.searchPlaceListener(); // Enable search location listener

        map.getmMap().setOnMarkerClickListener(marker -> {
            Bundle result = new Bundle();
            marker.hideInfoWindow();
            String title = (marker.getTitle());

            SportActivity clicked = map.getSportActivities().get(title);
            if (clicked != null) {
                Log.d(TAG, clicked.getDescription());
                result.putString("sport", clicked.getSport());
                result.putString("activityID", title);
                result.putStringArrayList("participants", (ArrayList<String>) clicked.getUuids());
                result.putString("organiserUuid", clicked.getUuidOrganiser());
                result.putString("dateTime", clicked.getDate() + ", " + clicked.getTime());
                result.putString("location", clicked.getCoords());
                Fragment newF = new OverviewFragment();
                newF.setArguments(result);
                map.getmMap().animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                this.getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
            }
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        binding.btnAddActivity.setOnClickListener(view -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).addToBackStack(null).commit());

        binding.civProfile.setOnClickListener(view1 -> {
            MeowBottomNavigation meowBottomNavigation = ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation();
            meowBottomNavigation.show(3, true);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new PersonFragment()).addToBackStack(null).commit();
        });

        // Allow vertical scroll in map fragment
        binding.transparentImage.setOnTouchListener(this::mapOnTouchListener);

        //ajout mez
        trsoccer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, SportActivity> activities = Utils.getSocceractivities();
                Toast.makeText(view.getContext(), activities.toString(), Toast.LENGTH_SHORT).show();
                map.clearMap();
                map.setLocationPoints(activities);
            }
        });
        trbasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, SportActivity> activities = Utils.getBasketactivities();
                Toast.makeText(view.getContext(), activities.toString(), Toast.LENGTH_SHORT).show();
                map.clearMap();
                map.setLocationPoints(activities);
            }
        });
        trhand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, SportActivity> activities = Utils.getHandactivities();
                Toast.makeText(view.getContext(), activities.toString(), Toast.LENGTH_SHORT).show();
                map.clearMap();
                map.setLocationPoints(activities);
            }
        });
        trvolley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, SportActivity> activities = Utils.getVolleyactivities();
                Toast.makeText(view.getContext(), activities.toString(), Toast.LENGTH_SHORT).show();
                map.clearMap();
                map.setLocationPoints(activities);
            }
        });
        tvact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getAllActivities();
            }
        });
    }

    private boolean mapOnTouchListener(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Disallow ScrollView to intercept touch events.
                binding.scrollView.requestDisallowInterceptTouchEvent(true);
                // Disable touch on transparent view
                return false;
            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                binding.scrollView.requestDisallowInterceptTouchEvent(false);
                return true;
            default:
                return true;
        }
    }

}