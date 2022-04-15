package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.socialsport.databinding.WelcomeActivityBinding;


public class WelcomeActivity extends AppCompatActivity {

    private WelcomeActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WelcomeActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGoLogIn.setOnClickListener(view -> {
            Intent intentLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLoginActivity);
        });
        binding.btnGoRegister.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
        });
    }

}
