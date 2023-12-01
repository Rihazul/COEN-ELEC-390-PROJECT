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

public class CameraFootageActivity extends BaseActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_footage_list_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchVideosFromFirebase();
    }

    private void fetchVideosFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("videos");
        List<CameraFootage> cameraFootageList = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cameraFootageList.clear();
                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    CameraFootage cameraFootage = videoSnapshot.getValue(CameraFootage.class);
                    if (cameraFootage != null) {
                        cameraFootageList.add(cameraFootage);
                    }
                }
                setupRecyclerView(cameraFootageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching videos", databaseError.toException());
            }
        });
    }

    private void setupRecyclerView(List<CameraFootage> cameraFootageList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        CameraFootageAdapter cameraFootageAdapter = new CameraFootageAdapter(cameraFootageList, CameraFootageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(cameraFootageAdapter);
    }
}