package com.example.prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
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
    private ProgressBar progressIndicator;

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
        progressIndicator = findViewById(R.id.progress_sign_in);

        progressIndicator.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                progressIndicator.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (validateLoginInformation(email, password)) {
                            signInUser(email, password);
                        } else {
                            Toast.makeText(SignInActivity.this, R.string.please_enter_valid_email_and_password, Toast.LENGTH_LONG).show();
                            progressIndicator.setVisibility(View.GONE);
                        }
                    }
                },4000);
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
                setForgotPasswordActivity();
            }
        });
    }

    private boolean validateLoginInformation(String email, String password) {
        boolean isValid = true;

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            emailInput.setError(getString(R.string.email_cannot_be_empty));
            isValid = false;
        } else if (!email.matches(emailPattern)) {
            emailInput.setError(getString(R.string.please_enter_a_valid_email_address));
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordInputLayout.setError(getString(R.string.password_cannot_be_empty));
            isValid = false;
        } else if (password.length() < 8) {
            passwordInputLayout.setError(getString(R.string.password_is_too_short));
            isValid = false;
        }

        return isValid;
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if(mAuth.getCurrentUser().isEmailVerified())
                            {goToAddHomeActivity();}
                        else {
                            Toast.makeText(SignInActivity.this, R.string.please_verify_your_email_address, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(SignInActivity.this, R.string.account_not_found_please_sign_up, Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(SignInActivity.this, R.string.invalid_credentials_please_try_again, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToAddHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), AddHomeActivity.class);
        intent.putExtra("Source", "Sign Up");
        startActivity(intent);
        finish();
    }

    private void setForgotPasswordActivity(){
        Intent intent= new Intent(getApplicationContext(),ForgetPassword.class);
        startActivity(intent);
        finish();
    }
}
