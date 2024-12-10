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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Spinner spinnerRole;
    Button btnCreateUser, btnBackToAdmin;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
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
            finish();
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

        // Fetch current admin's group ID
        String adminId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(adminId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String adminGroup = document.getString("adminGroup");

                        // Step 1: Create the user in Firebase Authentication
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String userId = task.getResult().getUser().getUid();

                                        // Step 2: Save user in Firestore
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email);
                                        userMap.put("role", role);
                                        userMap.put("adminGroup", adminGroup);
                                        userMap.put("fcmToken", "");

                                        db.collection("users").document(userId).set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(CreateUserActivity.this, AdminDashboard.class));
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(this, "Error creating user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Failed to fetch admin group!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
