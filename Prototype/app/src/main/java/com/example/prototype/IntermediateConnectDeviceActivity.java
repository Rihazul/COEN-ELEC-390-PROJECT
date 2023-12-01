package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class IntermediateConnectDeviceActivity extends BaseActivity {
    private Button notNow;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate_connect_device);
        Objects.requireNonNull(getSupportActionBar()).hide();

        notNow = findViewById(R.id.notNowButton);
        next = findViewById(R.id.nextButton);

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectHomeActivity();
            }
        });
    }

    private void goToSelectHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), SelectHomeActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
