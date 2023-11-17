package com.example.prototype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import java.util.Random;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.PendingIntent;
import android.widget.Toast;
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
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        // Show a message/dialog explaining why the notification permission is needed
        // Here is an example of a toast message directing the user to the app's notification settings

        Toast.makeText(context, "Please enable notifications for this app in the device settings", Toast.LENGTH_LONG).show();

    }
}
