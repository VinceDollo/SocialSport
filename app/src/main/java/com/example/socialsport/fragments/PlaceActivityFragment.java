package com.example.socialsport.Fragments;

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

import com.example.socialsport.Map;
import com.example.socialsport.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class PlaceActivityFragment extends Fragment implements OnMapReadyCallback {

    private ImageButton btn_back;
    private Button btn_validate;
    private TextView tv_title;

    private LatLng current_latLng;
    private String sport;

    public PlaceActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_activity, container, false);

        tv_title = view.findViewById(R.id.tv_title);
        btn_validate = view.findViewById(R.id.btn_validate);

        //recupere le sport
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            tv_title.setText("Choose location for " + sport);
        }

        //Placer la map
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).commit());
        btn_validate.setOnClickListener(view12 -> {
            //add information to next fragment
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            bundle1.putString("location", String.valueOf(current_latLng)); // TODO: problème ici, la localisation n'est pas encore donnée
            Fragment newF = new DescriptionActivityFragment();
            newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Map map = new Map(googleMap, requireActivity(), requireView());
        map.addActivityMarker(sport);
    }

}