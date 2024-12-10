package com.example.firebaselogin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ExpenseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private FirebaseFirestore db;
    private ExpenseExporter expenseExporter;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        // Initialize Firebase and exporter
        db = FirebaseFirestore.getInstance();
        expenseExporter = new ExpenseExporter(this);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.expense_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize search input
        searchInput = findViewById(R.id.search_input);

        // Load expenses initially
        loadExpenses(null);

        // Listen for search input changes
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                loadExpenses(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bind export buttons
        Button exportCsvButton = findViewById(R.id.export_csv_button);
        Button exportPdfButton = findViewById(R.id.export_pdf_button);

        // Handle Export to CSV
        exportCsvButton.setOnClickListener(v -> getCurrentUserGroup(groupId -> {
            if (groupId != null) {
                expenseExporter.exportToCSV(groupId);
                Toast.makeText(this, "Exported to CSV", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Group ID not found!", Toast.LENGTH_SHORT).show();
            }
        }));

        // Handle Export to PDF
        exportPdfButton.setOnClickListener(v -> getCurrentUserGroup(groupId -> {
            if (groupId != null) {
                expenseExporter.exportToPDF(groupId);
                Toast.makeText(this, "Exported to PDF", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Group ID not found!", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadExpenses(String keyword) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query expenses for the user's group
        Query query = db.collection("expenses")
                .whereEqualTo("userId", userId)
                .orderBy("description");

        // Apply search filter if keyword is provided
        if (keyword != null && !keyword.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("description", keyword)
                    .whereLessThanOrEqualTo("description", keyword + "\uf8ff");
        }

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
                db.collection("expenses").document(expense.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(ExpenseHistoryActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ExpenseHistoryActivity.this, "Failed to delete expense", Toast.LENGTH_SHORT).show());
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.startListening();
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

        // Populate and preselect the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        if (expense.getCategory() != null) {
            int spinnerPosition = adapter.getPosition(expense.getCategory());
            categorySpinner.setSelection(spinnerPosition);
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedAmount = amountEditText.getText().toString().trim();
            updateExpense(expense.getId(),
                    updatedAmount,
                    descriptionEditText.getText().toString().trim(),
                    dateEditText.getText().toString().trim(),
                    categorySpinner.getSelectedItem().toString());
            checkBudgetLimit(expense.getCategory(), Double.parseDouble(updatedAmount));
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateExpense(String expenseId, String amount, String description, String date, String category) {
        // Update logic as it is in the original file
    }

    private void checkBudgetLimit(String category, double amount) {
        // Budget checking logic as it is in the original file
    }

    private void getCurrentUserGroup(GroupIdCallback callback) {
        // Current user group fetching logic as it is in the original file
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public interface GroupIdCallback {
        void onGroupIdFetched(String groupId);
    }
}
