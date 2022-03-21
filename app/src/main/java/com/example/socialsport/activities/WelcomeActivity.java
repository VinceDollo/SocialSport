package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.R;

public class WelcomeActivity extends AppCompatActivity {

    private Button btn_go_login_in, btn_go_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        btn_go_login_in = findViewById(R.id.btn_go_log_in);
        btn_go_register = findViewById(R.id.btn_go_register);

        btn_go_login_in.setOnClickListener(view -> {
            Intent IntentLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(IntentLoginActivity);
        });
        //TODO
        btn_go_register.setOnClickListener(view -> {
            Intent IntentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(IntentSignUpActivity);
        }
        );
    }


}
