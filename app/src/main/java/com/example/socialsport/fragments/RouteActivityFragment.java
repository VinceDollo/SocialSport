package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.databinding.FragmentRouteActivityBinding;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.utils.MyMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class RouteActivityFragment extends Fragment {

    private FragmentRouteActivityBinding binding;
    private SportActivity destinationActivity;

    private final OnMapReadyCallback callback = googleMap -> {
        MyMap myMap = new MyMap(googleMap, requireActivity(), requireView());
        Handler handler = new Handler();
        handler.postDelayed(() -> myMap.drawRoute(myMap.getCurrentLatLng(), destinationActivity), 1000); //TODO: try a better way => callback
    };

    private void btnBackListener() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String sport = bundle.getString("sport");
            String activityID = bundle.getString("activityID");
            ArrayList<String> participants = bundle.getStringArrayList("participants");
            String organiserUuid = bundle.getString("organiserUuid");
            String dateTime = bundle.getString("dateTime");
            String location = bundle.getString("location");
            destinationActivity = new SportActivity(sport, "", dateTime, "", organiserUuid, location);

            binding.btnBack.setOnClickListener(view1 -> {
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
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRouteActivityBinding.inflate(inflater);
        View view = binding.getRoot();

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.f_maps, new MapsFragment(callback)).addToBackStack(null).commit();

        binding.btnHome.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit());

        btnBackListener();

        return view;
    }

}