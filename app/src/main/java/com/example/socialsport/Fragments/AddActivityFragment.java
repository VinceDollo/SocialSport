package com.example.socialsport.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.socialsport.R;

public class AddActivityFragment extends Fragment implements View.OnClickListener {

    private TableRow activity_soccer,activity_football,activity_volleyball, activity_basketball, activity_handball, activity_tennis, activity_running;
    private ImageButton btn_back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_add_activity, container, false);
        activity_soccer = view.findViewById(R.id.add_activity_soccer);
        activity_soccer.setOnClickListener(this); // calling onClick() method

        activity_football = view.findViewById(R.id.add_activity_football);
        activity_football.setOnClickListener(this); // calling onClick() method

        activity_volleyball = view.findViewById(R.id.add_activity_volleyball);
        activity_volleyball.setOnClickListener(this); // calling onClick() method

        activity_basketball = view.findViewById(R.id.add_activity_basketball);
        activity_basketball.setOnClickListener(this); // calling onClick() method

        activity_handball = view.findViewById(R.id.add_activity_handball);
        activity_handball.setOnClickListener(this); // calling onClick() method

        activity_tennis = view.findViewById(R.id.add_activity_tennis);
        activity_tennis.setOnClickListener(this); // calling onClick() method

        activity_running = view.findViewById(R.id.add_activity_running);
        activity_running.setOnClickListener(this); // calling onClick() method

        btn_back = view.findViewById(R.id.btn_back);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("sport",view.getTag().toString());
        Fragment newF = new PlaceActivityFragment();
        newF.setArguments(bundle);
        //Toast.makeText(getActivity(), view.getTag().toString(), Toast.LENGTH_SHORT).show();
        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();

    }
}