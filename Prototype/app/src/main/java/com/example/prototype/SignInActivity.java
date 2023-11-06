package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private TextInputLayout passwordInputLayout;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private CheckBox rememberMeButton;
    private Button forgotPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        rememberMeButton = findViewById(R.id.rememberMeButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (validateLoginInformation(email, password)) {
                    signInUser(email, password);
                } else {
                    Toast.makeText(SignInActivity.this, "Please enter valid email and password", Toast.LENGTH_LONG).show();
                }
            }
        });

        rememberMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean rememberMe = rememberMeButton.isChecked();
                SharedPreferences preferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("RememberMe", rememberMe);
                editor.apply();
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add a forgot password option
            }
        });
    }

    private boolean validateLoginInformation(String email, String password) {
        boolean isValid = true;

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            emailInput.setError("Email cannot be empty.");
            isValid = false;
        } else if (!email.matches(emailPattern)) {
            emailInput.setError("Please enter a valid email address.");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordInputLayout.setError("Password cannot be empty.");
            isValid = false;
        } else if (password.length() < 8) {
            passwordInputLayout.setError("Password is too short.");
            isValid = false;
        }

        return isValid;
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToMainActivity();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(SignInActivity.this, "Account not found. Please sign up.", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(SignInActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
