package com.example.group_project_team8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set Firebase to use the device's language
        auth.useAppLanguage();

        // Check if a user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, fetch their expenses
            fetchExpenses(currentUser.getUid());
        } else {
            // No user is signed in, redirect to SignUpActivity or LoginActivity
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish(); // Close MainActivity if no user is logged in
        }
    }

    private void fetchExpenses(String userId) {
        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String description = document.getString("description");
                        double amount = document.getDouble("amount");
                        String category = document.getString("category");
                        String date = document.getString("date");
                        Expense expense = new Expense(description, amount, category, date);
                        expenseList.add(expense);
                    }
                    if (expenseList.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No expenses found", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter = new ExpenseAdapter(expenseList);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to fetch expenses.", Toast.LENGTH_SHORT).show());
    }
}
