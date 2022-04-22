package com.example.socialsport.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.databinding.FragmentAddActivityBinding;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.TableKeys;

public class AddActivityFragment extends Fragment implements View.OnClickListener {

    private FragmentAddActivityBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddActivityBinding.inflate(inflater);
        View view = binding.getRoot();
        setOnClickListener();

        User user = ((PrincipalPageActivity) requireActivity()).getUser();

        if(user.getProfileImage() != null){
            byte[] bytes = Base64.decode(user.getProfileImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            binding.civProfile.setImageBitmap(bitmap);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("sport", view.getTag().toString());
        Fragment newF = new PlaceActivityFragment();
        newF.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
    }


    private void setOnClickListener() {
        binding.addActivitySoccer.setOnClickListener(this); // calling onClick() method
        binding.addActivityFootball.setOnClickListener(this); // calling onClick() method
        binding.addActivityVolleyball.setOnClickListener(this); // calling onClick() method
        binding.addActivityBasketball.setOnClickListener(this); // calling onClick() method
        binding.addActivityHandball.setOnClickListener(this); // calling onClick() method
        binding.addActivityTennis.setOnClickListener(this); // calling onClick() method
        binding.addActivityRunning.setOnClickListener(this); // calling onClick() method
        binding.btnBack.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit());
    }
}