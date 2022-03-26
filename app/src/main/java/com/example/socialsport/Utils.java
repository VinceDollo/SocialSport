package com.example.socialsport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.socialsport.entities.User;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static void writeUserIntoDatabase(String email, String name, String age, String uid) {
        User currentUser = new User(email, name, age);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(currentUser);
    }

    public static void getUserFromDatabase(String uid) {
        AtomicReference<String> userString = new AtomicReference<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firebase_user", "Error getting data", task.getException());
            } else {
                Log.d("Firebase_user1", String.valueOf(task.getResult().getValue()));
                userString.set(Objects.requireNonNull(task.getResult().getValue()).toString());
            }
        });
        Log.d("Firebase_user2", userString.toString());
    }

    public static LatLng stringToLatLng(String string) {
        String res = string.substring(string.indexOf("(") + 1, string.indexOf(")"));
        String[] latLng = res.split(",");
        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);
        return new LatLng(latitude, longitude);
    }

    public static BitmapDescriptor getBitmapDescriptor(Context context, String sport) {
        BitmapDescriptor icon = null;
        switch (sport) {
            case "Football":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_football);
                break;
            case "Tennis":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_tennis);
                break;
            case "Volleyball":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_volley);
                break;
            case "Soccer":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_soccer);
                break;
            case "Basketball":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_basket);
                break;
            case "Handball":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_hand);
                break;
            case "Running":
                icon = bitmapDescriptorFromVector(context, R.drawable.img_map_run);
                break;
            default:
                break;
        }
        return icon;
    }

    public static Bitmap getBitmap(Context context, String sport) {
        Bitmap icon = null;
        switch (sport) {
            case "Football":
                icon = bitmapFromVector(context, R.drawable.img_map_football);
                break;
            case "Tennis":
                icon = bitmapFromVector(context, R.drawable.img_map_tennis);
                break;
            case "Volleyball":
                icon = bitmapFromVector(context, R.drawable.img_map_volley);
                break;
            case "Soccer":
                icon = bitmapFromVector(context, R.drawable.img_map_soccer);
                break;
            case "Basketball":
                icon = bitmapFromVector(context, R.drawable.img_map_basket);
                break;
            case "Handball":
                icon = bitmapFromVector(context, R.drawable.img_map_hand);
                break;
            case "Running":
                icon = bitmapFromVector(context, R.drawable.img_map_run);
                break;
            default:
                break;
        }
        return icon;
    }

    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private static Bitmap bitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return bitmap;
    }

}
