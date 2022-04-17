package com.example.socialsport.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.R;
import com.example.socialsport.utils.TableKeys;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword;
    private TextView tvGoReg;
    private Button btnLogin;
    private ImageButton btnBack;
    private EditText etEmail;
    private CheckBox cbRememberMe;
    private int remainingTries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_log_in);
        tvGoReg = findViewById(R.id.tv_go_reg_from_login);
        btnBack = findViewById(R.id.btn_back);
        cbRememberMe = findViewById(R.id.checkbox_remember_me);

        Paper.init(this);

        setListeners();

        remainingTries = 3;
    }

    private void setListeners() {
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

        btnLogin.setOnClickListener(view -> tryToLoginUser());

        tvGoReg.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
        });
    }

    private void tryToLoginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (cbRememberMe.isChecked()) {
                Paper.book().write(TableKeys.USER_EMAIL_KEY, email);
                Paper.book().write(TableKeys.USER_PASSWORD_KEY, password);
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginPage", "signInWithEmail:success");
                    Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
                    startActivity(i);

                    Toast.makeText(LoginActivity.this, "Authentication successful.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    remainingTries--;
                    // If sign in fails, display a message to the user.
                    Log.w("LoginPage", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Email/password are not corrects, remaining tries : " + remainingTries,
                            Toast.LENGTH_SHORT).show();
                    if (remainingTries == 0) {
                        btnLogin.setEnabled(false);
                        btnLogin.setBackgroundColor(Color.DKGRAY);
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Email and/or password is empty",
                    Toast.LENGTH_SHORT).show();
        }
    }

}


