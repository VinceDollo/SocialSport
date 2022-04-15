package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.Constants;
import com.example.socialsport.Utils;
import com.example.socialsport.R;
import com.example.socialsport.databinding.RegisterActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etCheckPassword;
    private EditText etName;
    private EditText etAge;
    private Button btnLogin;
    private ImageButton btnBack;
    private TextView tvGoLogin;

    private RegisterActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etCheckPassword = findViewById(R.id.et_check_password);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);

        btnLogin = findViewById(R.id.btn_log_in);
        tvGoLogin = findViewById(R.id.tv_go_login_from_reg);
        btnBack = findViewById(R.id.btn_back);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("Start", currentUser.toString());
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(view -> {
            loading(true);
                    if (isValidInformation()) {
                        HashMap<String, Object> userInfo = new HashMap<>();
                        userInfo.put(Constants.KEY_NAME, binding.etName.getText().toString().trim());
                        userInfo.put(Constants.KEY_EMAIL, binding.etEmail.getText().toString().trim());
                        userInfo.put(Constants.KEY_AGE, binding.etAge.getText().toString().trim());
                        userInfo.put(Constants.KEY_PASSWORD, binding.etPassword.getText().toString().trim());
                        database.collection(Constants.KEY_COLLECTION_NAME).add(userInfo).addOnSuccessListener(documentReference -> {
                            loading(false);
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            Toast.makeText(RegisterActivity.this, "Account successfully created.", Toast.LENGTH_SHORT).show();

                        }).addOnFailureListener(exception -> {
                            loading(false);
                            Toast.makeText(RegisterActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }



        });


        btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        etPassword.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                btnLogin.performClick();
                return true;
            }
            return false;
        });

        tvGoLogin.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentSignUpActivity);
        });
    }

    private Boolean isValidInformation() {
        if(binding.etEmail.getText().toString().trim().isEmpty()){
            Toast.makeText(RegisterActivity.this, "Email empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.etPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(RegisterActivity.this, "Password empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.etCheckPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password verification empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.etName.getText().toString().trim().isEmpty()){
            Toast.makeText(RegisterActivity.this, "Name empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.etAge.getText().toString().trim().isEmpty()){
            Toast.makeText(RegisterActivity.this, "Age empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!binding.etPassword.getText().toString().trim().equals(binding.etCheckPassword.getText().toString().trim())){
            Toast.makeText(RegisterActivity.this, "Password not equals", Toast.LENGTH_SHORT).show();
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
