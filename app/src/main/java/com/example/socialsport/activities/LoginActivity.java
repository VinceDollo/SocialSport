package com.example.socialsport.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.Constants;
import com.example.socialsport.PreferenceManager;
import com.example.socialsport.Utils;
import com.example.socialsport.databinding.LogInActivityBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {


    private int remainingTries;
    private LogInActivityBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LogInActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        remainingTries = 3;


        preferenceManager = new PreferenceManager(getApplicationContext());

   /*     if(preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
            Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
            startActivity(i);
        }*/

        setListeners();

    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USER_NAME)
                .whereEqualTo(Constants.KEY_EMAIL, binding.etEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.etPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getData().get(Constants.KEY_NAME).toString());
                        preferenceManager.putString(Constants.KEY_AGE, documentSnapshot.getData().get(Constants.KEY_AGE).toString());
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getData().get(Constants.KEY_EMAIL).toString());
                        Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
                        loading(false);
                        startActivity(i);
                    } else {
                        loading(false);
                        remainingTries--;
                        Utils.toast("Email/password are not corrects, remaining tries : " + remainingTries, LoginActivity.this);
                        if (remainingTries == 0) {
                            binding.btnLogIn.setEnabled(false);
                            binding.btnLogIn.setBackgroundColor(Color.DKGRAY);
                        }
                    }
                });
    }

    private void setListeners(){
        binding.btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        //When you push enter button
        binding.etPassword.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                binding.btnLogIn.performClick();
                return true;
            }
            return false;
        });

        binding.tvGoRegFromLogin.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
        });

        binding.btnLogIn.setOnClickListener(view -> {
            if(isValidInformation()){
                signIn();
            }
        });
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

    private Boolean isValidInformation() {
        if(binding.etEmail.getText().toString().trim().isEmpty()) {
            Utils.toast("Enter email", LoginActivity.this);
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()){
            Utils.toast("Enter valid email", LoginActivity.this);
            return false;
        }else if(binding.etPassword.getText().toString().trim().isEmpty()){
            Utils.toast("Enter password", LoginActivity.this);
            return false;
        }else {
            return true;
        }
    }
}


