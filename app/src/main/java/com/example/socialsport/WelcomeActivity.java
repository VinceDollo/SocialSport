package com.example.socialsport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button btn_go_login_in, btn_go_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        btn_go_login_in = findViewById(R.id.btn_go_log_in);
        btn_go_register = findViewById(R.id.btn_go_register);

        btn_go_login_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(IntentLoginActivity);
            }
        });
        //TODO
        btn_go_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentSignUpActivity = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(IntentSignUpActivity);
            }
        }
        );
    }


}
