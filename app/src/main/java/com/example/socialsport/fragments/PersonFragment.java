package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.Utils;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.SportActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonFragment extends Fragment {

    private Button btnDisc;
    private LinearLayout llActivities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(3, true);

        TextView tvName = view.findViewById(R.id.tv_name);
        CircleImageView civProfile = view.findViewById(R.id.civ_profil);
        llActivities = view.findViewById(R.id.llactivitiefinished);

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), civProfile::setImageURI);
        civProfile.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        tvName.setText(((PrincipalPageActivity) requireActivity()).getUser().getName());
        Log.d("firebase", "" + ((PrincipalPageActivity) requireActivity()).getUser().getName());
        btnDisc = view.findViewById(R.id.btn_disconnect);

        displayMyActivities();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        btnDisc.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
            requireActivity().onBackPressed();
        });
    }

    @SuppressLint("SetTextI18n")
    public void displayMyActivities() {
        llActivities.removeAllViews();
        int cmp = 0;
        List<SportActivity> myActivities = Utils.getMyActivities();
        Log.d("PersonFragment", myActivities.toString());
        for (SportActivity currentAct : myActivities) {
            TextView tv = new TextView(PersonFragment.this.getContext());
            tv.setText(currentAct.getSport() + " : " + currentAct.getDate() + "\n"
                    + Utils.getPrintableLocation(getActivity(), currentAct.getCoords()));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (cmp != 0) {
                params.setMargins(0, 30, 0, 0);
            }

            cmp++;

            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.btn_finished_activities);
            tv.setPadding(20, 30, 20, 30);
            tv.setGravity(Gravity.CENTER);
            llActivities.addView(tv);
        }
    }
}