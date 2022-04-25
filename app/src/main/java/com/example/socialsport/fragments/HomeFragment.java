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
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentHomeBinding;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.MyMap;
import com.example.socialsport.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private View view;

    private MyMap map;
    private FragmentHomeBinding binding;

    private TableRow trSoccer;
    private TableRow trHand;
    private TableRow trBasket;
    private TableRow trVolley;
    private TextView tvAct;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        view = binding.getRoot();

        trSoccer = binding.activity1;
        trBasket = binding.activity2;
        trVolley = binding.activity3;
        trHand = binding.activity4;
        tvAct = binding.activities;

        MapsFragment mapsFragment = new MapsFragment(callback);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.f_maps, mapsFragment).addToBackStack(null).commit();

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(1, true);

        setListeners();

        User user = ((PrincipalPageActivity) requireActivity()).getUser();

        if (user.getImage() != null) {
            Log.d("Home", user.getImage());
            byte[] bytes = Base64.decode(user.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.civProfile.setImageBitmap(bitmap);
        } else {
            Log.d("Home", "Image null for this user");
        }

        return view;
    }

    private final OnMapReadyCallback callback = googleMap -> {
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
    };

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

        trSoccer.setOnClickListener(view -> getSpecificActivities("Soccer"));
        trBasket.setOnClickListener(view -> getSpecificActivities("Basket"));
        trHand.setOnClickListener(view -> getSpecificActivities("Hand"));
        trVolley.setOnClickListener(view -> getSpecificActivities("Volley"));

        tvAct.setOnClickListener(view -> map.getAllActivities());
    }

    private void getSpecificActivities(String sport){
        Map<String, SportActivity> activities = new HashMap<>();

        switch (sport){
            case "Soccer":
                activities = Utils.getSoccerActivities();
                break;
            case "Basket":
                activities = Utils.getBasketActivities();
                break;
            case "Hand":
                activities = Utils.getHandActivities();
                break;
            case "Volley":
                activities = Utils.getVolleyActivities();
                break;
            default:
                break;
        }

        map.getmMap().clear();
        map.setLocationPoints(activities);
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