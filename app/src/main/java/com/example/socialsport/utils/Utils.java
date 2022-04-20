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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private static final List<SportActivity> allActivities = new ArrayList<>();

    //ajout mez
    private static final List<SportActivity>socceractivities = new ArrayList<>(), basketactivities = new ArrayList<>(), volleyactivities = new ArrayList<>(), handactivities = new ArrayList<>();


    private static SportActivity nextActivity = new SportActivity();

    public static void uploadImage(Context context, Bitmap bitmap, String uid) {
        if (bitmap != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + uid);

            ref.putBytes(byteArray).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();
                Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            });
        }
    }

    public static Bitmap getUserImageFromDatabase(String uid) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + uid);
        final Bitmap[] userImage = {null};
        imageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(bytes -> userImage[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
                .addOnFailureListener(exception -> Log.e(TAG, exception.getMessage()));
        return userImage[0];
    }

    public static void writeUserIntoDatabase(String email, String name, String age, String uid) {
        User currentUser = new User(email, name, age, null);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(currentUser);
    }

    public static User getUserFromDatabase(String uid) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        User user = new User(null, null, null, null);
        myRef.child("users").child(uid).child("name").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                user.setName(Objects.requireNonNull(task.getResult().getValue()).toString());
            }
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

                //ajout mez
                basketactivities.clear();
                socceractivities.clear();
                volleyactivities.clear();
                handactivities.clear();


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SportActivity act = ds.getValue(SportActivity.class);
                    assert act != null;
                    Log.d(TAG, act.toString());

                    String sport = act.getSport();
                    String description = act.getDescription();
                    String date = act.getDate();
                    String time = act.getTime();
                    String uuidOrganiser = act.getUuidOrganiser();
                    String coords = act.getCoords();
                    ArrayList<String> uuids = (ArrayList<String>) act.getUuids();

                    SportActivity newActivity = new SportActivity(sport, description, date, time, uuidOrganiser, coords);
                    newActivity.setUuids(uuids);
                    allActivities.add(newActivity);

                    //ajout mez
                    if(sport.equals("Soccer")){
                        socceractivities.add(newActivity);
                    }
                    else if(sport.equals("Volley")){
                        volleyactivities.add(newActivity);
                    }
                    else if(sport.equals("Basket")){
                        basketactivities.add(newActivity);
                    }
                    else if(sport.equals("Handball")){
                        handactivities.add(newActivity);
                    }
                }

                List<SportActivity> myActivities = getMyActivities();
                if (!myActivities.isEmpty()) {
                    Log.d(TAG, myActivities.toString());
                    myActivities.sort(Comparator.comparing((SportActivity activity) -> stringToDateTime(activity.getDate(), activity.getTime()))); //Sort my activities by date
                    myActivities = getUpcomingActivities(myActivities);

                    if (!myActivities.isEmpty()) {
                        nextActivity = myActivities.get(0);

                        Intent notifyIntent = new Intent(context, MyReceiver.class);
                        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        //Set an alarm one hour before the next activity
                        int oneHour = 3600 * 1000;
                        alarmManager.set(AlarmManager.RTC_WAKEUP, Objects.requireNonNull(stringToDateTime(nextActivity.getDate(), nextActivity.getTime())).getTime() - oneHour, pendingIntent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "An error occurred while getting the activities");
            }
        });
    }

    public static Date stringToDateTime(String date, String time) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<SportActivity> getUpcomingActivities(List<SportActivity> activities) {
        ArrayList<SportActivity> activitiesToReturn = new ArrayList<>(activities);
        activitiesToReturn.removeIf(activity -> Objects.requireNonNull(stringToDateTime(activity.getDate(), activity.getTime())).before(new Date()));
        return activitiesToReturn;
    }

    public static List<SportActivity> getPastActivities(List<SportActivity> activities) {
        ArrayList<SportActivity> activitiesToReturn = new ArrayList<>(activities);
        activitiesToReturn.removeIf(activity -> Objects.requireNonNull(stringToDateTime(activity.getDate(), activity.getTime())).after(new Date())
                || Objects.requireNonNull(stringToDateTime(activity.getDate(), activity.getTime())).equals(new Date()));
        return activitiesToReturn;
    }


    public static List<SportActivity> getAllActivities() {
        return allActivities;
    }

    //ajout mez
    public static List<SportActivity> getSocceractivities() {
        return socceractivities;
    }

    public static List<SportActivity> getBasketactivities() {
        return basketactivities;
    }

    public static List<SportActivity> getVolleyactivities() {
        return volleyactivities;
    }

    public static List<SportActivity> getHandactivities() {
        return handactivities;
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
