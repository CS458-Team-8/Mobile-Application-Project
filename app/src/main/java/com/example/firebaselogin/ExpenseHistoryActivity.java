package com.example.firebaselogin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

        FirestoreRecyclerOptions<DocumentSnapshot> options = new FirestoreRecyclerOptions.Builder<DocumentSnapshot>()
                .setQuery(query, DocumentSnapshot.class)
                .build();

        adapter = new ExpenseAdapter(options, new ExpenseAdapter.OnExpenseInteractionListener() {
            @Override
            public void onEdit(DocumentSnapshot expense) {
                // Handle edit action
            }

            @Override
            public void onDelete(DocumentSnapshot expense) {
                expense.getReference().delete();
            }
        });

        recyclerView.setAdapter(adapter);
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

