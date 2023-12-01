package com.example.prototype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationHandler {

    private static final String CHANNEL_ID = "alert_channel";

    public static void sendNotification(Context context, String title, String message) {
        try {
            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon_background)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon_background))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent intent = new Intent(context, LiveAlertsListActivity.class);
            PendingIntent pendingIntent;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            builder.setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = generateUniqueId();
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
            hasNotificationPermission(context);
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static boolean hasNotificationPermission(Context context) {

        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    private static int generateUniqueId() {
        return (int) (System.currentTimeMillis() + new Random().nextInt(100));
    }

    private static void showNotificationPermissionExplanation(Context context) {

        Toast.makeText(context, (R.string.please_enable_notifications_for_this_app_in_the_device_settings), Toast.LENGTH_LONG).show();

    }
}
