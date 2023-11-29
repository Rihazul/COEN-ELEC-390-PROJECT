package com.example.prototype;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureQRcodePortrait extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_qrcode_portrait);
    }
}