package com.example.socialsport.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.socialsport.R;
import com.example.socialsport.utils.TableKeys;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Paper.init(this);

        String email = Paper.book().read(TableKeys.USER_EMAIL_KEY);
        String password = Paper.book().read(TableKeys.USER_PASSWORD_KEY);

        if (!"".equals(email) && !"".equals(password) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            assert email != null && password != null;
            tryToLoginUser(email, password);
        } else {
            Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(i);
        }
    }

    private void tryToLoginUser(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginPage", "signInWithEmail:success");
                    Intent i = new Intent(getApplicationContext(), PrincipalPageActivity.class);
                    startActivity(i);

                    Toast.makeText(this, "Authentication successful.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginPage", "signInWithEmail:failure", task.getException());
                }
            });
        } else {
            Toast.makeText(this, "Email and/or password is empty",
                    Toast.LENGTH_SHORT).show();
        }
    }
}