package com.example.socialsport.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialsport.R;
import com.example.socialsport.activities.LoginActivity;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.activities.WelcomeActivity;
import com.google.firebase.database.FirebaseDatabase;

public class PersonFragment extends Fragment {

    private TextView tv_name;
    private Button btn_disc;

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
        tv_name.setText(((PrincipalPageActivity) getActivity()).getUser().getName());
        Log.d("firebase", "" + ((PrincipalPageActivity) getActivity()).getUser().getName());
        btn_disc = view.findViewById(R.id.btn_disconnect);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().goOffline();
                Intent IntentLoginActivity = new Intent(PersonFragment.this.getContext(), WelcomeActivity.class);
                startActivity(IntentLoginActivity);
            }
        });
    }
}