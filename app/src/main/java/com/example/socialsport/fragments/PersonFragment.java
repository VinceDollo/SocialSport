package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.Utils;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.SportActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonFragment extends Fragment {

    private Button btnDisc;
    private FirebaseAuth mAuth;
    private LinearLayout llActivities;
    private CircleImageView civProfile;
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

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(3, true);

        TextView tvName = view.findViewById(R.id.tv_name);
        civProfile = view.findViewById(R.id.civ_profil);
        llActivities = view.findViewById(R.id.llactivitiefinished);

        civProfile.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        tvName.setText(((PrincipalPageActivity) requireActivity()).getUser().getName());
        Log.d("firebase", "" + ((PrincipalPageActivity) requireActivity()).getUser().getName());
        btnDisc = view.findViewById(R.id.btn_disconnect);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllActivities();

        btnDisc.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
            requireActivity().onBackPressed();
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> civProfile.setImageURI(uri));

    public void getAllActivities() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activitiesRef = rootRef.child("activities");

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SportActivity act = ds.getValue(SportActivity.class);
                    assert act != null;
                    Log.d("Firebase_activity", act.toString());

                    String sport = act.getSport();
                    String description = act.getDescription();
                    String date = act.getDate();
                    String hour = act.getHour();
                    String uuidOrganiser = act.getUuidOrganiser();
                    String coords = act.getCoords();
                    ArrayList<String> uuids = (ArrayList<String>) act.getUuids();

                    SportActivity newActivity = new SportActivity(sport, description, date, hour, uuidOrganiser, coords);
                    newActivity.setUuids(uuids);
                    sportActivities.add(newActivity);
                }
                getMyActivities();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void getMyActivities() {
        for (SportActivity currentAct : sportActivities) {
            ArrayList<String> uids = (ArrayList<String>) currentAct.getUuids();
            if (!uids.isEmpty()) {
                Log.d("verification", uids + " |||" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                if (uids.contains(mAuth.getCurrentUser().getUid())) {
                    myActivities.add(currentAct);
                }
            }
        }

        int cmp = 0;
        for (SportActivity currentAct : myActivities) {
            TextView tv = new TextView(PersonFragment.this.getContext());
            tv.setText(currentAct.getSport() + " : " + currentAct.getDate() + "\n"
                    + Utils.getPrintableLocation(getActivity(), currentAct.getCoords()));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (cmp != 0) {
                params.setMargins(0, 30, 0, 0);
            }

            cmp++;

            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.btn_finished_activities);
            tv.setPadding(20, 30, 20, 30);
            tv.setGravity(Gravity.CENTER);
            llActivities.addView(tv);
        }
    }
}