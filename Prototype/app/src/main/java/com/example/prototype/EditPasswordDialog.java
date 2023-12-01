package com.example.prototype;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditPasswordDialog extends Dialog {

    private final EditText currentPasswordEditText;
    private final EditText newPasswordEditText;
    private final EditText confirmPasswordEditText;
    private final Button updatePasswordButton;

    public EditPasswordDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_edit_password);

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the password update logic here
                handlePasswordUpdate();
            }
        });
    }

    private void handlePasswordUpdate() {
        // Get the entered passwords
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate the entered passwords
        if (validatePasswords(currentPassword, newPassword, confirmPassword)) {
            // For demonstration purposes, show a toast message
            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();

            // Dismiss the dialog
            dismiss();
        } else {
            // Display an error message for invalid passwords
            Toast.makeText(getContext(), "Invalid passwords, please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

//    private boolean validatePasswords(String currentPassword, String newPassword, String confirmPassword) {
//        // Check if the entered current password is correct
//        if (!currentPassword.equals("yourCurrentPassword")) {
//            return false;
//        }
//
//        // Check if the new password and confirm password match
//        if (!newPassword.equals(confirmPassword)) {
//            return false;
//        }
//
//        // For demonstration purposes, a simple check is performed
//        return newPassword.length() >= 8 &&
//                newPassword.matches(".*\\d.*") &&
//                newPassword.matches(".*[A-Z].*") &&
//                newPassword.matches(".*[a-z].*") &&
//                newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?].*");
//    }

    private boolean validatePasswords(String currentPassword, String newPassword, String confirmPassword) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the entered current password matches the actual current password
        if (currentUser != null && !TextUtils.isEmpty(currentPassword)) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // The entered current password is correct, now check other conditions
                                if (newPassword.equals(confirmPassword) &&
                                        newPassword.length() >= 8 &&
                                        newPassword.matches(".*\\d.*") &&
                                        newPassword.matches(".*[A-Z].*") &&
                                        newPassword.matches(".*[a-z].*") &&
                                        newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?].*")) {
                                    // Password is valid
                                    // Implement your password update logic here
                                    // ...

                                    // Dismiss the dialog
                                    dismiss();
                                } else {
                                    // Display an error message for invalid passwords
                                    Toast.makeText(getContext(), "Invalid passwords, please check and try again", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Display an error message for incorrect current password
                                Toast.makeText(getContext(), "Incorrect current password, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return false; // Default return, should never reach here
    }

}

