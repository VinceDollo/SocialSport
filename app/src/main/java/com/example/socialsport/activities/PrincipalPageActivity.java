package com.example.socialsport.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.socialsport.R;
import com.example.socialsport.entities.User;
import com.example.socialsport.fragments.HomeFragment;
import com.example.socialsport.fragments.LoadFragment;
import com.example.socialsport.fragments.MessageFragment;
import com.example.socialsport.fragments.PersonFragment;
import com.example.socialsport.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrincipalPageActivity extends FragmentActivity {

    private static final String TAG = PrincipalPageActivity.class.getSimpleName();

    private MeowBottomNavigation meowBottomNavigation;
    private User user;

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> message = new ArrayList<>();

    private HashMap<String, ArrayList<String>> messagesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_page_activity);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateMessages();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            user = (User) Utils.getUserFromDatabase(uid);

        } else {
            Log.e(TAG, "Current user is null");
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

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LoadFragment()).addToBackStack(null).commit();
        Utils.setActivitiesListenerFromDatabase(this); //Listen to activities managements

        Handler handler = new Handler();
        handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit(), 1000);
    }

    private void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    public void updateMessages() {
        messagesMap = new HashMap<>();
        FirebaseDatabase.getInstance().getReference().child("chat")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String contact = snapshot.getKey();
                    messagesMap.put(contact, new ArrayList<>());
                    name.add(contact);
                    if (contact != null)
                        updateMessagesForContact(contact);
                }
            }
        });
    }

    public void updateMessagesForContact(String contact) {
        FirebaseDatabase.getInstance().getReference().child("chat")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(contact)
                .get().addOnCompleteListener(task1 -> {
            if (!task1.isSuccessful()) {
                Log.e("firebase", "Error getting data", task1.getException());
            } else {
                for (DataSnapshot snapshot1 : task1.getResult().getChildren()) {
                    Objects.requireNonNull(messagesMap.get(contact))
                            .add(Objects.requireNonNull(snapshot1.child("message").getValue())
                                    + "//"
                                    + Objects.requireNonNull(snapshot1.child("date").getValue())
                                    + "//"
                                    + Objects.requireNonNull(snapshot1.child("sender").getValue()));
                    message.add(Objects.requireNonNull(snapshot1.child("message").getValue())
                            + "//" + Objects.requireNonNull(snapshot1.child("date").getValue())
                            + "//" + Objects.requireNonNull(snapshot1.child("sender").getValue()));
                }
            }
        });
    }

    public Map<String, ArrayList<String>> getMessagesMap() {
        return messagesMap;
    }

    public MeowBottomNavigation getMeowBottomNavigation() {
        return meowBottomNavigation;
    }

    public User getUser() {
        return user;
    }

}
