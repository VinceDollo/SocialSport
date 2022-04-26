package com.example.socialsport.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrincipalPageActivity extends FragmentActivity {

    private static final String TAG = PrincipalPageActivity.class.getSimpleName();

    private MeowBottomNavigation meowBottomNavigation;
    private User user;

    ArrayList<String> idConv = new ArrayList<>();

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_page_activity);

        Log.d(TAG, idConv.toString());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            user = Utils.getUserFromDatabase(currentUser.getUid());
            addConvId(currentUser.getUid());
            Log.d(TAG, idConv.toString());
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
        handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit(), 1500);
    }

    private void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    public List<String> getIdConv() {
        return idConv;
    }

    public MeowBottomNavigation getMeowBottomNavigation() {
        return meowBottomNavigation;
    }

    public User getUser() {
        return user;
    }

    public String getUidUser() {
        return currentUser.getUid();
    }


    private void addConvId(String uuid) {
        FirebaseDatabase.getInstance().getReference().child("users").child(uuid).child("conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    idConv.add(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

}
