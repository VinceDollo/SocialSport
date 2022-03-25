package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialsport.R;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {

    private View view;
    private TextView tv_name, tv_message;
    private String name;
    private ArrayList<String> message;
    private LinearLayout ll_coversation;

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


        ll_coversation = view.findViewById(R.id.ll_conversation);
        tv_name = view.findViewById(R.id.name);



        String messages = "";
        String sender ="";
        String time ="";

        for (String msg: message) {
            String[] a = msg.split("//");
            messages=a[0];
            sender = a[2];
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,20);
            textView.setText(messages);
            textView.setTextSize(15);
            textView.setPadding(30,20,30,20);
            if(sender.equals("false")){
                params.gravity = Gravity.LEFT;
                textView.setTextColor(textView.getContext().getColor(R.color.black));
                textView.setBackgroundResource(R.drawable.tv_received_message);
            }else {
                params.gravity = Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.tv_send_message);
                textView.setTextColor(textView.getContext().getColor(R.color.white));
                textView.setLayoutParams(params);
            }
            textView.setLayoutParams(params);

            ll_coversation.addView(textView);
        }

        tv_name.setText(name);




        return view;
    }
}