package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.utils.Utils;
import com.example.socialsport.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DescriptionActivityFragment extends Fragment {
    private EditText etDescription;
    private EditText etTime;
    private EditText etDate;
    private EditText etNumberOfParticipant;
    private TextView tvDescription;
    private ImageButton btnBack;
    private Button btnValidate;
    private String sport;
    private String coordinates;
    private String description;
    private String date;
    private String time;
    private FirebaseAuth mAuth;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void setListeners() {
        btnBack.setOnClickListener(view12 -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            Fragment newF = new PlaceActivityFragment();
            newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        etDate.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            datePicker = new DatePickerDialog(getActivity(), (view2, year1, monthOfYear, dayOfMonth) ->
                    etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
            datePicker.show();
        });

        etTime.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            timePicker = new TimePickerDialog(getActivity(), (tp, sHour, sMinute) ->
                    etTime.setText(sHour + ":" + sMinute), hour, minutes, true);
            timePicker.show();
        });

        tvDescription.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && motionEvent.getRawX() >= (tvDescription.getRight() - tvDescription.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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

        etNumberOfParticipant.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && allFieldsFilled()) {
                // Perform action on key press
                btnValidate.performClick();
                Utils.hideKeyboard(this.requireContext(), this.requireView());
                return true;
            }
            return false;
        });

        btnValidate.setOnClickListener(view1 -> {
            date = etDate.getText().toString();
            time = etTime.getText().toString();
            description = etDescription.getText().toString();

            if (!allFieldsFilled()) {
                Toast.makeText(getActivity(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            } else {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                if (currentUser == null) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(getActivity(), "Sorry, you need to log back in to your account",
                            Toast.LENGTH_SHORT).show();
                }

                if (currentUser != null) {
                    Utils.writeActivityToDatabase(database, sport, description, date, time, coordinates, currentUser.getUid());
                }
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        });
    }

    private boolean allFieldsFilled() {
        return etDescription.getText().length() > 0 && etDate.getText().length() > 0 && etTime.getText().length() > 0 && etNumberOfParticipant.getText().length() > 0;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description_activity, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            coordinates = bundle.getString("location");
        }

        btnBack = view.findViewById(R.id.btn_back);
        btnValidate = view.findViewById(R.id.btn_validate);
        etTime = view.findViewById(R.id.et_time);
        etDate = view.findViewById(R.id.et_date);
        etDescription = view.findViewById(R.id.et_description);
        etNumberOfParticipant = view.findViewById(R.id.et_number_participant_required);
        tvDescription = view.findViewById(R.id.tv_description);

        setListeners();

        return view;
    }
}