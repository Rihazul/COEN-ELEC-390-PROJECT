package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private Button saveButton;
    private Button editPasswordButton;
    private ImageButton backButton;

    private void getCurrentUserFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Set user details to the corresponding EditText fields
                        editFirstName.setText(user.getFirstName());
                        editLastName.setText(user.getLastName());
                        editEmail.setText(user.getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error
                    Toast.makeText(ProfileActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        saveButton = findViewById(R.id.saveButton);
        editPasswordButton = findViewById(R.id.editPasswordButton);
        backButton = findViewById(R.id.backButton);

        // Call the method to retrieve and set user details
        getCurrentUserFromFirebase();

        // Set a click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the edited user details to your data source (e.g., Firebase)
                // You may want to add validation before saving the data
                updateUserDetails();
            }
        });
        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the "Edit Password" dialog
                EditPasswordDialog editPasswordDialog = new EditPasswordDialog(ProfileActivity.this);
                editPasswordDialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });
    }

    private void updateUserDetails() {
        String newFirstName = editFirstName.getText().toString().trim();
        String newLastName = editLastName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();

        // Validate the input data (you can reuse your existing validation logic)
        if (validateUpdatedInformation(newFirstName, newLastName, newEmail)) {
            if (isEmailValid(newEmail)) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // Update the user details in Firebase
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
                    userReference.child("firstName").setValue(newFirstName);
                    userReference.child("lastName").setValue(newLastName);
                    userReference.child("email").setValue(newEmail);

                    // Display a success message to the user
                    Toast.makeText(ProfileActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Display an error message for invalid email
                Toast.makeText(ProfileActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Display an error message for other invalid information
            Toast.makeText(ProfileActivity.this, "Invalid information, please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updatePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Reauthenticate the user with their current password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reauthentication succeeded, now update the password
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Password updated successfully
                                                Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Handle the error
                                                Toast.makeText(ProfileActivity.this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Reauthentication failed, handle the error
                            Toast.makeText(ProfileActivity.this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean validateUpdatedInformation(String newFirstName, String newLastName, String newEmail) {
        // Implement your validation logic here (similar to the validation in SignUpActivity)
        // Return true if the information is valid, false otherwise.
        // You may want to display error messages for invalid fields.
        // Make sure to consider your specific requirements for validation.
        // For example, you may allow updating the email only if it's a valid email address.
        // Similarly, you may have specific rules for the password.

        // Sample validation:
        return !newFirstName.isEmpty() && !newLastName.isEmpty() && !newEmail.isEmpty();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
