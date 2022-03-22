package com.example.socialsport.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;

import com.example.socialsport.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddActivityFragment extends Fragment implements View.OnClickListener {

    private TableRow activity_soccer,activity_football,activity_volleyball, activity_basketball, activity_handball, activity_tennis, activity_running;
    private ImageButton btn_back;
    private CircleImageView civ_profil;
    private  View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_activity, container, false);

        findViewById();
        setOnClickListener();

        return view;
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("sport",view.getTag().toString());
        Fragment newF = new PlaceActivityFragment();
        newF.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
    }

    private void findViewById(){
        civ_profil = view.findViewById(R.id.civ_profil);
        activity_soccer = view.findViewById(R.id.add_activity_soccer);
        activity_football = view.findViewById(R.id.add_activity_football);
        activity_volleyball = view.findViewById(R.id.add_activity_volleyball);
        activity_basketball = view.findViewById(R.id.add_activity_basketball);
        activity_handball = view.findViewById(R.id.add_activity_handball);
        activity_tennis = view.findViewById(R.id.add_activity_tennis);
        activity_running = view.findViewById(R.id.add_activity_running);
        btn_back = view.findViewById(R.id.btn_back);
    }

    private void setOnClickListener(){
        activity_soccer.setOnClickListener(this); // calling onClick() method
        activity_football.setOnClickListener(this); // calling onClick() method
        activity_volleyball.setOnClickListener(this); // calling onClick() method
        activity_basketball.setOnClickListener(this); // calling onClick() method
        activity_handball.setOnClickListener(this); // calling onClick() method
        activity_tennis.setOnClickListener(this); // calling onClick() method
        activity_running.setOnClickListener(this); // calling onClick() method
        btn_back.setOnClickListener(view1 ->  {
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
        });
    }
}