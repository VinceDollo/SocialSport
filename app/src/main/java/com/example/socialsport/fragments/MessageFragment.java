package com.example.socialsport.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentMessageBinding;
import com.example.socialsport.utils.ListAdapter;


public class MessageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.example.socialsport.databinding.FragmentMessageBinding binding = FragmentMessageBinding.inflate(inflater);
        View view = binding.getRoot();
        String uid = ((PrincipalPageActivity) requireActivity()).getUidUser();

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(2, true);

        ((PrincipalPageActivity) requireActivity()).getIdConv();

        ListAdapter lAdapter = new ListAdapter(getContext(), ((PrincipalPageActivity) requireActivity()).getIdConv(), uid);

        binding.lvChat.setAdapter(lAdapter);

        binding.lvChat.setOnItemClickListener((adapterView, view1, i, l) -> {
            String firstKey = ((PrincipalPageActivity) requireActivity()).getIdConv().get(i);

            Bundle bundle = new Bundle();
            bundle.putString("idConv", firstKey);
            Log.d("MessageFragment", firstKey);
            Fragment newF = new ConversationFragment();
            newF.setArguments(bundle);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        return view;
    }

}