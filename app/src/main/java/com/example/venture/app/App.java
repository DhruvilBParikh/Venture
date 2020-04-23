package com.example.venture.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class App extends Application {

    private static final String TAG = "App";

    public static final String CHANNEL_ID = "NewEventChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        // create notification channel
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Event",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("A new event is added");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "createNotificationChannel: notification channel created");
        }
    }

}
