package com.example.socialsport;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private Button btn_log_in;
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_log_in = findViewById(R.id.btn_log_in);
        btn_back = findViewById(R.id.btn_back);

        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO with better way
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if(!email.equals("") && !password.equals("")){
                    if(email.equals("admin") && password.equals("admin")){
                        Toast.makeText(getApplicationContext(), "Succes", Toast.LENGTH_SHORT);
                        Intent IntentPrincipalActivity = new Intent(getApplicationContext(), PrincipalPage.class);
                        startActivity(IntentPrincipalActivity);
                    }else {
                        Toast.makeText(getApplicationContext(), "Wrong ! Try again", Toast.LENGTH_SHORT);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Empty fields", Toast.LENGTH_SHORT);
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(IntentWelcomeActivity);
                finish();
            }
        });



    }
}