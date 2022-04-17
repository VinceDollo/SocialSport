package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.socialsport.activities.LoginActivity;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class PersonFragment extends Fragment {

    private Button btnLogout;
    private LinearLayout llFinishedActivities;
    private LinearLayout llUpcomingActivities;
    private CircleImageView civProfile;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(3, true);

        TextView tvName = view.findViewById(R.id.tv_name);
        civProfile = view.findViewById(R.id.civ_profile);
        llFinishedActivities = view.findViewById(R.id.ll_finished_activities);
        llUpcomingActivities = view.findViewById(R.id.ll_upcoming_activities);
        user = ((PrincipalPageActivity) requireActivity()).getUser();

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            civProfile.setImageURI(uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                user.setProfileImage(bitmap);
                Utils.uploadImage(this.getContext(), user.getProfileImage(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        civProfile.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        tvName.setText(((PrincipalPageActivity) requireActivity()).getUser().getName());
        Log.d("firebase", "" + ((PrincipalPageActivity) requireActivity()).getUser().getName());
        btnLogout = view.findViewById(R.id.btn_disconnect);

        displayMyActivities();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        btnLogout.setOnClickListener(view -> {
            Paper.book().destroy();
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            startActivity(i);
        });

        if (user.getProfileImage() != null)
            civProfile.setImageBitmap(user.getProfileImage());
    }

    @SuppressLint("SetTextI18n")
    public void displayMyActivities() {
        llFinishedActivities.removeAllViews();
        llUpcomingActivities.removeAllViews();

        int cmp = 0;
        List<SportActivity> myActivities = Utils.getMyActivities();
        Log.d("PersonFragment_myActivities", myActivities.toString());
        List<SportActivity> upcomingActivities = Utils.getUpcomingActivities(myActivities);
        Log.d("PersonFragment_upcomingActivities", upcomingActivities.toString());
        List<SportActivity> pastActivities = Utils.getPastActivities(myActivities);
        Collections.reverse(pastActivities); //To have most recent activity in first
        Log.d("PersonFragment_pastActivities", pastActivities.toString());

        for (SportActivity activity : upcomingActivities) {
            TextView tv = new TextView(PersonFragment.this.getContext());
            tv.setText(activity.getSport() + " : " + activity.getDate() + "\n"
                    + Utils.getPrintableLocation(getActivity(), activity.getCoords()));
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
            llUpcomingActivities.addView(tv);
        }

        cmp = 0;
        for (SportActivity activity : pastActivities) {
            TextView tv = new TextView(PersonFragment.this.getContext());
            tv.setText(activity.getSport() + " : " + activity.getDate() + "\n"
                    + Utils.getPrintableLocation(getActivity(), activity.getCoords()));
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
            llFinishedActivities.addView(tv);
        }
    }
}