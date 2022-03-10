package com.example.socialsport;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PrincipalPageActivity extends AppCompatActivity {
    EditText et_localisation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_page_activity);

        Fragment fragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_map,fragment).commit();
        et_localisation = (EditText) findViewById(R.id.et_search_city);

        et_localisation.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //Save the answer
                String localisation=et_localisation.getText().toString();
                // Perform action on key press
                Toast.makeText(this, localisation, Toast.LENGTH_SHORT).show();
                //Delete text from edit text
                et_localisation.setText("");
                return true;
            }
            return false;
        });
    }
}
