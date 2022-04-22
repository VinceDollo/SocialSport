package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.databinding.FragmentPlaceActivityBinding;
import com.example.socialsport.utils.PreferenceManager;
import com.example.socialsport.utils.TableKeys;
import com.google.android.gms.maps.GoogleMap;
import com.example.socialsport.utils.MyMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class PlaceActivityFragment extends Fragment {

    private String sport;
    private FragmentPlaceActivityBinding binding;

    private final OnMapReadyCallback callback = googleMap -> {
        MyMap myMap = new MyMap(googleMap, requireActivity(), requireView());

        myMap.addActivityMarker(sport);

        binding.btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).addToBackStack(null).commit());

        binding.btnValidate.setOnClickListener(view1 -> {
            //Add information to next fragment
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            bundle1.putString("location", String.valueOf(myMap.getCurrentLatLng()));
            Fragment newF = new DescriptionActivityFragment();
            newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });
    };

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaceActivityBinding.inflate(inflater);
        View view = binding.getRoot();

        PreferenceManager preferenceManager= new PreferenceManager(getActivity());

        if(preferenceManager.getString(TableKeys.USERS_IMAGE) != null){
            byte[] bytes = Base64.decode(preferenceManager.getString(TableKeys.USERS_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            binding.civProfile.setImageBitmap(bitmap);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            binding.tvTitle.setText("Choose location for " + sport);
        }

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.f_maps, new MapsFragment(callback)).addToBackStack(null).commit();

        return view;
    }

}