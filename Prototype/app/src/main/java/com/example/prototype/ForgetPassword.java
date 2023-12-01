package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPassword extends BaseActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private EditText editText1;
    private Button button_reset;
    private Button button_back;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Objects.requireNonNull(getSupportActionBar()).hide();

        editText1 = findViewById(R.id.reset_email);
        button_reset = findViewById(R.id.reset_password);
        button_back = findViewById(R.id.go_back);
        progressBar = findViewById(R.id.progress_forget);

        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Email = editText1.getText().toString().trim();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(Email)) {
                            ResetPassword();
                        } else {
                            editText1.setError(getString(R.string.email_field_can_t_be_empty));
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, 2000);
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void ResetPassword() {
        mAuth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgetPassword.this, (R.string.reset_password_link_has_been_sent_to_your_email_registered), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetPassword.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgetPassword.this, getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}