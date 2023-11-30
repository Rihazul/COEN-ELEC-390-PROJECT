package com.example.prototype;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

        devicesRecyclerView = findViewById(R.id.devicesRecyclerView);
        devicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new DevicesAdapter(this, new ArrayList<>(), new DevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String deviceName = adapter.getDeviceNameAtPosition(position);
                goToLiveAlertsActivity();
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
                for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                    String deviceName = deviceSnapshot.getValue(String.class);
                    deviceNamesList.add(deviceName);
                }
                adapter.updateDevicesList(deviceNamesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MainActivity", "loadDevices:onCancelled", databaseError.toException());
            }
        });
    }

    private void goToLiveAlertsActivity() {
        Intent intent = new Intent(getApplicationContext(), LiveAlertsListActivity.class);
        startActivity(intent);
    }

    private void goToSelectHomeActivity(String homeId) {
        Intent intent = new Intent(getApplicationContext(), SelectHomeActivity.class);
        intent.putExtra("homeId", homeId);
        startActivity(intent);
    }



    private void goToAddHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), AddHomeActivity.class);
        intent.putExtra("Source", "Main");
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

    private void goToFaceScan()
    {
        Intent intent = new Intent(getApplicationContext(), Face_Scan.class);
        intent.putExtra("CAMERA DIRECTION", "Face Scan");
        startActivity(intent);
        finish();
    }
}


