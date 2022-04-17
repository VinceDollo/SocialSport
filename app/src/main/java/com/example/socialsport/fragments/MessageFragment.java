package com.example.socialsport.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.socialsport.utils.ListAdapter;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;


public class MessageFragment extends Fragment {

    //TODO changer avec image de bdd
    private final int[] images = {R.drawable.img_football,R.drawable.img_football,R.drawable.img_football,R.drawable.img_football,R.drawable.img_football};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2,true);

        ListView lvChat = view.findViewById(R.id.lv_chat);

        ListAdapter lAdapter = new ListAdapter(getContext(), ((PrincipalPageActivity) requireActivity()).getMessagesMap(), images);

        lvChat.setAdapter(lAdapter);

        lvChat.setOnItemClickListener((adapterView, view1, i, l) -> {
            Object firstKey = ((PrincipalPageActivity) requireActivity()).getMessagesMap().keySet().toArray()[i];
            Bundle bundle = new Bundle();
            bundle.putString("name", (String) firstKey);
            bundle.putStringArrayList("message",((PrincipalPageActivity) requireActivity()).getMessagesMap().get(firstKey));
            Fragment newF = new ConversationFragment();
            newF.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        return view;
    }

}