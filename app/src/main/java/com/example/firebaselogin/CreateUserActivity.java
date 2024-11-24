package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Spinner spinnerRole;
    Button btnCreateUser, btnBackToAdmin;

    FirebaseAuth mAuth; // Main auth instance (admin stays logged in)
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance(); // Main auth instance (admin stays logged in)
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        btnBackToAdmin = findViewById(R.id.btnBackToAdmin);

        // Set up role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Handle user creation
        btnCreateUser.setOnClickListener(this::createUser);

        // Navigate back to Admin Dashboard
        btnBackToAdmin.setOnClickListener(view -> {
            Intent intent = new Intent(CreateUserActivity.this, AdminDashboard.class);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

    public void createUser(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Create the user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Step 2: Once the user is created in Firebase Authentication, add them to Firestore
                        String userId = task.getResult().getUser().getUid(); // Get the UID of the newly created user

                        // Save user data to Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("role", role); // Save role for the new user

                        db.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                    // Step 3: Navigate back to Admin Dashboard without signing in the user
                                    startActivity(new Intent(CreateUserActivity.this, AdminDashboard.class));
                                    finish(); // Close current activity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error saving user to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Error creating user in Firebase Authentication: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
