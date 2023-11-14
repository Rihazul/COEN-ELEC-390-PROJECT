package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    interface FirstHomeCheckCallback {
        void onCheckCompleted(boolean isFirstHome);
    }

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

        determineIfFirstHome(isFirstHome -> {
            this.isFirstHome = isFirstHome;

            String source = getIntent().getStringExtra("Source");
            if (source.equals("Sign Up") && !this.isFirstHome) {
                goToMainActivity();
            }

            setupUIForFirstHome(this.isFirstHome);

            addHomeButton.setOnClickListener(view -> {
                String homeName = homeNameInput.getText().toString();
                saveHome(homeName);
                if (this.isFirstHome) {
                    //TODO implement connect device activity
                    //goToConnectDeviceActivity();
                    goToMainActivity();
                } else {
                    goToMainActivity();
                }
            });
        });

        cancelButton.setOnClickListener(view -> {
            goToMainActivity();
            finish();
        });
    }

    private void determineIfFirstHome(FirstHomeCheckCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        DatabaseReference userHomesRef = FirebaseDatabase.getInstance().getReference("users")
                .child(uid).child("homes");

        userHomesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFirstHome = !dataSnapshot.exists() || !dataSnapshot.hasChildren();
                callback.onCheckCompleted(isFirstHome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AddHomeActivity", "checkIfFirstHome:onCancelled", databaseError.toException());
            }
        });
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
