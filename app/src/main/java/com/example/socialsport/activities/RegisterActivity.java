package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.utils.Utils;
import com.example.socialsport.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
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

        setListeners();
    }

    private void setListeners() {
        btnLogin.setOnClickListener(view -> {
            //TODO with better way
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String checkPassword = etCheckPassword.getText().toString();
            String name = etName.getText().toString();
            String age = etAge.getText().toString();

            if (checkPassword.compareTo(password) == 0) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Sign up page", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                Utils.writeUserIntoDatabase(user.getEmail(), name, age, user.getUid());
                                Toast.makeText(RegisterActivity.this, "Account successfully created.", Toast.LENGTH_SHORT).show();
                                Log.d("USER UID", user.getUid());
                                Utils.hideKeyboard(this, view);
                                //TODO on complete sign up treatment
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign up page", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                                //TODO on failed sign up treatment
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        etAge.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && allFieldsFilled()) {
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

    private boolean allFieldsFilled() {
        return etEmail.getText().length() > 0 && etName.getText().length() > 0 && etPassword.getText().length() > 0 && etAge.getText().length() > 0;
    }
}
