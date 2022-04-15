package com.example.socialsport.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.Constants;
import com.example.socialsport.PreferenceManager;
import com.example.socialsport.R;
import com.example.socialsport.Utils;
import com.example.socialsport.databinding.PrincipalPageActivityBinding;
import com.example.socialsport.entities.User;
import com.example.socialsport.fragments.HomeFragment;
import com.example.socialsport.fragments.MessageFragment;
import com.example.socialsport.fragments.PersonFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrincipalPageActivity extends FragmentActivity {

    private MeowBottomNavigation meowBottomNavigation;
    private User user;

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> message = new ArrayList<>();

    HashMap<String, ArrayList<String>> map;

    private PrincipalPageActivityBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PrincipalPageActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        loadUserInformation();

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateMessages();

        if (currentUser != null) {
            user = getUserFromDatabase(currentUser.getUid());
        } else {
            Toast.makeText(getApplicationContext(), "Current User == null", Toast.LENGTH_SHORT).show();
        }


        meowBottomNavigation = findViewById(R.id.bottom_app_bar);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.img_home));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.img_message));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.img_person));

        meowBottomNavigation.show(1, true);

        meowBottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    replace(new HomeFragment());
                    break;
                case 2:
                    replace(new MessageFragment());
                    break;
                case 3:
                    replace(new PersonFragment());
                    break;
                default:
                    break;
            }
            return null;
        });
    }

    public MeowBottomNavigation getMeowBottomNavigation() {
        return meowBottomNavigation;
    }

    public User getUser() {
        return user;
    }

    private void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    public void updateMessages() {
        map = new HashMap<>();
        FirebaseDatabase.getInstance().getReference().child("chat").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String contact = snapshot.getKey();
                    map.put(contact, new ArrayList<>());
                    name.add(contact);
                    if (contact != null)
                        updateMessagesForContact(contact);
                }
            }
        });
    }

    public void updateMessagesForContact(String contact) {
        FirebaseDatabase.getInstance().getReference().child("chat").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(contact).get().addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e("firebase", "Error getting data", task1.getException());
            } else {
                for (DataSnapshot snapshot1 : task1.getResult().getChildren()) {
                    Objects.requireNonNull(map.get(contact)).add(Objects.requireNonNull(snapshot1.child("message").getValue()) + "//" + Objects.requireNonNull(snapshot1.child("date").getValue()) + "//" + Objects.requireNonNull(snapshot1.child("sender").getValue()));
                    message.add(Objects.requireNonNull(snapshot1.child("message").getValue()) + "//" + Objects.requireNonNull(snapshot1.child("date").getValue()) + "//" + Objects.requireNonNull(snapshot1.child("sender").getValue()));
                }
            }
        });
    }

    public static User getUserFromDatabase(String uid) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        User user = new User(null, null, null);
        myRef.child("users").child(uid).child("name").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firebase_user", "Error getting data", task.getException());
            } else {
                user.setName(Objects.requireNonNull(task.getResult().getValue()).toString());
            }
        });
        myRef.child("users").child(uid).child("email").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firebase_user", "Error getting data", task.getException());
            } else {
                user.setEmail(Objects.requireNonNull(task.getResult().getValue()).toString());

            }
        });
        myRef.child("users").child(uid).child("age").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firebase_user", "Error getting data", task.getException());
            } else {
                user.setAge(Objects.requireNonNull(task.getResult().getValue()).toString());
            }
        });
        return user;
    }

    public Map<String, ArrayList<String>> getMap() {
        return map;
    }

    private void loadUserInformation(){
        user = new User(preferenceManager.getString(Constants.KEY_EMAIL), preferenceManager.getString(Constants.KEY_NAME), preferenceManager.getString(Constants.KEY_AGE));
    }

}
