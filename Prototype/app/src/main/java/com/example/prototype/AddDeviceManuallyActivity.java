package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class AddDeviceManuallyActivity extends AppCompatActivity {

    private EditText deviceCodeInput;
    private Button nextButton;
    private Button cancelButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device_manual_device_id);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String homeId = getIntent().getStringExtra("homeId");

        deviceCodeInput = findViewById(R.id.deviceCodeInput);
        nextButton = findViewById(R.id.nextButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = deviceCodeInput.getText().toString();
                goToConnectDeviceActivity(homeId, deviceId);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void goToConnectDeviceActivity(String homeId, String deviceId) {
        Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
        intent.putExtra("homeId", homeId);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }
}
