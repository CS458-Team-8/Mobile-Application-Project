package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logoutButton, addExpenseButton, viewHistoryButton, viewSummaryButton;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout);
        addExpenseButton = findViewById(R.id.btn_add_expense);
        viewHistoryButton = findViewById(R.id.btn_view_summary);
        viewSummaryButton = findViewById(R.id.btn_view_summary);
        textView = findViewById(R.id.user_details);

        // Get the current user
        user = auth.getCurrentUser();

        // If the user is not logged in, redirect to the login activity
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Show the logged-in user's email
            textView.setText(user.getEmail());
        }

        // Logout button logic
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Navigate to Add Expense
        addExpenseButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        // Navigate to Expense History
        viewHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpenseHistoryActivity.class);
            startActivity(intent);
        });

        // Navigate to Expense Summary
        viewSummaryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpenseSummaryActivity.class);
            startActivity(intent);
        });
    }
}

