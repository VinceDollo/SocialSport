package com.example.socialsport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsport.R;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        Button btnGoLoginIn = findViewById(R.id.btn_go_log_in);
        Button btnGoRegister = findViewById(R.id.btn_go_register);

       /* FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("TAG", token);
                        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });*/

        btnGoLoginIn.setOnClickListener(view -> {
            Intent intentLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLoginActivity);
        });

        btnGoRegister.setOnClickListener(view -> {
            Intent intentSignUpActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intentSignUpActivity);
        }
        );
    }

   /* private void addDataToFirestore(){
        FirebaseFirestore database=  FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("test","test");
        data.put("test1","test1");
        data.put("test2","test2");
        database.collection("users").add(data).addOnSuccessListener(documentReference -> {
            Toast.makeText(getApplicationContext(), "Data insert", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }*/

}
