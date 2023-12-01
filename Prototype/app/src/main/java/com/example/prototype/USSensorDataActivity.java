package com.example.prototype;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
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

public class USSensorDataActivity extends BaseActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_sensor_list_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchMotionSensorDataFromFirebase();
    }

    private void fetchMotionSensorDataFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("sensors/usSensor");
        List<Sensor> sensorList = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sensorList.clear();
                for (DataSnapshot sensorSnapshot : dataSnapshot.getChildren()) {
                    Sensor sensor = sensorSnapshot.getValue(Sensor.class);
                    if (sensor != null) {
                        sensorList.add(sensor);
                    }
                }
                setupRecyclerView(sensorList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching us sensor data", databaseError.toException());
            }
        });
    }

    private void setupRecyclerView(List<Sensor> sensorList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SensorAdapter sensorAdapter = new SensorAdapter(sensorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(sensorAdapter);
    }
}
