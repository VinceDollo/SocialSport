package com.example.socialsport.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.socialsport.MyReceiver;
import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.entities.User;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private static final List<SportActivity> allActivities = new ArrayList<>();
    private static SportActivity nextActivity = new SportActivity();

    public static void uploadImage(String image, String uid) {
        if (image != null) {
            FirebaseDatabase.getInstance().getReference().child(TableKeys.USERS).child(uid).child(TableKeys.USERS_IMAGE).setValue(image);
        }
    }


    public static void writeUserIntoDatabase(String email, String name, String age, String uid) {
        User currentUser = new User(email, name, age, null);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(currentUser);
    }

    public static User getUserFromDatabase(String uid) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Log.d("TEST", myRef.child(TableKeys.USERS).get().toString());
        User user = new User(null, null, null, null);
        myRef.child(TableKeys.USERS).child(uid).child(TableKeys.USER_NAME_KEY).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                user.setName(Objects.requireNonNull(task.getResult().getValue()).toString());
            }
        }).addOnFailureListener(e -> {
            Log.e("GROSTAG", " "  + e.getMessage());
        });
        myRef.child("users").child(uid).child("email").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                user.setEmail(Objects.requireNonNull(task.getResult().getValue()).toString());

            }
        });
        myRef.child("users").child(uid).child("age").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                user.setAge(Objects.requireNonNull(task.getResult().getValue()).toString());
            }
        });
        myRef.child("users").child(uid).child("image").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                if(task.getResult().getValue()!=null){
                    user.setProfileImage(Objects.requireNonNull(task.getResult().getValue()).toString());
                    Log.d("QUOI ?", user.getProfileImage());

                }else{
                    Log.d("QUOI ?", "null");
                }
            }
        });
        return user;
    }

    public static void writeActivityToDatabase(FirebaseDatabase database, String sport, String
            description, String date, String hour, String coords, String currentUserID) {
        DatabaseReference myRef = database.getReference();
        SportActivity newActivity = new SportActivity(sport, description, date, hour, currentUserID, coords);
        myRef.child("activities").push().setValue(newActivity);
    }

    public static void setActivitiesListenerFromDatabase(Context context) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference activitiesRef = rootRef.child("activities");

        activitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allActivities.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SportActivity act = ds.getValue(SportActivity.class);
                    assert act != null;
                    Log.d(TAG, act.toString());

                    String sport = act.getSport();
                    String description = act.getDescription();
                    String date = act.getDate();
                    String hour = act.getHour();
                    String uuidOrganiser = act.getUuidOrganiser();
                    String coords = act.getCoords();
                    ArrayList<String> uuids = (ArrayList<String>) act.getUuids();

                    SportActivity newActivity = new SportActivity(sport, description, date, hour, uuidOrganiser, coords);
                    newActivity.setUuids(uuids);
                    allActivities.add(newActivity);
                }

                List<SportActivity> myActivities = getMyActivities();
                if (!myActivities.isEmpty()) {
                    myActivities.sort(Comparator.comparing(SportActivity::getDateTime)); //Sort my activities by date
                    myActivities = getUpcomingActivities(myActivities);

                    if (!myActivities.isEmpty()) {
                        nextActivity = myActivities.get(0);

                        Intent notifyIntent = new Intent(context, MyReceiver.class);
                        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        //Set an alarm one hour before the next activity
                        int oneHour = 3600 * 1000;
                        alarmManager.set(AlarmManager.RTC_WAKEUP, nextActivity.getDateTime().getTime() - oneHour, pendingIntent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "An error occurred while getting the activities");
            }
        });
    }

    public static List<SportActivity> getUpcomingActivities(List<SportActivity> activities) {
        ArrayList<SportActivity> activitiesToReturn = new ArrayList<>(activities);
        activitiesToReturn.removeIf(activity -> activity.getDateTime().before(new Date()));
        return activitiesToReturn;
    }

    public static List<SportActivity> getPastActivities(List<SportActivity> activities) {
        ArrayList<SportActivity> activitiesToReturn = new ArrayList<>(activities);
        activitiesToReturn.removeIf(activity -> activity.getDateTime().after(new Date()) || activity.getDateTime().equals(new Date()));
        return activitiesToReturn;
    }

    public static List<SportActivity> getMyActivities() {
        ArrayList<SportActivity> myActivities = new ArrayList<>();
        for (SportActivity currentAct : allActivities) {
            ArrayList<String> uids = (ArrayList<String>) currentAct.getUuids();
            if (!uids.isEmpty() && uids.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                myActivities.add(currentAct);
            }
        }
        return myActivities;
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static SportActivity getNextActivity() {
        return nextActivity;
    }

    public static LatLng stringToLatLng(String string) {
        String res = string;
        if (string.contains("(") && string.contains(")")) {
            res = string.substring(string.indexOf("(") + 1, string.indexOf(")"));
        }
        String[] latLng = res.split(",");
        double latitude = Double.parseDouble(latLng[0]);
        double longitude = Double.parseDouble(latLng[1]);
        return new LatLng(latitude, longitude);
    }

    public static String latLngToString(LatLng latLng) {
        String res = latLng.toString();
        res = res.substring(res.indexOf("(") + 1, res.indexOf(")"));
        return res;
    }

    public static String getPrintableLocation(Activity activity, String location) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        LatLng locationLatLng = Utils.stringToLatLng(location);
        StringBuilder locationString = new StringBuilder();
        try {
            Address locationAddress = geocoder.getFromLocation(locationLatLng.latitude, locationLatLng.longitude, 1).get(0);
            int i = 0;
            while (locationAddress.getAddressLine(i) != null) {
                locationString.append(locationAddress.getAddressLine(i));
                locationString.append(", ");
                i++;
            }
            locationString.delete(locationString.length() - 2, locationString.length() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationString.toString();
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
            case "Start":
                icon = bitmapDescriptorFromVector(context, R.drawable.ic_baseline_flag_24);
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

    public static void toast(Context c,String a){
        Toast.makeText(c, a, Toast.LENGTH_SHORT).show();
    }



}
