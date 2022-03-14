package com.example.socialsport;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class PrincipalPageActivity extends FragmentActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_page_activity);
        bottomNavigationView = findViewById(R.id.bottom_app_bar);
        getSupportFragmentManager().beginTransaction().replace(R.id.principal_page,new HomeFragment()).commit();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment =  null;
                switch (item.getItemId()){
                    case R.id.appBar_personn:
                        fragment = new PersonFragment();
                        break;
                    case R.id.appBar_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.appBar_message:
                        fragment = new MessageFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.principal_page, fragment).commit();
                return true;
            }
        });
    }

}
