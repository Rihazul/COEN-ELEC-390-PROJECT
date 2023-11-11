package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddHomeActivity extends AppCompatActivity {

    private TextView firstHomeTitle;
    private EditText homeNameInput;
    private Button cancelButton;
    private Button addHomeButton;
    private boolean isFirstHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);
        Objects.requireNonNull(getSupportActionBar()).hide();

        firstHomeTitle = findViewById(R.id.createFirstHomeTitle);
        homeNameInput = findViewById(R.id.homeNameInput);
        cancelButton = findViewById(R.id.cancelButton);
        addHomeButton = findViewById(R.id.addHomeButton);

        isFirstHome = determineIfFirstHome();

        setupUIForFirstHome(isFirstHome);

        addHomeButton.setOnClickListener(view -> {
            String homeName = homeNameInput.getText().toString();
            saveHome(homeName);
            if (isFirstHome) {
                //TODO implement connect device activity
                //goToConnectDeviceActivity();
                goToMainActivity();
            } else {
                goToMainActivity();
            }
        });

        cancelButton.setOnClickListener(view -> {
            finish();
        });
    }

    private boolean determineIfFirstHome() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        final boolean[] isFirstHome = {true};
        DatabaseReference userHomesRef = FirebaseDatabase.getInstance().getReference("users")
                .child(uid).child("homes");

        userHomesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                    isFirstHome[0] = true;
                } else {
                    isFirstHome[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AddHomeActivity", "checkIfFirstHome:onCancelled", databaseError.toException());
            }
        });
        return isFirstHome[0];
    }

    private void setupUIForFirstHome(boolean isFirstHome) {
        if (!isFirstHome) {
            cancelButton.setVisibility(View.VISIBLE);
            firstHomeTitle.setVisibility(View.GONE);
        }
    }

    private void saveHome(String homeName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String ownerUID = currentUser.getUid();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

            String homeID = databaseRef.child("homes").push().getKey();
            String accessLevel = "owner";

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/homes/" + homeID + "/members/" + ownerUID, accessLevel);

            childUpdates.put("/users/" + ownerUID + "/homes/" + homeID, homeName);
            databaseRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.d("SaveHome", "Home saved successfully and user set as owner.");
                    } else {
                        Log.e("SaveHome", "Failed to save home and set user as owner.", databaseError.toException());
                    }
                }
            });
        } else {
        }
    }

    private void goToConnectDeviceActivity() {
        Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
        startActivity(intent);
    }
    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
