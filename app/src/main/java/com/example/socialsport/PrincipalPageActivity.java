package com.example.socialsport;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

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
                //Delete text from edit text
                et_localisation.setText("");
                Geocoder gc = new Geocoder(PrincipalPageActivity.this);
                double locationName;
                try {
                    List<Address> addresses = gc.getFromLocationName(localisation, 1);
                    double lattitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    Toast.makeText(this, Double.toString(lattitude)+" "+Double.toString(longitude), Toast.LENGTH_SHORT).show();
                    LatLng placeToFind = new LatLng(lattitude, longitude);
                    //googleMap.addMarker(new MarkerOptions().position(placeToFind).title(localisation));
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeToFind));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error place doesn't exist", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }
}
