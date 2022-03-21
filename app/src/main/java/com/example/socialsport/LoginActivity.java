package com.example.socialsport;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText et_email, et_password;
    private Button btn_log_in,btn_go_reg;
    int remainingTries;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();

            if (!email.isEmpty()&!password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginPage", "signInWithEmail:success");


                                //TODO
                                //Passer les infos utilisateurs
                                Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
                                startActivity(i);

                                Toast.makeText(LoginActivity.this, "Authentication succesful.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(user);
                            } else {
                                remainingTries--;
                                // If sign in fails, display a message to the user.
                                Log.w("LoginPage", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Email/password are not corrects, remaining tries : "+remainingTries,
                                        Toast.LENGTH_SHORT).show();
                                // updateUI(null);
                                if(remainingTries==0){
                                    btn_log_in.setEnabled(false);
                                    btn_log_in.setBackgroundColor(Color.DKGRAY);
                                }
                            }
                        });
            }
        else {
                Toast.makeText(LoginActivity.this, "Email and/or password is empty",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        mAuth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_log_in = findViewById(R.id.btn_log_in);
        btn_go_reg = findViewById(R.id.btn_go_reg_from_login);
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_log_in.setOnClickListener(onClickListener);

        remainingTries=3;

        btn_back.setOnClickListener(view -> {
            Intent IntentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(IntentWelcomeActivity);
            finish();
        });

        //When you push enter button
        et_password.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                btn_log_in.performClick();
                return true;
            }
            return false;
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            System.out.println(currentUser);
        }
        btn_go_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(IntentSignUpActivity);
            }
        });
    }
}


