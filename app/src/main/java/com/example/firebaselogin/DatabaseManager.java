package com.example.firebaselogin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final DatabaseHelper dbHelper;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Insert an Expense
    public void insertExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, expense.getId());
        values.put(DatabaseHelper.COLUMN_AMOUNT, expense.getAmount());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, expense.getDescription());
        values.put(DatabaseHelper.COLUMN_DATE, expense.getDate());
        values.put(DatabaseHelper.COLUMN_CATEGORY, expense.getCategory());
        values.put(DatabaseHelper.COLUMN_GROUP, expense.getGroup());
        db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        db.close();
    }

    // Fetch Expenses by Group
    public List<Expense> getExpensesByGroup(String groupId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_GROUP + " = ?";
        String[] selectionArgs = {groupId};
        Cursor cursor = db.query(DatabaseHelper.TABLE_EXPENSES, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GROUP))
                );
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    // Delete an Expense
    public void deleteExpense(String expenseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {expenseId};
        db.delete(DatabaseHelper.TABLE_EXPENSES, whereClause, whereArgs);
        db.close();
    }
}
