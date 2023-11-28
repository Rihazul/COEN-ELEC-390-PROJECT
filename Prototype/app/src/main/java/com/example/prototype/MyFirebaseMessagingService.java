package com.example.prototype;

import static com.example.prototype.NotificationHandler.sendNotification;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Handle FCM messages received here
        if (remoteMessage.getData().size() > 0) {
            // Handle the data payload of the message
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            // You can handle the data received and trigger a notification
            sendNotification(title, message);
        }

        // Check if the message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            // Handle the notification payload of the message
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();

            // You can handle the notification payload received separately
            sendNotification(notificationTitle, notificationBody);
        }
    }

    private void sendNotification(String title, String messageBody) {
        // Create a notification channel (for Android Oreo and higher)
        String channelId = "alert_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "alert_channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(1, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "New token: " + token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Check if the UID is null (user not authenticated)
        if (uid == null) {
            Log.d("FCM", "User is not authenticated. Token not saved.");
            return;
        }

        // Define the path to where you want to store the token in your database
        String tokenPath = "users/" + uid + "/fcmToken";

        // Set the token at the specified path
        databaseRef.child(tokenPath).setValue(token)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FCM", "Token saved successfully");
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    Log.e("FCM", "Failed to save token", e);
                    // Handle the error
                });
    }
}

