package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button motionSensorButton;
    private Button usSensorButton;
    private Button camFootageButton;
    private Button liveAlertsButton;
    private Button connectDeviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        motionSensorButton = findViewById(R.id.motionSensorButton);
        usSensorButton = findViewById(R.id.usSensorButton);
        camFootageButton = findViewById(R.id.camFootageButton);
        liveAlertsButton = findViewById(R.id.liveAlertsButton);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);

        motionSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMotionSensorDataActivity();
            }
        });

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


