package com.example.group_project_team8;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText descriptionEditText, amountEditText, dateEditText, categoryEditText;
    private Button addExpenseButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense); // Ensure the XML layout file exists

        // Initialize UI components
        descriptionEditText = findViewById(R.id.descriptionEditText);
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        // Initialize Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Handle button click to add expense
        addExpenseButton.setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString();
            double amount = Double.parseDouble(amountEditText.getText().toString());
            String date = dateEditText.getText().toString();
            String category = categoryEditText.getText().toString();
            String userId = auth.getCurrentUser().getUid(); // Get the logged-in user's ID

            // Create a map for the expense data
            Map<String, Object> expense = new HashMap<>();
            expense.put("description", description);
            expense.put("amount", amount);
            expense.put("category", category);
            expense.put("date", date);
            expense.put("userId", userId);

            // Add the expense to Firestore
            db.collection("expenses")
                    .add(expense)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddExpenseActivity.this, "Expense added!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful addition
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddExpenseActivity.this, "Error adding expense.", Toast.LENGTH_SHORT).show());
        });
    }
}
