package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;
    Button backToMainButton; // Button to navigate back to MainActivity
    Button createUserButton; // Button to navigate to CreateUserActivity

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

        // Fetch the list of users
        fetchUsers();

        // Handle navigation back to MainActivity
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Handle navigation to CreateUserActivity
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboard.this, CreateUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchUsers() {
        db.collection("users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Create a new User object
                            User user = new User();

                            // Set the fields manually
                            user.setId(document.getId());
                            user.setEmail(document.getString("email"));
                            user.setRole(document.getString("role"));

                            // Add the user to the list
                            userList.add(user);
                        }
                        // Notify the adapter of the changes
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error fetching users: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
