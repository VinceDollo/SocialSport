package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.Constants;
import com.example.socialsport.Utils;
import com.example.socialsport.databinding.RegisterActivityBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivityBinding binding;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @Override
    public void onStart() {
        super.onStart();

        database = FirebaseFirestore.getInstance();

        binding.btnLogIn.setOnClickListener(view -> {
            loading(true);
                    if (isValidInformation()) {
                        HashMap<String, Object> userInfo = new HashMap<>();
                        userInfo.put(Constants.KEY_NAME, binding.etName.getText().toString().trim());
                        userInfo.put(Constants.KEY_EMAIL, binding.etEmail.getText().toString().trim());
                        userInfo.put(Constants.KEY_AGE, binding.etAge.getText().toString().trim());
                        userInfo.put(Constants.KEY_PASSWORD, binding.etPassword.getText().toString().trim());
                        database.collection(Constants.KEY_COLLECTION_USER_NAME).add(userInfo).addOnSuccessListener(documentReference -> {
                            loading(false);
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            Utils.toast("Account successfully created.",RegisterActivity.this);
                        }).addOnFailureListener(exception -> {
                            loading(false);
                            Utils.toast(exception.getMessage(),RegisterActivity.this);
                        });

                    }

        });


        binding.btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        binding.etPassword.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                binding.btnLogIn.performClick();
                return true;
            }
            return false;
        });

        binding.tvGoLoginFromReg.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentSignUpActivity);
        });
    }

    private Boolean isValidInformation() {
        if(binding.etEmail.getText().toString().trim().isEmpty()){
            Utils.toast("Email empty",RegisterActivity.this);
            return false;
        }else if(binding.etPassword.getText().toString().trim().isEmpty()){
            Utils.toast("Password empty",RegisterActivity.this);
            return false;
        }else if(binding.etCheckPassword.getText().toString().trim().isEmpty()) {
            Utils.toast("Password verification empty",RegisterActivity.this);
            return false;
        }else if(binding.etName.getText().toString().trim().isEmpty()){
            Utils.toast("Name empty",RegisterActivity.this);
            return false;
        }else if(binding.etAge.getText().toString().trim().isEmpty()){
            Utils.toast("Age empty",RegisterActivity.this);
            return false;
        }else if(!binding.etPassword.getText().toString().trim().equals(binding.etCheckPassword.getText().toString().trim())){
            Utils.toast("Password not equals",RegisterActivity.this);
            return false;
        }else {
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnLogIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.btnLogIn.setVisibility(View.VISIBLE);

        }
    }



}
