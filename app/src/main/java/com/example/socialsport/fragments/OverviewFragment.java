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

import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewFragment extends Fragment {

    View view;
    TableLayout tableLayout;
    ImageButton btnBack;
    CircleImageView imgSport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview_activity, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);
        btnBack = view.findViewById(R.id.btn_back);
        imgSport = view.findViewById(R.id.img_sport);

        btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit());


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String sport = bundle.getString("sport");
            imgSport.setImageBitmap(Utils.getBitmap(getActivity(), sport));

            ArrayList<String> participantsUuids = bundle.getStringArrayList("participants");
            Log.d("OverviewFragment", participantsUuids.toString());
            for (String participantUuid : participantsUuids) {
                //TODO: find a better way to avoid attach a listener for each iteration
                FirebaseDatabase.getInstance().getReference().child("users").child(participantUuid).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("Firebase_user", "Error getting data", task.getException());
                    } else {
                        Log.d("Firebase_user", String.valueOf(task.getResult().getValue()));
                        HashMap participant = (HashMap) task.getResult().getValue();
                        assert participant != null;

                        @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(this.getActivity()).inflate(R.layout.table_row, null);
                        //TODO: add person image
                        Log.d("OverviewFragment", participant.toString());
                        ((TextView) row.findViewById(R.id.name_participant)).setText((String) participant.get("name"));
                        tableLayout.addView(row);
                    }
                });
            }
        }

        return view;
    }

}
