package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
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
import com.example.socialsport.activities.LoginActivity;
import com.example.socialsport.entities.SportActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DescriptionActivityFragment extends Fragment {

    private ImageButton btn_back;
    private Button btn_validate;
    private EditText et_description;
    private EditText et_time;
    private EditText et_date;
    private EditText et_number_of_participant;
    private TextView tv_info_description;
    private TextView tv_description;
    private String sport;
    private String coordinates;
    private String description;
    private String date;
    private String time;
    private int number_participants;
    private FirebaseAuth mAuth;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private boolean isIconClicked = false;

    private void writeActivityToDatabase(FirebaseDatabase database, String sport, String description, String date, String heure, String coords, String currentUserID) {
        DatabaseReference myRef = database.getReference();
        SportActivity newActivity = new SportActivity(sport, description, date, heure, currentUserID, coords);
        myRef.child("activities").push().setValue(newActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description_activity, container, false);

        //recupere le sport
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            coordinates = bundle.getString("location");
        }

        btn_back = view.findViewById(R.id.btn_back);
        btn_validate = view.findViewById(R.id.btn_validate);
        et_time = view.findViewById(R.id.et_time);
        et_date = view.findViewById(R.id.et_date);
        et_description = view.findViewById(R.id.et_description);
        et_number_of_participant = view.findViewById(R.id.et_number_participant_required);
        et_number_of_participant = view.findViewById(R.id.et_number_participant_required);
        tv_info_description = view.findViewById(R.id.info_description);
        tv_description = view.findViewById(R.id.tv_description);

        btn_back.setOnClickListener(view12 -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            Fragment newF = new PlaceActivityFragment();
            newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        et_date.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            datePicker = new DatePickerDialog(getActivity(), (view2, year1, monthOfYear, dayOfMonth) ->
                    et_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
            datePicker.show();
        });

        et_time.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            timePicker = new TimePickerDialog(getActivity(), (tp, sHour, sMinute) ->
                    et_time.setText(sHour + ":" + sMinute), hour, minutes, true);
            timePicker.show();
        });

        tv_description.setOnTouchListener((view13, motionEvent) -> {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if(motionEvent.getRawX() >= (tv_description.getRight() - tv_description.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if(isIconClicked){
                                tv_info_description.setText("");
                                isIconClicked = false;
                            }else{
                                tv_info_description.setText("Enter a short description about your activity, the level of the participants and every informations that could be useful for participants");
                                isIconClicked = true;
                            }
                            return true;
                        }
                    }
                    return false;
        });

        btn_validate.setOnClickListener(view1 -> {
            date = et_date.getText().toString();
            time = et_time.getText().toString();
            description = et_description.getText().toString();
            String string_number_participant = et_number_of_participant.getText().toString();

            if (date.equals("") || time.equals("") || description.equals("") || string_number_participant.equals("")) {
                Toast.makeText(getActivity(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            } else {
                number_participants = Integer.parseInt(string_number_participant);
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
                    writeActivityToDatabase(database, sport, description, date, time, coordinates, currentUser.getUid());
                }
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }
}