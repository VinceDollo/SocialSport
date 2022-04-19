package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.databinding.RegisterActivityBinding;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RegisterActivityBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("Start", currentUser.toString());
        }

        setListeners();
    }

    private void setListeners() {
       binding.btnLogIn.setOnClickListener(view -> {
            loading(true);
            if(isValidInformation()){
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                String name = binding.etName.getText().toString();
                String age = binding.etAge.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                Utils.writeUserIntoDatabase(user.getEmail(), name, age, user.getUid());
                                Utils.toast(RegisterActivity.this,"Account successfully created.");
                                Utils.hideKeyboard(this, view);
                                loading(false);
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                //TODO on complete sign up treatment
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign up page", "createUserWithEmail:failure", task.getException());
                                loading(false);
                                Utils.toast(RegisterActivity.this,Objects.requireNonNull(task.getException()).getLocalizedMessage());
                            }
                        });
            } else{
                loading(false);
            }
        });

        binding.btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        binding.etAge.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && isValidInformation()) {
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
            Utils.toast(RegisterActivity.this,"Email empty");
            return false;
        }else if(binding.etPassword.getText().toString().trim().isEmpty()){
            Utils.toast(RegisterActivity.this,"Password empty");
            return false;
        }else if(binding.etCheckPassword.getText().toString().trim().isEmpty()) {
            Utils.toast(RegisterActivity.this,"Password verification empty");
            return false;
        }else if(binding.etName.getText().toString().trim().isEmpty()){
            Utils.toast(RegisterActivity.this,"Name empty");
            return false;
        }else if(binding.etAge.getText().toString().trim().isEmpty()){
            Utils.toast(RegisterActivity.this,"Age empty");
            return false;
        }else if(!binding.etPassword.getText().toString().trim().equals(binding.etCheckPassword.getText().toString().trim())){
            Utils.toast(RegisterActivity.this,"Password not equals");
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
