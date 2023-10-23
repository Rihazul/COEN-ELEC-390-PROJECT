package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button camFootageButton;
    private Button connectDeviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        camFootageButton = findViewById(R.id.camFootageButton);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);

        camFootageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCameraFootageActivity();
            }
        });

        connectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToConnectDeviceActivity();
            }
        });
    }

    private void goToCameraFootageActivity() {
        Intent intent = new Intent(getApplicationContext(), CameraFootageActivity.class);
        startActivity(intent);
    }

    private void goToConnectDeviceActivity() {
        Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
        startActivity(intent);
    }
}


