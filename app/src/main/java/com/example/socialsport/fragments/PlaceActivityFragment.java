package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.MyMap;
import com.example.socialsport.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class PlaceActivityFragment extends Fragment implements OnMapReadyCallback {
    private Button btnValidate;
    private String sport;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_activity, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        btnValidate = view.findViewById(R.id.btn_validate);
        ImageButton btnBack = view.findViewById(R.id.btn_back);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            tvTitle.setText("Choose location for " + sport);
        }

        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).addToBackStack(null).commit());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MyMap map = new MyMap(googleMap, requireActivity(), requireView());
        map.searchPlaceListener();

        btnValidate.setOnClickListener(view12 -> {
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