package com.example.prototype;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CameraFootageDetailsActivity extends AppCompatActivity {

    private  VideoView videoView;
    private TextView dateView;
    private TextView timeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_footage_details);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String videoURL = getIntent().getStringExtra("videoURL");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");

        videoView = findViewById(R.id.videoDetailView);
        dateView = findViewById(R.id.dateView);
        timeView = findViewById(R.id.timeView);

        videoView.setVideoURI(Uri.parse(videoURL));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoURL));

        dateView.setText(date);
        timeView.setText(time);
    }
}
