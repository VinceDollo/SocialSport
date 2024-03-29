package com.example.socialsport.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.activities.LoginActivity;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentPersonBinding;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class PersonFragment extends Fragment {

    private FragmentPersonBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonBinding.inflate(inflater);
        // Inflate the layout for this fragment
        View view = binding.getRoot();
        ((PrincipalPageActivity) requireActivity()).getMeowBottomNavigation().show(3, true);

        User user = ((PrincipalPageActivity) requireActivity()).getUser();

        if (user.getImage() != null) {
            byte[] bytes = Base64.decode(user.getImage() , Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.civProfile.setImageBitmap(bitmap);
        }

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            binding.civProfile.setImageURI(uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                Utils.uploadImage(encodeImage(bitmap), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                user.setImage(encodeImage(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        binding.civProfile.setOnClickListener(view1 -> mGetContent.launch("image/*"));

        binding.tvName.setText(((PrincipalPageActivity) requireActivity()).getUser().getName());
        Log.d("firebase", "" + ((PrincipalPageActivity) requireActivity()).getUser().getName());

        displayMyActivities();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.btnDisconnect.setOnClickListener(view -> {
            Paper.book().destroy();
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            startActivity(i);
        });
    }

    @SuppressLint("SetTextI18n")
    public void displayMyActivities() {
        binding.llFinishedActivities.removeAllViews();
        binding.llUpcomingActivities.removeAllViews();

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
            binding.llUpcomingActivities.addView(tv);
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
            binding.llFinishedActivities.addView(tv);
        }
    }

    private String encodeImage(Bitmap image) {
        int previewWidth = 150;
        int previewHeight = image.getHeight() * previewWidth / image.getHeight();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(image, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}