package com.example.prototype;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class LiveAlertsListActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_alerts_list_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchVideosAndSensorsFromFirebase();
    }

    private void fetchVideosAndSensorsFromFirebase() {
        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("videos");
        DatabaseReference motionSensorRef = FirebaseDatabase.getInstance().getReference("sensors/motionSensor");
        DatabaseReference usSensorRef = FirebaseDatabase.getInstance().getReference("sensors/usSensor");

        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LiveAlert> liveAlerts = new ArrayList<>();

                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    CameraFootage cameraFootage = videoSnapshot.getValue(CameraFootage.class);
                    if (cameraFootage != null) {
                        LiveAlert liveAlert = new LiveAlert();
                        liveAlert.setVideo(cameraFootage);

                        motionSensorRef.orderByChild("date").equalTo(cameraFootage.getDate())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot motionSnapshot) {
                                        for (DataSnapshot snap : motionSnapshot.getChildren()) {
                                            Sensor motionSensor = snap.getValue(Sensor.class);
                                            if (motionSensor != null && motionSensor.getTime().equals(cameraFootage.getTime())) {
                                                liveAlert.setMotionSensor(motionSensor);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Error fetching motion sensor data", error.toException());
                                    }
                                });

                        usSensorRef.orderByChild("date").equalTo(cameraFootage.getDate())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot usSnapshot) {
                                        for (DataSnapshot snap : usSnapshot.getChildren()) {
                                            Sensor usSensor = snap.getValue(Sensor.class);
                                            if (usSensor != null && usSensor.getTime().equals(cameraFootage.getTime())) {
                                                liveAlert.setUsSensor(usSensor);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Error fetching US sensor data", error.toException());
                                    }
                                });

                        liveAlerts.add(liveAlert);
                    }
                }
                Collections.sort(liveAlerts, liveAlertComparator);
                setupRecyclerView(liveAlerts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching videos", databaseError.toException());
            }
        });
    }

    private void setupRecyclerView(List<LiveAlert> liveAlerts) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LiveAlertsAdapter liveAlertsAdapter = new LiveAlertsAdapter(liveAlerts, LiveAlertsListActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(liveAlertsAdapter);
    }

    private Comparator<LiveAlert> liveAlertComparator = new Comparator<LiveAlert>() {
        @Override
        public int compare(LiveAlert alert1, LiveAlert alert2) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date1 = format.parse(alert1.getVideo().getDate() + " " + alert1.getVideo().getTime());
                Date date2 = format.parse(alert2.getVideo().getDate() + " " + alert2.getVideo().getTime());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    };
}