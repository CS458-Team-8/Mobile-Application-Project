package com.example.firebaselogin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText amountEditText, descriptionEditText;
    private TextView dateTextView;
    private Spinner categorySpinner;
    private Button saveButton, datePickerButton;
    private FirebaseFirestore db;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize fields
        amountEditText = findViewById(R.id.amount);
        descriptionEditText = findViewById(R.id.description);
        dateTextView = findViewById(R.id.date_text_view);
        categorySpinner = findViewById(R.id.category_spinner);
        saveButton = findViewById(R.id.save_button);
        datePickerButton = findViewById(R.id.date_picker_button);

        db = FirebaseFirestore.getInstance();

        // Set up DatePicker
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Save expense
        saveButton.setOnClickListener(view -> saveExpense());
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Update dateTextView
                    String formattedDate = String.format("%02d/%02d/%04d",
                            selectedMonth + 1, // Month is 0-indexed
                            selectedDay,
                            selectedYear);
                    dateTextView.setText(formattedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveExpense() {
        String amount = amountEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String date = dateTextView.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please fill in all mandatory fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the user's adminGroup
        db.collection("users").document(userId).get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String group = userDoc.getString("adminGroup");

                        // Prepare the expense data
                        Map<String, Object> expense = new HashMap<>();
                        expense.put("amount", amount);
                        expense.put("description", description);
                        expense.put("date", date);
                        expense.put("category", category);
                        expense.put("group", group); // Associate expense with the group
                        expense.put("user", userId);

                        // Save the expense to Firestore
                        db.collection("expenses")
                                .add(expense)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                                    finish(); // Close activity after saving
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error adding expense", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Unable to fetch user group", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user information", Toast.LENGTH_SHORT).show();
                });
    }
}
