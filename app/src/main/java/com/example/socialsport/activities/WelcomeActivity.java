package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        Button btnGoLoginIn = findViewById(R.id.btn_go_log_in);
        Button btnGoRegister = findViewById(R.id.btn_go_register);

        btnGoLoginIn.setOnClickListener(view -> {
            Intent intentLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLoginActivity);
        });

        btnGoRegister.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
        }
        );
    }


}
