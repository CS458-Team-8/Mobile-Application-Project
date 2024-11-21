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
    Button logoutButton, addExpenseButton, viewHistoryButton, viewSummaryButton, launchCalculatorButton;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth and buttons
        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout);
        addExpenseButton = findViewById(R.id.btn_add_expense);
        viewHistoryButton = findViewById(R.id.btn_view_history);
        viewSummaryButton = findViewById(R.id.btn_view_summary);
        launchCalculatorButton = findViewById(R.id.btn_launch_calculator);
        textView = findViewById(R.id.user_details);

        // Get the current user
        user = auth.getCurrentUser();

        // Redirect to Login if not authenticated
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Display logged-in user's email
            textView.setText("Welcome, " + user.getEmail());
        }

        // Logout functionality
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Navigate to Add Expense screen
        addExpenseButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        // Navigate to Expense History screen
        viewHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpenseHistoryActivity.class);
            startActivity(intent);
        });

        // Navigate to Expense Summary screen
        viewSummaryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpenseSummaryActivity.class);
            startActivity(intent);
        });

        // Navigate to Calculator screen
        launchCalculatorButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Calculator.class);
            startActivity(intent);
        });
    }
}

