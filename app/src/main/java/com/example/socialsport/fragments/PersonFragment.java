package com.example.socialsport.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsport.R;
import com.example.socialsport.activities.LoginActivity;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.activities.WelcomeActivity;
import com.example.socialsport.entities.SportActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonFragment extends Fragment {

    private Button btn_disc;
    private FirebaseAuth mAuth;
    private LinearLayout llactivities;
    private CircleImageView civ_profil;
    private TextView tv_name,tvfinishedAct;
    private int count;
    private final ArrayList<SportActivity> sportActivities = new ArrayList<>();
    private final ArrayList<SportActivity> myActivities = new ArrayList<>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(3,true);

        tv_name = view.findViewById(R.id.tv_name);
        tvfinishedAct=view.findViewById(R.id.finishedactivities);
        civ_profil = view.findViewById(R.id.civ_profil);
        llactivities=view.findViewById(R.id.llactivitiefinished);

        civ_profil.setOnClickListener(view1 -> {
            mGetContent.launch("image/*");
        });

        tv_name.setText(((PrincipalPageActivity) requireActivity()).getUser().getName());
        Log.d("firebase", "" + ((PrincipalPageActivity) requireActivity()).getUser().getName());
        btn_disc = view.findViewById(R.id.btn_disconnect);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllActivities();

        btn_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                getActivity().onBackPressed();
            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    civ_profil.setImageURI(uri);
                }
    });

    public void getAllActivities() {
        System.out.println("traitement Act");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activitiesRef = rootRef.child("activities");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap act = (HashMap) ds.getValue(); //Static types are wanky here
                    assert act != null;
                    Log.d("Firebase_activity", act.toString());

                    String sport = (String) act.get("sport");
                    String description = (String) act.get("description");
                    String date = (String) act.get("date");
                    String hour = (String) act.get("hour");
                    String uuidOrganiser = (String) act.get("uuidOrganiser");
                    String coords = (String) act.get("coords");
                    ArrayList<String> uuids = (ArrayList<String>) act.get("uuids");

                    SportActivity newActivity = new SportActivity(sport, description, date, hour, uuidOrganiser, coords);
                    newActivity.setUuids(uuids);
                    sportActivities.add(newActivity);
                }
                Log.d("meziane",sportActivities.toString());
                getMyActivities();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //
            }
        };
        activitiesRef.addValueEventListener(eventListener);
    }

    public void getMyActivities() {
        System.out.println("traitement get my Act");
        for(SportActivity currentAct : sportActivities){
            ArrayList<String> uids = currentAct.getUuids();
            Log.d("verification",uids.toString()+" kkk"+mAuth.getCurrentUser().getUid());
            if (uids.contains(mAuth.getCurrentUser().getUid().toString())){
                System.out.println("correct");
                myActivities.add(currentAct);
            }
        }
        Log.d("mezianeactiv",myActivities.toString());    

        for(SportActivity currentAct : myActivities){
            TextView tv = new TextView(PersonFragment.this.getContext());
            tv.setText("Activity : "+currentAct.getSport() +" ("+currentAct.getDate()+", "
            +currentAct.getCoords()+")");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            tv.setLayoutParams(params);
            llactivities.addView(tv);
        }
    }
}