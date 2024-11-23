package com.example.firebaselogin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ExpenseAdapter extends FirestoreRecyclerAdapter<DocumentSnapshot, ExpenseAdapter.ExpenseViewHolder> {

    private final OnExpenseInteractionListener listener;

    public interface OnExpenseInteractionListener {
        void onEdit(DocumentSnapshot expense);
        void onDelete(DocumentSnapshot expense);
    }

    public ExpenseAdapter(@NonNull FirestoreRecyclerOptions<DocumentSnapshot> options, OnExpenseInteractionListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull DocumentSnapshot model) {
        holder.bind(model, listener);
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, dateText, categoryText, descriptionText;
        ImageButton editButton, deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amount_text);
            dateText = itemView.findViewById(R.id.date_text);
            categoryText = itemView.findViewById(R.id.category_text);
            descriptionText = itemView.findViewById(R.id.description_text);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(DocumentSnapshot expense, OnExpenseInteractionListener listener) {
            amountText.setText(String.valueOf(expense.get("amount")));
            dateText.setText(String.valueOf(expense.get("date")));
            categoryText.setText(String.valueOf(expense.get("category")));
            descriptionText.setText(String.valueOf(expense.get("description")));

            editButton.setOnClickListener(view -> listener.onEdit(expense));
            deleteButton.setOnClickListener(view -> listener.onDelete(expense));
        }
    }
}
