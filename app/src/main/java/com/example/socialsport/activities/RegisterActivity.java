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

import com.example.socialsport.R;
import com.example.socialsport.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText et_email, et_password, et_checkpassword, et_name, et_age;
    private Button btn_log_in;
    private TextView tv_go_login;

    private void writeUserToDatabase(FirebaseDatabase database, String email, String name, String age, String uid) {
        DatabaseReference myRef = database.getReference();
        User currentUser = new User(email, name, age);
        myRef.child("users").child(uid).setValue(currentUser);
    }

    private void getUserFromDatabase(FirebaseDatabase database, String uid) {
        DatabaseReference myRef = database.getReference();
        myRef.child("users").child(uid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
            }
        });

    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO with better way
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();
            String checkPassword = et_checkpassword.getText().toString();
            String name = et_name.getText().toString();
            String age = et_age.getText().toString();

            if (checkPassword.compareTo(password) == 0) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Sign up page", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                               // Toast.makeText(RegisterActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                assert user != null;
                                writeUserToDatabase(database, user.getEmail(), name, age, user.getUid());
                                getUserFromDatabase(database, user.getUid());
                                Toast.makeText(RegisterActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                                Log.d("USER UID", user.getUid());

                               // Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                               // i.putExtra("user", (String) user.getUid());
                               // startActivity(i);

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
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mAuth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_checkpassword = findViewById(R.id.et_check_password);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);

        btn_log_in = findViewById(R.id.btn_log_in);
        tv_go_login = findViewById(R.id.tv_go_login_from_reg);
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_log_in.setOnClickListener(onClickListener);


        btn_back.setOnClickListener(view -> {
            Intent IntentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(IntentWelcomeActivity);
            finish();
        });

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
        tv_go_login.setOnClickListener(view -> {
            Intent IntentSignUpActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(IntentSignUpActivity);
        }
        );
    }
}
