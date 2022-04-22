package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentHomeBinding;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.PreferenceManager;
import com.example.socialsport.utils.TableKeys;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        View view = binding.getRoot();

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.f_maps, new MapsFragment()).addToBackStack(null).commit();

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(1, true);

        setListeners();

        User user = ((PrincipalPageActivity) requireActivity()).getUser();

        //TODO - refactorer
        PreferenceManager preferenceManager= new PreferenceManager(getActivity());
        if(preferenceManager.getString(TableKeys.USERS_IMAGE) != null){
            byte[] bytes = Base64.decode(preferenceManager.getString(TableKeys.USERS_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            binding.civProfile.setImageBitmap(bitmap);
        }else {
            Toast.makeText(getActivity(), "Image == null", Toast.LENGTH_SHORT).show();
        }

        return view;
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