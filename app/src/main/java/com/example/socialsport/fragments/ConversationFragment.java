package com.example.socialsport.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialsport.R;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {

    private View view;
    private TextView tv_name, tv_message;
    private String name;
    private ArrayList<String> message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_conversation, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            message = bundle.getStringArrayList("message");
        }

        tv_message = view.findViewById(R.id.message);
        tv_name = view.findViewById(R.id.name);

        String messagesText = "";
        for (String msg: message) {
            messagesText += msg.split("//")[0] + "\n";
        }

        tv_message.setText(messagesText);
        tv_name.setText(name);




        return view;
    }
}