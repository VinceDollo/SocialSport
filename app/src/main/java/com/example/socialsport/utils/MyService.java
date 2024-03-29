package com.example.socialsport.utils;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.socialsport.R;
import com.example.socialsport.activities.PrincipalPageActivity;
import com.example.socialsport.entities.SportActivity;
import com.example.socialsport.utils.Utils;

public class MyService extends IntentService {

    private static final String CHANNEL_ID = "My notification";
    private static final int NOTIFICATION_ID = 1;

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent newIntent = new Intent(this, PrincipalPageActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, PendingIntent.FLAG_IMMUTABLE);

        createNotificationChannel();

        //TODO: extract this string
        SportActivity nextActivity = Utils.getNextActivity();
        StringBuilder text = new StringBuilder();
        text.append("You have an activity ");
        text.append(nextActivity.getSport());
        text.append(" at ");
        text.append(nextActivity.getTime());
        text.append(" the ");
        text.append(nextActivity.getDate());
        text.append(".");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Activity in coming")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
