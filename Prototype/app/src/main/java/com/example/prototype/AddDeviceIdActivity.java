package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

public class AddDeviceIdActivity extends BaseActivity {

    private ImageButton backButton;
    private Button manualDeviceIdButton;
    private Button cancelButton;
    private Button QRCodeScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device_device_id);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String homeId = getIntent().getStringExtra("homeId");

        manualDeviceIdButton = findViewById(R.id.manualDeviceIdButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);
        QRCodeScanButton = findViewById(R.id.buttonScan);

        manualDeviceIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddDeviceManually(homeId);
            }
        });
        QRCodeScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(AddDeviceIdActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                //integrator.setCaptureActivity(CaptureQRcodePortrait.class);
                integrator.setPrompt(getString(R.string.scan_a_qr_code));
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, (R.string.cancelled), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.scanned) + result.getContents(), Toast.LENGTH_LONG).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String deviceId = result.getContents();
                    String homeId = getIntent().getStringExtra("homeId");

                    goToConnectDeviceActivity(homeId, deviceId);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void goToAddDeviceManually(String homeId) {
        Intent intent = new Intent(getApplicationContext(), AddDeviceManuallyActivity.class);
        intent.putExtra("homeId", homeId);
        startActivity(intent);
    }

    private void goToConnectDeviceActivity(String homeId, String deviceId) {
        Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
        intent.putExtra("homeId", homeId);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }
}
