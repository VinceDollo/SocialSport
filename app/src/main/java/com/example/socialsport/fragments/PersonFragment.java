package com.example.socialsport.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;

public class PersonFragment extends Fragment {

    private TextView tv_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText(((PrincipalPageActivity)getActivity()).getUser().getName());

        return view;
    }
}