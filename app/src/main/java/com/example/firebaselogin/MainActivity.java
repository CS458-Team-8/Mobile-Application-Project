package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;

    TextView userDetailsTextView, roleTextView;
    Button logoutButton, addExpenseButton, viewHistoryButton, viewSummaryButton, launchCalculatorButton, adminButton, viewerButton, adminDashboardButton, budgetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Bind UI elements
        userDetailsTextView = findViewById(R.id.user_details);
        roleTextView = findViewById(R.id.role);
        logoutButton = findViewById(R.id.logout);
        addExpenseButton = findViewById(R.id.btn_add_expense);
        viewHistoryButton = findViewById(R.id.btn_view_history);
        viewSummaryButton = findViewById(R.id.btn_view_summary);
        launchCalculatorButton = findViewById(R.id.btn_launch_calculator);
        adminButton = findViewById(R.id.admin_button);
        viewerButton = findViewById(R.id.viewer_button);
        adminDashboardButton = findViewById(R.id.btn_admin_dashboard);
        budgetButton = findViewById(R.id.btn_budget_settings); // New button for Budget Settings

        // Ensure the user is logged in
        if (user == null) {
            // Redirect to login if the user is not authenticated
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Display the user's email
            userDetailsTextView.setText("Welcome, " + user.getEmail());

            // Fetch the user's role and adjust UI accordingly
            fetchAndAdjustUIBasedOnRole(user.getUid());
        }

        // Handle logout button click
        logoutButton.setOnClickListener(view -> {
            auth.signOut(); // Sign out the current user
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish(); // Close the current activity
        });

        // Handle Add Expense button click
        addExpenseButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        // Handle View History button click
        viewHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpenseHistoryActivity.class);
            startActivity(intent);
        });

        // Handle View Summary button click
        viewSummaryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpenseSummaryActivity.class);
            startActivity(intent);
        });

        // Handle Calculator button click
        launchCalculatorButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Calculator.class);
            startActivity(intent);
        });

        // Handle Admin Dashboard button click
        adminDashboardButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AdminDashboard.class);
            startActivity(intent);
        });

        // Handle Budget Settings button click
        budgetButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Fetches the user's role from Firestore and adjusts the UI based on their role.
     *
     * @param userId The user's Firebase UID.
     */
    private void fetchAndAdjustUIBasedOnRole(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the user's role from Firestore
                            String role = document.getString("role");
                            roleTextView.setText("Role: " + role);

                            // Adjust UI based on the user's role
                            adjustUIBasedOnRole(role);
                        } else {
                            Toast.makeText(MainActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error fetching role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Adjusts the UI elements based on the user's role.
     *
     * @param role The role of the user (e.g., "admin", "viewer").
     */
    private void adjustUIBasedOnRole(String role) {
        if ("admin".equals(role)) {
            adminButton.setVisibility(View.VISIBLE);
            viewerButton.setVisibility(View.GONE);
            adminDashboardButton.setVisibility(View.VISIBLE); // Show the Admin Dashboard button for admins
            budgetButton.setVisibility(View.VISIBLE); // Allow budget setting for admins
            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
        } else if ("viewer".equals(role)) {
            adminButton.setVisibility(View.GONE);
            viewerButton.setVisibility(View.VISIBLE);
            adminDashboardButton.setVisibility(View.GONE); // Hide the Admin Dashboard button for viewers
            budgetButton.setVisibility(View.GONE); // Hide budget setting for viewers
            Toast.makeText(this, "Welcome, Viewer!", Toast.LENGTH_SHORT).show();
        } else {
            // Default case for unknown roles
            adminButton.setVisibility(View.GONE);
            viewerButton.setVisibility(View.GONE);
            adminDashboardButton.setVisibility(View.GONE);
            budgetButton.setVisibility(View.GONE);
            Toast.makeText(this, "Welcome, User!", Toast.LENGTH_SHORT).show();
        }
    }
}

