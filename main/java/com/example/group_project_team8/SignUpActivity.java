package com.example.group_project_team8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button signUpButton;
    private TextView loginRedirectText, passwordRequirementText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        passwordRequirementText = findViewById(R.id.passwordRequirementText);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Sign up button click listener
        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                if (password.length() >= 6) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Sign up successful! Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                    // Send verification email
                                    firebaseAuth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(verificationTask -> {
                                                if (verificationTask.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });

        // Redirect to LoginActivity
        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}
