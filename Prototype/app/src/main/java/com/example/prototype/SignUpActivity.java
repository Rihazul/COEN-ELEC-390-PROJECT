package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button createAccountButton;
    private CheckBox rememberMeButton;
    private Button alreadyHaveAccountButton;
    private FirebaseAuth mAuth;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        createAccountButton = findViewById(R.id.createAccountButton);
        rememberMeButton = findViewById(R.id.rememberMeButton);
        alreadyHaveAccountButton = findViewById(R.id.alreadyHaveAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                User user = new User(firstName, lastName, email, password);

                if (validateInformation(user, confirmPassword)) {
                    saveUserInDatabase(user);
                } else {
                    Toast.makeText(SignUpActivity.this, "Invalid Information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
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

        alreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignInActivity();
            }
        });

    }

    private boolean validateInformation(User user, String confirmPassword) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        boolean isValid = true;

        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty()) {
            firstNameInput.setError("Name can not be empty!");
            lastNameInput.setError("Name can not be empty!");
            isValid =  false;
        }

        if (!user.getEmail().matches(emailPattern)) {
            emailInput.setError("Please enter a valid email address!");
            isValid = false;
        }

        if (user.getPassword().length() < 8) {
            passwordInputLayout.setError("Password must be at least 8 characters");
            isValid = false;
        }

        if (!user.getPassword().matches(".*[0-9].*")) {
            passwordInputLayout.setError("Password must contain at least one digit.");
            isValid = false;
        }

        if (!user.getPassword().matches(".*[A-Z].*")) {
            passwordInputLayout.setError("Password must contain at least one uppercase letter.");
            isValid = false;
        }

        if (!user.getPassword().matches(".*[a-z].*")) {
            passwordInputLayout.setError("Password must contain at least one lowercase letter.");
            isValid = false;
        }

        if (!user.getPassword().matches(".*[!@#$%^&*+=?~_<>-].*")) {
            passwordInputLayout.setError("Password must contain at least one valid special character (!@#$%^&*+=?-).");
            isValid = false;
        }

        if (!user.getPassword().equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match.");
             isValid = false;
        }

        return isValid;
    }

    private void saveUserInDatabase(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(SignUpActivity.this,"Registered Successfully. Please check your email for verification",Toast.LENGTH_LONG).show();
                                    emailInput.setText("");
                                    passwordInput.setText("");
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                        if (firebaseUser != null) {
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            User userInfo = new User(user.getFirstName(), user.getLastName(), user.getEmail());

                            rootRef.child("users").child(firebaseUser.getUid()).setValue(userInfo)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            //goToAddHomeActivity();
                                            Toast.makeText(SignUpActivity.this,"Verify the email before joining in!",Toast.LENGTH_LONG).show();
                                            goToSignInActivity();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Failed to save user profile information. Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            emailInput.setError("An account with this email already exists!");
                            Toast.makeText(SignUpActivity.this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void goToAddHomeActivity() {
//        Intent intent = new Intent(getApplicationContext(), AddHomeActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private void goToSignInActivity() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}
