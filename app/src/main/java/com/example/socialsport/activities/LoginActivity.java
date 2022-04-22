package com.example.socialsport.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.databinding.LogInActivityBinding;
import com.example.socialsport.entities.User;
import com.example.socialsport.utils.PreferenceManager;
import com.example.socialsport.utils.TableKeys;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private int remainingTries;
    private LogInActivityBinding binding;
    private User infoUser;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LogInActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        Paper.init(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setListeners();
        remainingTries = 3;
    }

    private void setListeners() {
        binding.btnBack.setOnClickListener(view -> {
            Intent intentWelcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intentWelcomeActivity);
            finish();
        });

        //When you push enter button
        binding.etPassword.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                binding.btnLogIn.performClick();
                return true;
            }
            return false;
        });

        binding.btnLogIn.setOnClickListener(view -> {
            if(isValidInformation()) {
                tryToLoginUser();
            }
        });

        binding.tvGoRegFromLogin.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
        });
    }

    private void tryToLoginUser() {
        loading(true);
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (binding.checkboxRememberMe.isChecked()) {
                Paper.book().write(TableKeys.USER_EMAIL_KEY, email);
                Paper.book().write(TableKeys.USER_PASSWORD_KEY, password);
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                if (task.isSuccessful()) {
                    preferenceManager.putString(TableKeys.USER_UUID_KEY, task.getResult().getUser().toString());

                    FirebaseDatabase.getInstance().getReference().child(TableKeys.USERS).child(task.getResult().getUser().getUid()).child(TableKeys.USER_NAME_KEY).get().addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            Log.e("TAG", "Error getting data", task2.getException());
                        } else {
                            Log.e("TAG", task2.getResult().getValue().toString());
                            preferenceManager.putString(TableKeys.USER_NAME_KEY, (String) task2.getResult().getValue());
                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child(TableKeys.USERS).child(task.getResult().getUser().getUid()).child(TableKeys.USER_REAL_EMAIL_KEY).get().addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            Log.e("TAG", "Error getting data", task2.getException());
                        } else {
                            Log.e("TAG", task2.getResult().getValue().toString());
                            preferenceManager.putString(TableKeys.USER_REAL_EMAIL_KEY, (String) task2.getResult().getValue());
                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child(TableKeys.USERS).child(task.getResult().getUser().getUid()).child(TableKeys.USER_AGE_KEY).get().addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            Log.e("TAG", "Error getting data", task2.getException());
                        } else {
                            Log.e("TAG", task2.getResult().getValue().toString());
                            preferenceManager.putString(TableKeys.USER_AGE_KEY, (String) task2.getResult().getValue());
                        }
                    });

                    FirebaseDatabase.getInstance().getReference().child(TableKeys.USERS).child(task.getResult().getUser().getUid()).child(TableKeys.USERS_IMAGE).get().addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            preferenceManager.putString(TableKeys.USERS_IMAGE, "");
                            Log.e("TAG", "Error getting data", task2.getException());
                        } else if(task2.getResult().getValue()==null) {
                            Log.e("TAG", "Pas d'image");

                        }else{
                            Log.e("TAG", task2.getResult().getValue().toString());
                            preferenceManager.putString(TableKeys.USERS_IMAGE, (String) task2.getResult().getValue());
                        }
                    });

                    Utils.getUserFromDatabase(task.getResult().getUser().getUid());
                    //preferenceManager.putString(TableKeys.USER_NAME_KEY, task.getResult().);
                   // infoUser = Utils.getUserFromDatabase(task.getResult().getUser().getUid());
                    //preferenceManager.putString(TableKeys.USER_NAME_KEY, task.getResult().);
                  //  Log.d("DEBUGCA", "Name " +Utils.getUserFromDatabase(task.getResult().getUser().getUid()).getName());
                  //  Log.d("DEBUGCA", " Image : " +Utils.getUserFromDatabase(task.getResult().getUser().getUid()).getProfileImage());

                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginPage", "signInWithEmail:success");
                    loading(false);
                    Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
                    i.putExtra("user",Utils.getUserFromDatabase(task.getResult().getUser().getUid()));
                    startActivity(i);

                } else {
                    remainingTries--;
                    // If sign in fails, display a message to the user.
                    Log.w("LoginPage", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Email/password are not corrects, remaining tries : " + remainingTries,
                            Toast.LENGTH_SHORT).show();
                    if (remainingTries == 0) {
                        binding.btnLogIn.setEnabled(false);
                        binding.btnLogIn.setBackgroundColor(Color.DKGRAY);
                    }
                    loading(false);

                }
            });
        } else {
            loading(false);
            Toast.makeText(LoginActivity.this, "Email and/or password is empty",
                    Toast.LENGTH_SHORT).show();
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

    private Boolean isValidInformation() {
        if(binding.etEmail.getText().toString().trim().isEmpty()) {
            Utils.toast(LoginActivity.this, "Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()){
            Utils.toast( LoginActivity.this,"Enter valid email");
            return false;
        }else if(binding.etPassword.getText().toString().trim().isEmpty()){
            Utils.toast(LoginActivity.this,"Enter password");
            return false;
        }else {
            return true;
        }
    }

}


