package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentHomeBinding;
import com.example.socialsport.entities.User;

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
        if (user.getProfileImage() != null)
            binding.civProfile.setImageBitmap(user.getProfileImage());

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