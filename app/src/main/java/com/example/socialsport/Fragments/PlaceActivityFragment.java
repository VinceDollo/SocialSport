package com.example.socialsport.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialsport.LoginActivity;
import com.example.socialsport.R;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlaceActivityFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton btn_back;
    private Button btn_validate;
    private TextView tv_title;
    private FirebaseAuth mAuth;


    private LatLng current_latLng;
    private String sport;

    private void writeActivityToDatabase(FirebaseDatabase database, String description, String date, String heure, String coords, String currentUserID) {
        DatabaseReference myRef = database.getReference();
        SportActivity newActivity = new SportActivity(description, date, heure, currentUserID,coords);
        myRef.child("activities").push().setValue(newActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_activity, container, false);

        tv_title = view.findViewById(R.id.tv_title);
        btn_validate = view.findViewById(R.id.btn_validate);

        //recupere le sport
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sport = bundle.getString("sport");
            tv_title.setText("Choose location for " + sport);
        }

        //Placer la map
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.f_maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        btn_back = view.findViewById(R.id.btn_back);

        btn_back.setOnClickListener(view1 -> getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddActivityFragment()).commit());
        btn_validate.setOnClickListener(view12 -> {
            //add information to next fragment
            Bundle bundle1 = new Bundle();
            bundle1.putString("sport", sport);
            bundle1.putString("location", String.valueOf(current_latLng));
            Fragment newF = new DescriptionActivityFragment();
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                if (currentUser == null) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(getActivity(), "Sorry, you need to log back in to your account",
                            Toast.LENGTH_SHORT).show();
                }
                Log.d("coords debug poing",String.valueOf(current_latLng));
                Log.d("debug uid client",currentUser.getUid());

                writeActivityToDatabase(database,"","","",String.valueOf(current_latLng), currentUser.getUid());

                newF.setArguments(bundle1);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, newF).addToBackStack(null).commit();
        });

        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        current_latLng = new LatLng(-34, 151);

        MarkerOptions marker = new MarkerOptions();

        BitmapDescriptor icon = checkIcon();
        if (icon != null) {
            //Toast.makeText(getActivity(), icon.toString(), Toast.LENGTH_LONG).show();
            mMap.addMarker(marker.position(current_latLng).title("default").icon(icon));
        } else {
            //Toast.makeText(getActivity(), "Icon not set", Toast.LENGTH_LONG).show();

            mMap.addMarker(marker.position(current_latLng).title("default"));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_latLng));
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(marker.position(latLng).title("Position you choose"));
            current_latLng = latLng;
            //Toast.makeText(getActivity(), "Lat : " + latLng.latitude + " , " + "Long : " + latLng.longitude, Toast.LENGTH_LONG).show();
        });

        //mMap.setOnInfoWindowClickListener(RegActivity.this);

    }

    private BitmapDescriptor checkIcon() {
        BitmapDescriptor icon = null;
        Toast.makeText(getActivity(), sport, Toast.LENGTH_LONG).show();
        switch (sport) {
            case "football":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.img_football);
                break;
            case "tennis":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.img_tennis);
                break;
            case "volley":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.img_volley);
                break;
            case "Soccer":
                icon = bitmapDescriptorFromVector(getActivity(), R.drawable.img_soccer_map);
                break;
        }
        return icon;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}