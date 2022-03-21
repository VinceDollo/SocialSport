package com.example.socialsport.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.socialsport.R;

public class DescriptionActivityFragment extends Fragment {

    private ImageButton btn_back;
    private Button btn_validate;
    private EditText et_description, et_time, et_date, et_number_of_participant;
    private String sport, coordinates, description, date, time;
    private int number_participant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        btn_back.setOnClickListener(view12 -> {
            //TODO add info
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new PlaceActivityFragment()).addToBackStack(null).commit();
        });

        btn_validate.setOnClickListener(view1 -> {
            date = et_date.getText().toString();
            time = et_time.getText().toString();
            description = et_description.getText().toString();
            String string_number_participant = et_number_of_participant.getText().toString();

            if (date.equals("") || time.equals("") || description.equals("") || string_number_participant.equals("")) {
                //ELOY Info a recuperer + sport et coordinates
                Toast.makeText(getActivity(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            } else {
                number_participant = Integer.parseInt(string_number_participant);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            }
        });
        return view;
    }
}