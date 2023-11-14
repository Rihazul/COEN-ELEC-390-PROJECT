package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button motionSensorButton;
    private Button logoutButton;
    private Button usSensorButton;
    private Button camFootageButton;
    private Button liveAlertsButton;
    private Button connectDeviceButton;
    private Button addHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //motionSensorButton = findViewById(R.id.motionSensorButton);
        logoutButton = findViewById(R.id.logoutButton);
        //usSensorButton = findViewById(R.id.usSensorButton);
        //camFootageButton = findViewById(R.id.camFootageButton);
        addHomeButton = findViewById(R.id.addHomeButton);
        liveAlertsButton = findViewById(R.id.liveAlertsButton);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);

        /*motionSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMotionSensorDataActivity();
            }
        });*/

        logoutButton.setOnClickListener(view -> new LogoutConfirmationDialogFragment().show(getSupportFragmentManager(), "LogoutConfirmationDialogFragment"));

       /* usSensorButton.setOnClickListener(new View.OnClickListener() {
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
        });*/

        addHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddHomeActivity();
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

    private void goToAddHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), AddHomeActivity.class);
        intent.putExtra("Source", "Main");
        startActivity(intent);
        finish();
    }
}


