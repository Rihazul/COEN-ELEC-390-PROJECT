package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private Button motionSensorButton;
    private Button logoutButton;
    private Button usSensorButton;
    private Button camFootageButton;
    private Button liveAlertsButton;
    private Button connectDeviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //motionSensorButton = findViewById(R.id.motionSensorButton);
        logoutButton = findViewById(R.id.logoutButton);
        usSensorButton = findViewById(R.id.usSensorButton);
        camFootageButton = findViewById(R.id.camFootageButton);
        liveAlertsButton = findViewById(R.id.liveAlertsButton);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);

        /*motionSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMotionSensorDataActivity();
            }
        });*/

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the FCM registration token
                    String token = task.getResult();

                    // Log or store the token as needed
                    Log.d("FCM", "FCM Token: " + token);
                    sendTokenToServer(token);
                });


        logoutButton.setOnClickListener(view -> new LogoutConfirmationDialogFragment().show(getSupportFragmentManager(), "LogoutConfirmationDialogFragment"));

        usSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUSSensorDataActivity();
            }
        });

        camFootageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCameraFootageActivity();
            }
        });

        liveAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLiveAlertsActivity();
            }
        });

        connectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToConnectDeviceActivity();
            }
        });
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


    private void goToMotionSensorDataActivity() {
        Intent intent = new Intent(getApplicationContext(), MotionSensorDataActivity.class);
        startActivity(intent);
    }

    private void goToUSSensorDataActivity() {
        Intent intent = new Intent(getApplicationContext(), USSensorDataActivity.class);
        startActivity(intent);
    }

    private void goToCameraFootageActivity() {
        Intent intent = new Intent(getApplicationContext(), CameraFootageActivity.class);
        startActivity(intent);
    }

    private void goToLiveAlertsActivity() {
        Intent intent = new Intent(getApplicationContext(), LiveAlertsListActivity.class);
        startActivity(intent);
    }

    private void goToConnectDeviceActivity() {
        Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
        startActivity(intent);
    }


}


