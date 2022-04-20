package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.utils.MyMap;
import com.example.socialsport.R;
import com.example.socialsport.entities.SportActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class RouteActivityFragment extends Fragment implements OnMapReadyCallback {

    private Button btnHome;
    private ImageButton btnBack;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_activity, container, false);

        btnHome = view.findViewById(R.id.btn_home);
        btnBack = view.findViewById(R.id.btn_back);

        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MyMap map = new MyMap(googleMap, requireActivity(), requireView());

        btnHome.setOnClickListener(view -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String sport = bundle.getString("sport");
            String activityID = bundle.getString("activityID");
            ArrayList<String> participants = bundle.getStringArrayList("participants");
            String organiserUuid = bundle.getString("organiserUuid");
            String dateTime = bundle.getString("dateTime");
            String location = bundle.getString("location");
            SportActivity activity = new SportActivity(sport, "", dateTime, "", organiserUuid, location);

            btnBack.setOnClickListener(view1 -> {
                Bundle result = new Bundle();
                result.putString("sport", sport);
                result.putString("activityID", activityID);
                result.putStringArrayList("participants", participants);
                result.putString("organiserUuid", organiserUuid);
                result.putString("dateTime", dateTime);
                result.putString("location", location);
                Fragment newF = new OverviewFragment();
                newF.setArguments(result);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
            });

            map.drawRoute(map.getCurrentLatLng(), activity); //TODO: need to wait map entirely created before calling it, currentLatLng is updated in a Listener
        }
    }
}