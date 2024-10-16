package com.example.group_project_team8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.descriptionTextView.setText(expense.getDescription());
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        holder.categoryTextView.setText(expense.getCategory());
        holder.dateTextView.setText(expense.getDate());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView, amountTextView, categoryTextView, dateTextView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.expenseDescriptionTextView);
            amountTextView = itemView.findViewById(R.id.expenseAmountTextView);
            categoryTextView = itemView.findViewById(R.id.expenseCategoryTextView);
            dateTextView = itemView.findViewById(R.id.expenseDateTextView);
        }
    }
}
