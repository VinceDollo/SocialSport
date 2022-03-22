package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.Utils;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewFragment extends Fragment {

    String tag = "OverviewFragment";

    View view;
    TableLayout tableLayout;
    ImageButton btnBack;
    CircleImageView imgSport;
    TextView nameSport;
    TextView nameOrganiser;
    TextView dateTime;
    TextView location;
    CircleImageView imgOrganiser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview_activity, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);
        btnBack = view.findViewById(R.id.btn_back);
        imgSport = view.findViewById(R.id.img_sport);
        nameSport = view.findViewById(R.id.name_sport);
        nameOrganiser = view.findViewById(R.id.name_organiser);
        dateTime = view.findViewById(R.id.tv_date_and_time);
        location = view.findViewById(R.id.tv_location);
        imgOrganiser = view.findViewById(R.id.img_organiser);

        btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String sport = bundle.getString("sport");
            imgSport.setImageBitmap(Utils.getBitmap(getActivity(), sport));
            nameSport.setText(sport);
            dateTime.setText(bundle.getString("dateTime"));
            location.setText(bundle.getString("location"));

            FirebaseDatabase.getInstance().getReference().child("users").child(bundle.getString("organiserUuid")).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(tag, "Error getting data", task.getException());
                } else {
                    Log.d(tag, String.valueOf(task.getResult().getValue()));
                    HashMap<String, String> organiser = (HashMap<String, String>) Objects.requireNonNull(task.getResult().getValue());
                    nameOrganiser.setText(Objects.requireNonNull(organiser.get("name")));
                }
            });

            ArrayList<String> participantsUuids = bundle.getStringArrayList("participants");
            Log.d(tag, participantsUuids.toString());
            for (String participantUuid : participantsUuids) {
                //TODO: find a better way to avoid attach a listener for each iteration
                FirebaseDatabase.getInstance().getReference().child("users").child(participantUuid).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(tag, "Error getting data", task.getException());
                    } else {
                        @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(this.getActivity()).inflate(R.layout.table_row, null);
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

        return view;
    }

}
