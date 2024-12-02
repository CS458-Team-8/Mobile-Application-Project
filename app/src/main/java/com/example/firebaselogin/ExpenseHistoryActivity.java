package com.example.firebaselogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ExpenseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        recyclerView = findViewById(R.id.expense_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        loadExpenses();
    }

    private void loadExpenses() {
        Query query = db.collection("expenses").orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Expense> options = new FirestoreRecyclerOptions.Builder<Expense>()
                .setQuery(query, Expense.class)
                .build();

        adapter = new ExpenseAdapter(options, new ExpenseAdapter.OnExpenseInteractionListener() {
            @Override
            public void onEdit(Expense expense) {
                showEditDialog(expense);
            }

            @Override
            public void onDelete(Expense expense) {
                db.collection("expenses").document(expense.getId()).delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(ExpenseHistoryActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ExpenseHistoryActivity.this, "Failed to delete expense: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void showEditDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_expense, null);
        builder.setView(dialogView);

        // Bind dialog views
        EditText amountEditText = dialogView.findViewById(R.id.edit_amount);
        EditText descriptionEditText = dialogView.findViewById(R.id.edit_description);
        EditText dateEditText = dialogView.findViewById(R.id.edit_date);
        Spinner categorySpinner = dialogView.findViewById(R.id.edit_category_spinner);

        // Set current values
        amountEditText.setText(expense.getAmount());
        descriptionEditText.setText(expense.getDescription());
        dateEditText.setText(expense.getDate());

        // Populate the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Preselect the current category
        if (expense.getCategory() != null) {
            int spinnerPosition = adapter.getPosition(expense.getCategory());
            categorySpinner.setSelection(spinnerPosition);
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get updated values
            String updatedAmount = amountEditText.getText().toString().trim();
            String updatedDescription = descriptionEditText.getText().toString().trim();
            String updatedDate = dateEditText.getText().toString().trim();
            String updatedCategory = categorySpinner.getSelectedItem().toString();

            // Validate input
            if (updatedAmount.isEmpty() || updatedDate.isEmpty()) {
                Toast.makeText(this, "Amount and Date are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the expense in Firestore
            updateExpense(expense.getId(), updatedAmount, updatedDescription, updatedDate, updatedCategory);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    private void updateExpense(String expenseId, String amount, String description, String date, String category) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("amount", amount);
        updates.put("description", description);
        updates.put("date", date);
        updates.put("category", category);

        db.collection("expenses").document(expenseId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update expense: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

