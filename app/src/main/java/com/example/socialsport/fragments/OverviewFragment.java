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
import com.example.socialsport.utils.Utils;
import com.example.socialsport.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewFragment extends Fragment {

    private static final String TAG = OverviewFragment.class.getSimpleName();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private TableLayout tableLayout;
    private Button btnParticipate;
    private CircleImageView imgSport;
    private TextView nameSport;
    private TextView nameOrganiser;
    private TextView dateTime;
    private TextView location;
    private CircleImageView imgOrganiser;
    private ArrayList<String> participantsUuids = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview_activity, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnParticipate = view.findViewById(R.id.btn_participate);
        imgSport = view.findViewById(R.id.img_sport);
        nameSport = view.findViewById(R.id.name_sport);
        nameOrganiser = view.findViewById(R.id.name_organiser);
        dateTime = view.findViewById(R.id.tv_date_and_time);
        location = view.findViewById(R.id.tv_location);
        imgOrganiser = view.findViewById(R.id.img_organiser);

        btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit());

        setViewContent();

        return view;
    }

    private void stateButton() {
        if (participantsUuids.contains(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()))
            btnParticipate.setText(R.string.leave);
        else
            btnParticipate.setText(R.string.participate);
    }

    private void queryDbUsers() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activitiesRef = rootRef.child("activities");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tableLayout.removeAllViews();
                if (!participantsUuids.isEmpty()) {
                    imgOrganiser.setImageResource(R.drawable.img_person);
                    nameOrganiser.setText(R.string.app_name); //TODO: get organiser's name
                    for (String participantUuid : participantsUuids) {
                        addParticipantView(participantUuid);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        };
        activitiesRef.addValueEventListener(eventListener);
    }

    private void addParticipantView(String participantUuid) {
        FirebaseDatabase.getInstance().getReference().child("users").child(participantUuid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.table_row, null);
                User participant = task.getResult().getValue(User.class);
                if (participant != null) {
                    Log.d(TAG, participant.toString());
                    ((CircleImageView) row.findViewById(R.id.img_participant)).setImageResource(R.drawable.img_person); //TODO: add person image
                    ((TextView) row.findViewById(R.id.name_participant)).setText(participant.getName());
                    tableLayout.addView(row);
                }
            }
        });
    }

    private void handleParticipate(String activityID) {
        FirebaseDatabase.getInstance().getReference().child("activities").child(activityID).child("uuids").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                Log.d(TAG, Objects.requireNonNull(task.getResult().getValue()).getClass().toString());
                ArrayList<String> currentParticipants = (ArrayList<String>) task.getResult().getValue();
                if (!currentParticipants.contains(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    currentParticipants.add(mAuth.getCurrentUser().getUid());
                } else {
                    currentParticipants.remove(mAuth.getCurrentUser().getUid());
                    if (currentParticipants.isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("activities").child(activityID).setValue(currentParticipants);
                    }
                }
                participantsUuids = currentParticipants;
                stateButton();
                FirebaseDatabase.getInstance().getReference().child("activities").child(activityID).child("uuids").setValue(currentParticipants);
            }
        });
    }

    public void setViewContent() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            participantsUuids = bundle.getStringArrayList("participants");
            stateButton();
            queryDbUsers();

            btnParticipate.setOnClickListener(view -> handleParticipate(bundle.getString("activityID")));

            String sport = bundle.getString("sport");
            imgSport.setImageBitmap(Utils.getBitmap(getActivity(), sport));
            nameSport.setText(sport);
            dateTime.setText(bundle.getString("dateTime"));
            location.setText(Utils.getPrintableLocation(getActivity(), bundle.getString("location")));
        }
    }

}
