package com.example.socialsport.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;

import androidx.fragment.app.Fragment;

import com.example.socialsport.R;

public class AddActivityFragment extends Fragment implements View.OnClickListener {

    private TableRow activitySoccer;
    private TableRow activityFootball;
    private TableRow activityVolleyball;
    private TableRow activityBasketball;
    private TableRow activityHandball;
    private TableRow activityTennis;
    private TableRow activityRunning;
    private ImageButton btnBack;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_activity, container, false);

        findViewById();
        setOnClickListener();

        return view;
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("sport", view.getTag().toString());
        Fragment newF = new PlaceActivityFragment();
        newF.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
    }

    private void findViewById() {
        activitySoccer = view.findViewById(R.id.add_activity_soccer);
        activityFootball = view.findViewById(R.id.add_activity_football);
        activityVolleyball = view.findViewById(R.id.add_activity_volleyball);
        activityBasketball = view.findViewById(R.id.add_activity_basketball);
        activityHandball = view.findViewById(R.id.add_activity_handball);
        activityTennis = view.findViewById(R.id.add_activity_tennis);
        activityRunning = view.findViewById(R.id.add_activity_running);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void setOnClickListener() {
        activitySoccer.setOnClickListener(this); // calling onClick() method
        activityFootball.setOnClickListener(this); // calling onClick() method
        activityVolleyball.setOnClickListener(this); // calling onClick() method
        activityBasketball.setOnClickListener(this); // calling onClick() method
        activityHandball.setOnClickListener(this); // calling onClick() method
        activityTennis.setOnClickListener(this); // calling onClick() method
        activityRunning.setOnClickListener(this); // calling onClick() method
        btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit());
    }
}