package com.example.prototype;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LiveAlertsListActivity extends BaseActivity {

    private ImageButton backButton;
    private TextView titleTextView;
    private final Comparator<LiveAlert> liveAlertComparator = new Comparator<LiveAlert>() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_alerts_list_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String deviceId = getIntent().getStringExtra("deviceId");
        String deviceName = getIntent().getStringExtra("deviceName");

        backButton = findViewById(R.id.backButton);
        titleTextView = findViewById(R.id.titleTextView);

        titleTextView.setText(deviceName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchVideosAndSensorsFromFirebase(deviceId);
    }

    private void fetchVideosAndSensorsFromFirebase(String deviceId) {
        DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("devices/" + deviceId + "/recordings");

        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LiveAlert> liveAlerts = new ArrayList<>();

                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    CameraFootage cameraFootage = videoSnapshot.getValue(CameraFootage.class);
                    if (cameraFootage != null) {
                        LiveAlert liveAlert = new LiveAlert();
                        liveAlert.setVideo(cameraFootage);

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
}