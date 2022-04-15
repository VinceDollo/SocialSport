package com.example.socialsport.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.Constants;
import com.example.socialsport.PreferenceManager;
import com.example.socialsport.R;
import com.example.socialsport.databinding.LogInActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword;
    private TextView tvGoReg;
    private Button btnLogin;
    private ImageButton btnBack;
    private int remainingTries;

    private LogInActivityBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LogInActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

   /*     if(preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
            Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
            startActivity(i);
        }*/




        EditText etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_log_in);
        tvGoReg = findViewById(R.id.tv_go_reg_from_login);
        btnBack = findViewById(R.id.btn_back);

        remainingTries = 3;


        btnLogin.setOnClickListener(view -> {
            if(isValidInformation()){
                signIn();
            }

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

        });

        setListeners();

    }

    private void signIn() {
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_NAME)
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
                        Toast.makeText(LoginActivity.this, "Email/password are not corrects, remaining tries : " + remainingTries,
                                Toast.LENGTH_SHORT).show();
                        if (remainingTries == 0) {
                            btnLogin.setEnabled(false);
                            btnLogin.setBackgroundColor(Color.DKGRAY);
                        }
                    }
                });
    }

    private void setListeners(){
        btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        //When you push enter button
        etPassword.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                btnLogin.performClick();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        tvGoReg.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
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
            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()){
            Toast.makeText(LoginActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }else if(binding.etPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
}


