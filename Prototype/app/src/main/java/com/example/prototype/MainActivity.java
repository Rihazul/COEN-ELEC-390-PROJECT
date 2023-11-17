package com.example.prototype;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private Button liveAlertsButton;
    private Button connectDeviceButton;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        liveAlertsButton = findViewById(R.id.liveAlertsButton);
        connectDeviceButton = findViewById(R.id.connectDeviceButton);

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

        liveAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLiveAlertsActivity();
            }
        });

        connectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToConnectDeviceActivity();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.homeSettings) {

        } else if (id == R.id.addHome) {
            goToAddHomeActivity();
        } else if (id == R.id.profileInformation) {

        } else if (id == R.id.settings) {

        } else if (id == R.id.logout) {
            new LogoutConfirmationDialogFragment().show(getSupportFragmentManager(), "LogoutConfirmationDialogFragment");
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToMotionSensorDataActivity() {
        Intent intent = new Intent(getApplicationContext(), MotionSensorDataActivity.class);
        startActivity(intent);
    }

    private void goToUSSensorDataActivity() {
        Intent intent = new Intent(getApplicationContext(), USSensorDataActivity.class);
        startActivity(intent);
    }

    private void goToCameraFootageActivity() {
        Intent intent = new Intent(getApplicationContext(), CameraFootageActivity.class);
        startActivity(intent);
    }

    private void goToLiveAlertsActivity() {
        Intent intent = new Intent(getApplicationContext(), LiveAlertsListActivity.class);
        startActivity(intent);
    }

    private void goToConnectDeviceActivity() {
        Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
        startActivity(intent);
    }

    private void goToAddHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), AddHomeActivity.class);
        intent.putExtra("Source", "Main");
        startActivity(intent);
        finish();
    }

}


