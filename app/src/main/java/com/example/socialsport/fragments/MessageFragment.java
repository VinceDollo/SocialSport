package com.example.socialsport.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.databinding.FragmentMessageBinding;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.ListAdapter;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;


public class MessageFragment extends Fragment {

    //TODO changer avec image de bdd
    private final int[] images = {R.drawable.img_football,R.drawable.img_football,R.drawable.img_football,R.drawable.img_football,R.drawable.img_football};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.example.socialsport.databinding.FragmentMessageBinding binding = FragmentMessageBinding.inflate(inflater);
        View view = binding.getRoot();
        String uid = ((PrincipalPageActivity) requireActivity()).getUidUser();

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2,true);

        ((PrincipalPageActivity) requireActivity()).getIdconv();

        ListAdapter lAdapter = new ListAdapter(getContext(), ((PrincipalPageActivity) requireActivity()).getIdconv(), images, uid);

        binding.lvChat.setAdapter(lAdapter);

        binding.lvChat.setOnItemClickListener((adapterView, view1, i, l) -> {
            Object firstKey = ((PrincipalPageActivity) requireActivity()).getIdconv().get(i);

            Bundle bundle = new Bundle();
            bundle.putString("idConv", (String) firstKey);
            Fragment newF = new ConversationFragment();
            newF.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        return view;
    }

}