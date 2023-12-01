package com.example.prototype;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class LiveAlertsDetailsActivity extends BaseActivity {
    private VideoView videoView;
    private TextView dateTextView;
    private TextView timeTextView;
    //private TextView motionSensorTextView;
    //private TextView usSensorTextView;
    private ImageButton backButton;
    private ImageView videoThumbnail;

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

        Glide.with(this)
                .load(videoURL)
                .preload();

        videoView = findViewById(R.id.videoView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        //motionSensorTextView = findViewById(R.id.motionSensorTextView);
        //usSensorTextView = findViewById(R.id.usSensorTextView);
        backButton = findViewById(R.id.backButton);
        videoThumbnail = findViewById(R.id.videoThumbnail);

        Glide.with(this)
                .asBitmap()
                .load(videoURL)
                .into(videoThumbnail);

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

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    videoThumbnail.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

        dateTextView.setText(dateVideo);
        timeTextView.setText(timeVideo);
        /*if (dateMotionSensor.equals(dateVideo) && timeMotionSensor.equals(timeVideo)) {
            motionSensorTextView.setText("Detected Motion");
        } else {
            motionSensorTextView.setText("Did not detect motion");
        }

        if (dateUSSensor.equals(dateVideo) && timeUSSensor.equals(timeVideo)) {
            usSensorTextView.setText("Detected Motion");
        } else {
            usSensorTextView.setText("Did not detect motion");
        }*/
    }
}
