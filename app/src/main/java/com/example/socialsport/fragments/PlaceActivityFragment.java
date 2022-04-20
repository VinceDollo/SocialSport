package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.utils.MyMap;
import com.example.socialsport.R;
import com.example.socialsport.databinding.FragmentPlaceActivityBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class PlaceActivityFragment extends Fragment implements OnMapReadyCallback {

    private String sport;
    private FragmentPlaceActivityBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaceActivityBinding.inflate(inflater);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            binding.tvTitle.setText("Choose location for " + sport);
        }

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.f_maps, new MapsFragment()).addToBackStack(null).commit();

        binding.btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).addToBackStack(null).commit());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MyMap map = new MyMap(googleMap, requireActivity(), requireView());
        map.searchPlaceListener();

        binding.btnValidate.setOnClickListener(view12 -> {
            //Add information to next fragment
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            bundle1.putString("location", String.valueOf(map.getCurrentLatLng()));
            Fragment newF = new DescriptionActivityFragment();
            newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        map.addActivityMarker(sport);
    }

}