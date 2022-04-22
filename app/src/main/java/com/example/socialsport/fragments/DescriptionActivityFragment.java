package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.activities.LoginActivity;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentDescriptionActivityBinding;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.TableKeys;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DescriptionActivityFragment extends Fragment {

    private String sport;
    private String coordinates;
    private String description;
    private String date;
    private String time;
    private FirebaseAuth mAuth;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private FragmentDescriptionActivityBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDescriptionActivityBinding.inflate(inflater);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            coordinates = bundle.getString("location");
        }

        User user = ((PrincipalPageActivity) requireActivity()).getUser();


        //TODO - refactorer
        if(user.getImage() != null){
            byte[] bytes = Base64.decode(user.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            binding.civProfile.setImageBitmap(bitmap);
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

        binding.etNumberParticipantRequired.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && allFieldsFilled()) {
                // Perform action on key press
                binding.btnValidate.performClick();
                Utils.hideKeyboard(this.requireContext(), this.requireView());
                return true;
            }
            return false;
        });

        binding.btnValidate.setOnClickListener(view1 -> {

            if (!allFieldsFilled()) {
                Toast.makeText(getActivity(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            } else {
                date = Objects.requireNonNull(binding.etDate.getText()).toString();
                time = Objects.requireNonNull(binding.etTime.getText()).toString();
                description = Objects.requireNonNull(binding.etDescription.getText()).toString();
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
        return Objects.requireNonNull(binding.etDescription.getText()).length() > 0
                && Objects.requireNonNull(binding.etDate.getText()).length() > 0
                && Objects.requireNonNull(binding.etTime.getText()).length() > 0
                && Objects.requireNonNull(binding.etNumberParticipantRequired.getText()).length() > 0;
    }

}