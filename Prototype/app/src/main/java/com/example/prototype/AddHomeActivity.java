package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddHomeActivity extends BaseActivity {

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

        String isFirstHomeString = getIntent().getStringExtra("isFirstHome");

        firstHomeTitle = findViewById(R.id.createFirstHomeTitle);
        homeNameInput = findViewById(R.id.homeNameInput);
        cancelButton = findViewById(R.id.cancelButton);
        addHomeButton = findViewById(R.id.addHomeButton);

        isFirstHome = isFirstHomeString.equals("True");

        setupUIForFirstHome(isFirstHome);

        addHomeButton.setOnClickListener(view -> {
            String homeName = homeNameInput.getText().toString();
            saveHome(homeName);
            if (this.isFirstHome) {
                goToSelectHomeActivity();
            } else {
                goToMainActivity();
            }
        });

        cancelButton.setOnClickListener(view -> {
            goToMainActivity();
            finish();
        });
    }

    private void setupUIForFirstHome(boolean isFirstHome) {
        if (!isFirstHome) {
            cancelButton.setVisibility(View.VISIBLE);
            firstHomeTitle.setVisibility(View.GONE);
        } else {
            cancelButton.setVisibility(View.GONE);

            int dpValue = 325;
            float d = getResources().getDisplayMetrics().density;
            int width = (int) (dpValue * d);

            ViewGroup.LayoutParams params = addHomeButton.getLayoutParams();
            params.width = width;
            addHomeButton.setLayoutParams(params);
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

    private void goToSelectHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), IntermediateConnectDeviceActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
