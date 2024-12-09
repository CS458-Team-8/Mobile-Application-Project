package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;
    Button backToMainButton;
    Button createUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        // Initialize buttons
        backToMainButton = findViewById(R.id.btn_back_to_main);
        createUserButton = findViewById(R.id.btn_create_user);

        // Fetch the admin group and then fetch users
        fetchAdminGroup();

        // Handle navigation
        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
            startActivity(intent);
        });

        createUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, CreateUserActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAdminGroup() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String adminGroup = task.getResult().getString("adminGroup");
                            if (adminGroup != null) {
                                fetchUsers(adminGroup);
                            } else {
                                Toast.makeText(this, "Admin group not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error fetching admin group.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void fetchUsers(String adminGroup) {
        db.collection("users")
                .whereEqualTo("adminGroup", adminGroup)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = new User();
                            user.setId(document.getId());
                            user.setEmail(document.getString("email"));
                            user.setRole(document.getString("role"));
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error fetching users: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
