package com.example.socialsport;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseUtils {

    public static void writeUser(String email, String name, String age, String uid) {
        User currentUser = new User(email, name, age);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(currentUser);
    }

    public static void getUser(String uid) {
        ArrayList<Object> list = new ArrayList<>();//Creating arraylist
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firebase_debug", "Error getting data", task.getException());
            } else {
                Log.d("Firebase_debug", String.valueOf(task.getResult().getValue()));
                //putUserInObject(task.getResult().getValue());
                Log.d("Firebase_debug", list.toString());
            }
        });
        Log.d("Firebase_debug", list.toString());
    }

}
