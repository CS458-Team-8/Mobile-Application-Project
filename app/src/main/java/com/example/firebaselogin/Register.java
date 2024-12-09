package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        findViewById(R.id.btn_register).setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new admin user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveAdminToFirestore(user.getUid(), email);
                            }
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void saveAdminToFirestore(String userId, String email) {
        // Generate a unique group ID for the admin
        String groupId = db.collection("groups").document().getId();

        // Save admin user data
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("email", email);
        adminData.put("role", "admin");
        adminData.put("adminGroup", groupId);
        adminData.put("fcmToken", "");

        db.collection("users").document(userId).set(adminData)
                .addOnSuccessListener(aVoid -> {
                    // Save group metadata
                    Map<String, Object> groupData = new HashMap<>();
                    groupData.put("groupName", "Group for " + email);
                    groupData.put("createdBy", userId);

                    db.collection("groups").document(groupId).set(groupData);
                    Toast.makeText(this, "Admin registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Get new FCM token
        FirebaseMessaging.getInstance().getToken();
        finish();
    }
}
