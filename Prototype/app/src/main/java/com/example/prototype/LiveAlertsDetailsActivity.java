package com.example.prototype;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class LiveAlertsDetailsActivity extends AppCompatActivity {
    private VideoView videoView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView motionSensorTextView;
    private TextView usSensorTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_alert_details);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String videoURL = getIntent().getStringExtra("videoURL");
        String dateVideo = getIntent().getStringExtra("dateVideo");
        String timeVideo = getIntent().getStringExtra("timeVideo");
        String dateMotionSensor = getIntent().getStringExtra("dateMotionSensor");
        String timeMotionSensor = getIntent().getStringExtra("timeMotionSensor");
        String dateUSSensor = getIntent().getStringExtra("dateUSSensor");
        String timeUSSensor = getIntent().getStringExtra("timeUSSensor");

        videoView = findViewById(R.id.videoView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        motionSensorTextView = findViewById(R.id.motionSensorTextView);
        usSensorTextView = findViewById(R.id.usSensorTextView);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        videoView.setVideoURI(Uri.parse(videoURL));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoURL));

        dateTextView.setText(dateVideo);
        timeTextView.setText(timeVideo);
        if (dateMotionSensor.equals(dateVideo) && timeMotionSensor.equals(timeVideo)) {
            motionSensorTextView.setText("Detected Motion");
        } else {
            motionSensorTextView.setText("Did not detect motion");
        }

        if (dateUSSensor.equals(dateVideo) && timeUSSensor.equals(timeVideo)) {
            usSensorTextView.setText("Detected Motion");
        } else {
            usSensorTextView.setText("Did not detect motion");
        }
    }
}
