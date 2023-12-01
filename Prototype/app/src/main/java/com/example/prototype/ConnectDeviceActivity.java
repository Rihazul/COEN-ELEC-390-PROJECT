package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ConnectDeviceActivity extends BaseActivity {

    private EditText deviceNameInput;
    private Button connectDeviceButton;
    private Button cancelButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String homeId = getIntent().getStringExtra("homeId");
        String deviceId = getIntent().getStringExtra("deviceId");

        deviceNameInput = findViewById(R.id.deviceNameInput);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);

        connectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceName = deviceNameInput.getText().toString();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("homes").child(homeId).child("devices").child(deviceId).setValue(deviceName);
                ref.child("devices").child(deviceId);

                goToMainActivity();
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
}
