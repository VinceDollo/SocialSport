package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
    ArrayList<String> participantsUuids = new ArrayList<>();

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
                Log.d(tag, participantsUuids.toString());
                for (String participantUuid : participantsUuids) {
                    //TODO: error while displaying name of a new participant
                    FirebaseDatabase.getInstance().getReference().child("users").child(participantUuid).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(tag, "Error getting data", task.getException());
                        } else {
                            @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.table_row, null);
                            HashMap<String, String> participant = (HashMap<String, String>) Objects.requireNonNull(task.getResult().getValue());
                            //TODO: add person image
                            Log.d(tag, participant.toString());
                            Log.d(tag, "");
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
                Log.d(tag, ">" + Objects.requireNonNull(task.getResult().getValue()).getClass());
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

    public void setViewContent(){
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

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            LatLng locationLatLng = Utils.stringToLatLng(bundle.getString("location"));
            StringBuilder locationString = new StringBuilder();
            try {
                Address locationAddress = geocoder.getFromLocation(locationLatLng.latitude, locationLatLng.longitude, 1).get(0);
                int i = 0;
                while (locationAddress.getAddressLine(i) != null) {
                    locationString.append(locationAddress.getAddressLine(i));
                    locationString.append(", ");
                    i++;
                }
                locationString.delete(locationString.length() - 2, locationString.length() - 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            location.setText(locationString);
        }
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

        setViewContent();

        return view;
    }


}
