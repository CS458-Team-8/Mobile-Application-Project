package com.example.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private Spinner categorySpinner, timeframeSpinner;
    private EditText budgetAmountEditText;
    private Button saveButton, resetButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind UI components
        categorySpinner = findViewById(R.id.category_spinner);
        timeframeSpinner = findViewById(R.id.timeframe_spinner);
        budgetAmountEditText = findViewById(R.id.budget_amount);
        saveButton = findViewById(R.id.save_budget_button);
        resetButton = findViewById(R.id.reset_budget_button);

        // Load existing budget when a category is selected
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadExistingBudget();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        saveButton.setOnClickListener(v -> saveBudget());
        resetButton.setOnClickListener(v -> resetBudget());
    }

    private void saveBudget() {
        String category = categorySpinner.getSelectedItem().toString();
        String timeframe = timeframeSpinner.getSelectedItem().toString();
        String budgetAmount = budgetAmountEditText.getText().toString();

        if (budgetAmount.isEmpty()) {
            Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String groupId = document.getString("adminGroup");
                        Map<String, Object> budget = new HashMap<>();
                        budget.put("category", category);
                        budget.put("timeframe", timeframe);
                        budget.put("amount", Double.parseDouble(budgetAmount));
                        budget.put("group", groupId);

                        db.collection("budgets")
                                .document(groupId + "_" + category)
                                .set(budget)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Budget saved successfully", Toast.LENGTH_SHORT).show();

                                    // Redirect to MainActivity
                                    Intent intent = new Intent(BudgetActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish(); // Close BudgetActivity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error saving budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void resetBudget() {
        String category = categorySpinner.getSelectedItem().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String groupId = document.getString("adminGroup");
                        db.collection("budgets")
                                .document(groupId + "_" + category)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Budget reset successfully", Toast.LENGTH_SHORT).show();
                                    budgetAmountEditText.setText(""); // Clear the amount field
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error resetting budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void loadExistingBudget() {
        String category = categorySpinner.getSelectedItem().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String groupId = document.getString("adminGroup");
                        db.collection("budgets")
                                .document(groupId + "_" + category)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (snapshot.exists()) {
                                        // Populate fields with existing budget data
                                        budgetAmountEditText.setText(String.valueOf(snapshot.getDouble("amount")));
                                        String timeframe = snapshot.getString("timeframe");
                                        if (timeframe != null) {
                                            int spinnerPosition = ((ArrayAdapter<String>) timeframeSpinner.getAdapter())
                                                    .getPosition(timeframe);
                                            timeframeSpinner.setSelection(spinnerPosition);
                                        }
                                    } else {
                                        // Clear fields if no budget exists for the selected category
                                        budgetAmountEditText.setText("");
                                        timeframeSpinner.setSelection(0);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error loading budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }
}
