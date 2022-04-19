package com.example.socialsport.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.databinding.FragmentConversationBinding;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {
    private String name;
    private ArrayList<String> message;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.example.socialsport.databinding.FragmentConversationBinding binding = FragmentConversationBinding.inflate(inflater);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            message = bundle.getStringArrayList("message");
        }

        String messages;
        String sender;

        for (String msg : message) {
            String[] a = msg.split("//");
            messages = a[0];
            sender = a[2];
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 20);
            textView.setText(messages);
            textView.setTextSize(15);
            textView.setPadding(30, 20, 30, 20);
            if (sender.equals("false")) {
                params.gravity = Gravity.START;
                textView.setTextColor(textView.getContext().getColor(R.color.black));
                textView.setBackgroundResource(R.drawable.tv_received_message);
            } else {
                params.gravity = Gravity.END;
                textView.setBackgroundResource(R.drawable.tv_send_message);
                textView.setTextColor(textView.getContext().getColor(R.color.white));
                textView.setLayoutParams(params);
            }
            textView.setLayoutParams(params);

            binding.llConversation.addView(textView);
        }

        binding.name.setText(name);

        return view;
    }
}