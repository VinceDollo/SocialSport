package com.example.socialsport.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialsport.R;

public class DescriptionActivityFragment extends Fragment {

    private Button btn_back, btn_validate;
    private EditText et_description, et_time,et_date, et_number_of_participant;
    private String sport,coordinates,description,date, time;
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
        if(bundle != null){
            sport = bundle.getString("sport");
            coordinates = bundle.getString("location");
        }

        btn_validate = view.findViewById(R.id.btn_validate);
        et_time = view.findViewById(R.id.et_time);
        et_date = view.findViewById(R.id.et_date);
        et_description = view.findViewById(R.id.et_description);
        et_number_of_participant = view.findViewById(R.id.et_number_participant_required);



        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add info
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = et_date.getText().toString();
                time = et_time.getText().toString();
                description = et_description.getText().toString();
                String string_number_participant = et_number_of_participant.getText().toString();

                if(date.equals("") || time.equals("") || description.equals("") || string_number_participant.equals("")){
                    //ELOY Info a recuperer + sport et coordinates
                    number_participant = Integer.parseInt(string_number_participant);
                    Toast.makeText(getActivity(), "Empty fiels", Toast.LENGTH_SHORT).show();
                }else {
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new PlaceActivityFragment()).commit();
                }
            }
        });



        return view;
    }
}