package com.example.prototype;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private Button liveAlertsButton;
    private FloatingActionButton connectDeviceButton;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private RecyclerView devicesRecyclerView;
    private DevicesAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserInformation();

        final String[] homeId = {""};

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        progressBar = findViewById(R.id.progress_list);
        progressBar.setVisibility(View.GONE);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //liveAlertsButton = findViewById(R.id.liveAlertsButton);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference homesRef = usersRef.child(uid).child("homes");

            homesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> homeNamesList = new ArrayList<>();
                    ArrayList<String> homeIdsList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String homeName = snapshot.getValue(String.class);
                        String homeId = snapshot.getKey();
                        homeNamesList.add(homeName);
                        homeIdsList.add(homeId);
                    }
                    Spinner spinner = findViewById(R.id.spinner);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item, homeNamesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedHomeId = homeIdsList.get(position);
                            homeId[0] = selectedHomeId;
                            fetchDevicesForHome(selectedHomeId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the FCM registration token
                    String token = task.getResult();

                    // Log or store the token as needed
                    Log.d("FCM", "FCM Token: " + token);
                    sendTokenToServer(token);
                });

        devicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        devicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new DevicesAdapter(this, new ArrayList<>(), new DevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String deviceName = adapter.getDeviceNameAtPosition(position);
                String deviceId = adapter.getDeviceIdAtPosition(position);
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goToLiveAlertsActivity(deviceId, deviceName);
                    }
                }, 1000);

            }
        });


        devicesRecyclerView.setAdapter(adapter);

        connectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectHomeActivity(homeId[0]);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    public void getUserInformation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String userFirstName = user.getFirstName();
                        String userLastName = user.getLastName();
                        String userEmail = user.getEmail();

                        View headerView = navigationView.getHeaderView(0);
                        userNameTextView = headerView.findViewById(R.id.navHeaderUserName);
                        userEmailTextView = headerView.findViewById(R.id.navHeaderUserEmail);

                        userNameTextView.setText(userFirstName + " " + userLastName);
                        userEmailTextView.setText(userEmail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("MainActivity", "loadUser:onCancelled", databaseError.toException());
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.homeSettings) {

        } else if (id == R.id.addHome) {
            goToAddHomeActivity();
        } else if (id == R.id.profileInformation) {
            openProfileActivity();
        } else if (id == R.id.settings) {
            openSettingsActivity();
        } else if (id == R.id.aboutUs) {
            goToAboutUsActivity();
        } else if (id == R.id.contactInfo) {
            goToContactInfoActivity();
        } else if (id == R.id.disclaimer) {
            goToDisclaimer();
        } else if (id == R.id.logout) {
            new LogoutConfirmationDialogFragment().show(getSupportFragmentManager(), "LogoutConfirmationDialogFragment");
        } else if (id == R.id.scanFace) {
            goToFaceScan();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchDevicesForHome(String homeId) {
        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("homes").child(homeId).child("devices");
        devicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> deviceNamesList = new ArrayList<>();
                List<String> deviceIdList = new ArrayList<>();
                for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                    String deviceName = deviceSnapshot.getValue(String.class);
                    String deviceId = deviceSnapshot.getKey();
                    deviceNamesList.add(deviceName);
                    deviceIdList.add(deviceId);
                }
                adapter.updateDevicesList(deviceNamesList, deviceIdList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MainActivity", "loadDevices:onCancelled", databaseError.toException());
            }
        });
    }

    private void sendTokenToServer(String token) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Check if the UID is null (user not authenticated)
        if (uid == null) {
            Log.d("FCM", "User is not authenticated. Token not saved.");
            return;
        }

        // Define the path to where you want to store the token in your database
        String tokenPath = "users/" + uid + "/fcmToken";

        // Set the token at the specified path
        databaseRef.child(tokenPath).setValue(token)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FCM", "Token saved successfully");
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    Log.e("FCM", "Failed to save token", e);
                    // Handle the error
                });
    }

    private void goToLiveAlertsActivity(String deviceId, String deviceName) {
        Intent intent = new Intent(getApplicationContext(), LiveAlertsListActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("deviceName", deviceName);
        startActivity(intent);
    }

    private void goToSelectHomeActivity(String homeId) {
        Intent intent = new Intent(getApplicationContext(), SelectHomeActivity.class);
        intent.putExtra("homeId", homeId);
        startActivity(intent);
    }


    private void goToAddHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), AddHomeActivity.class);
        intent.putExtra("isFirstHome", "False");
        startActivity(intent);
        finish();
    }

    private void openProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void goToFaceScan() {
        Intent intent = new Intent(getApplicationContext(), FaceScanActivity.class);
        intent.putExtra("CAMERA DIRECTION", "Face Scan");
        startActivity(intent);
        finish();
    }

    private void goToAboutUsActivity() {
        Intent intent = new Intent(getApplicationContext(), AboutUs.class);
        startActivity(intent);
    }

    private void goToContactInfoActivity() {
        Intent intent = new Intent(getApplicationContext(), ContactInfo.class);
        startActivity(intent);
    }

    private void goToDisclaimer() {
        Intent intent = new Intent(getApplicationContext(), Disclaimer.class);
        startActivity(intent);
    }
}


