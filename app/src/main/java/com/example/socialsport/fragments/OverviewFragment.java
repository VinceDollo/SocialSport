package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.Utils;
import com.example.socialsport.entities.User;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewFragment extends Fragment {

    String tag = "OverviewFragment";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    View view;
    TableLayout tableLayout;
    ImageButton btnBack;
    Button btnParticipate;
    CircleImageView imgSport;
    TextView nameSport;
    TextView nameOrganiser;
    TextView dateTime;
    TextView location;
    CircleImageView imgOrganiser;

    private void queryDbUsers(Bundle bundle) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activitiesRef = rootRef.child("activities");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> participantsUuids = bundle.getStringArrayList("participants");
                Log.d(tag, participantsUuids.toString());
                for (String participantUuid : participantsUuids) {
                    //TODO: error while displaying name of a new participant
                    //TODO: find a better way to avoid attach a listener for each iteration
                    FirebaseDatabase.getInstance().getReference().child("users").child(participantUuid).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(tag, "Error getting data", task.getException());
                        } else {
                            @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.table_row, null);
                            Log.d(tag, String.valueOf(task.getResult().getValue()));
                            HashMap<String, String> participant = (HashMap<String, String>) Objects.requireNonNull(task.getResult().getValue());
                            //TODO: add person image
                            Log.d(tag, participant.toString());
                            ((TextView) row.findViewById(R.id.name_participant)).setText(participant.get("name"));
                            tableLayout.addView(row);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //
            }
        };
        activitiesRef.addValueEventListener(eventListener);
    }

    private void handleParticipate(String activityID) {
        FirebaseDatabase.getInstance().getReference().child("activities").child(activityID).child("uuids").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(tag, "Error getting data", task.getException());
            } else {
                Log.d(tag, "" + task.getResult().getValue().getClass());
                ArrayList<String> currentParticipants = (ArrayList<String>) task.getResult().getValue();
                if (!currentParticipants.contains(mAuth.getCurrentUser().getUid())) {
                    currentParticipants.add(mAuth.getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference().child("activities").child(activityID).child("uuids").setValue(currentParticipants);
                } else {
                    currentParticipants.remove(mAuth.getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference().child("activities").child(activityID).child("uuids").setValue(currentParticipants);
                }
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview_activity, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);
        btnBack = view.findViewById(R.id.btn_back);
        btnParticipate = view.findViewById(R.id.btn_participate);
        imgSport = view.findViewById(R.id.img_sport);
        nameSport = view.findViewById(R.id.name_sport);
        nameOrganiser = view.findViewById(R.id.name_organiser);
        dateTime = view.findViewById(R.id.tv_date_and_time);
        location = view.findViewById(R.id.tv_location);
        imgOrganiser = view.findViewById(R.id.img_organiser);


        btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit());


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            queryDbUsers(bundle);

            btnParticipate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleParticipate(bundle.getString("activityID"));
                }
            });
            ;


            String sport = bundle.getString("sport");
            imgSport.setImageBitmap(Utils.getBitmap(getActivity(), sport));
            nameSport.setText(sport);
            dateTime.setText(bundle.getString("dateTime"));
            location.setText(bundle.getString("location"));
        }

        return view;
    }


}
