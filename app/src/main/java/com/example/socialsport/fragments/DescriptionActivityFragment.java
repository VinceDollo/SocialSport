package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.Constants;
import com.example.socialsport.R;
import com.example.socialsport.Utils;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentDescriptionActivityBinding;
import com.example.socialsport.entities.SportActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class DescriptionActivityFragment extends Fragment {

    private String sport;
    private String coordinates;
    private String description;
    private String date;
    private String time;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private FragmentDescriptionActivityBinding binding;
    private FirebaseFirestore database;

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDescriptionActivityBinding.inflate(inflater);
        View view = binding.getRoot();

        database = FirebaseFirestore.getInstance();

        //recupere le sport
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            coordinates = bundle.getString("location");
        }

        setListeners();

        return view;
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void setListeners() {
        binding.btnBack.setOnClickListener(view12 -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            Fragment newF = new PlaceActivityFragment();
            newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        binding.etDate.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            datePicker = new DatePickerDialog(getActivity(), (view2, year1, monthOfYear, dayOfMonth) ->
                    binding.etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
            datePicker.show();
        });

        binding.etTime.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            timePicker = new TimePickerDialog(getActivity(), (tp, sHour, sMinute) ->
                    binding.etTime.setText(sHour + ":" + sMinute), hour, minutes, true);
            timePicker.show();
        });

        binding.tvDescription.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && motionEvent.getRawX() >= (binding.tvDescription.getRight() - binding.tvDescription.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DescriptionActivityFragment.this.getContext());
                builder.setCancelable(true);
                builder.setTitle("What should you describe ?");
                builder.setMessage("Here, you can describe the level required for the participants, and everything that will be useful for the people who wanted to join this activity");
                AlertDialog dialog = builder.create();
                dialog.show();
                view.performClick();
                return true;
            }
            return false;
        });

        binding.btnValidate.setOnClickListener(view1 -> {
            date =  Objects.requireNonNull(binding.etDate.getText()).toString();
            time =  Objects.requireNonNull(binding.etTime.getText()).toString();
            description =  Objects.requireNonNull(binding.etDescription.getText()).toString();
            String stringNumberParticipant =  Objects.requireNonNull(binding.etNumberParticipantRequired.getText()).toString();

            if (date.equals("") || time.equals("") || description.equals("") || stringNumberParticipant.equals("")) {
                Toast.makeText(getActivity(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            } else {
                Utils.writeActivityToDatabase(database, sport, description,date,time,coordinates,((PrincipalPageActivity) requireActivity()).getPreferenceManager().getString(Constants.KEY_USER_ID));
              /*  HashMap<String, Object> activityInfo = new HashMap<>();
                activityInfo.put(Constants.KEY_SPORT, sport);
                activityInfo.put(Constants.KEY_DESCRIPTION, description);
                activityInfo.put(Constants.KEY_DATE, date);
                activityInfo.put(Constants.KEY_TIME, time);
                activityInfo.put(Constants.KEY_COORDINATES, coordinates);
                activityInfo.put(Constants.KEY_USER_ID,((PrincipalPageActivity) requireActivity()).getPreferenceManager().getString(Constants.KEY_USER_ID));

                database.collection(Constants.KEY_COLLECTION_ACTIVITIES_NAME).add(activityInfo).addOnSuccessListener(documentReference -> {
                    SportActivity newActivity = new SportActivity(sport, description, date, time, ((PrincipalPageActivity) requireActivity()).getPreferenceManager().getString(Constants.KEY_USER_ID), coordinates);

                }).addOnFailureListener(exception -> Utils.toast(exception.getMessage(),getActivity()));*/

                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        });
    }

}