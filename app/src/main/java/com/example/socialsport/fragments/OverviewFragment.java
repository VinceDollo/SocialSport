package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialsport.DatabaseUtils;
import com.example.socialsport.R;
import com.example.socialsport.entities.User;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OverviewFragment extends Fragment {

    View view;
    TableLayout tableLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview_activity, container, false);
        tableLayout = view.findViewById(R.id.tableLayout);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ArrayList<String> participantsUuids = bundle.getStringArrayList("participants");
            Log.d("Clicked", participantsUuids.toString());
            for (String participantUuid : participantsUuids) {
                @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(this.getActivity()).inflate(R.layout.table_row, null);
                //TODO: add person image
                //String participant = DatabaseUtils.getUserFromDatabase(FirebaseDatabase.getInstance(), participantUuid);
                //Log.d("Clicked", participant);
                //((TextView) row.findViewById(R.id.name_participant)).setText(participant);
                tableLayout.addView(row);
            }
        }

        return view;
    }
}
