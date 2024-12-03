package com.example.firebaselogin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays a summary of expenses by category for the current user's group.
 */
public class ExpenseSummaryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private PieChart pieChart;
    private TextView totalSpendingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_summary);

        // Initialize views
        pieChart = findViewById(R.id.pie_chart);
        totalSpendingText = findViewById(R.id.total_spending_text);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load the expense summary for the current user's group
        loadExpenseSummary();
    }

    private void loadExpenseSummary() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the adminGroup for the current user
        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String group = userDoc.getString("adminGroup");

                // Query expenses for the user's group
                db.collection("expenses")
                        .whereEqualTo("group", group)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Map<String, Float> categoryTotals = new HashMap<>();
                                float totalSpending = 0;

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String category = document.getString("category");
                                    String amountString = document.getString("amount");
                                    float amount = 0;

                                    // Ensure valid number parsing
                                    try {
                                        amount = Float.parseFloat(amountString);
                                    } catch (NumberFormatException e) {
                                        amount = 0;
                                    }

                                    totalSpending += amount;
                                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
                                }

                                displaySummary(totalSpending, categoryTotals);
                            }
                        });
            }
        });
    }


    private void displaySummary(float totalSpending, Map<String, Float> categoryTotals) {
        // Set total spending text
        totalSpendingText.setText(String.format("Total Spending: $%.2f", totalSpending));

        // Create PieChart entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        // Configure PieChart data
        PieDataSet dataSet = new PieDataSet(entries, "Spending by Category");

        // Apply custom colors
        dataSet.setColors(new int[]{
                Color.rgb(255, 105, 180), // Neon Pink
                Color.rgb(50, 205, 50),   // Neon Green
                Color.rgb(30, 144, 255),  // Neon Blue
                Color.rgb(255, 255, 0),   // Neon Yellow
                Color.rgb(255, 69, 0)     // Neon Orange
        });

        dataSet.setValueTextColor(Color.BLACK); // Black text for contrast
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);

        // Configure PieChart
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK); // Black labels for contrast
        pieChart.setHoleColor(Color.WHITE);       // White hole for better visibility
        pieChart.setDescription(null);           // Remove description text
        pieChart.invalidate();                   // Refresh the chart
    }
}
