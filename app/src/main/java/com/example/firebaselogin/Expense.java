package com.example.firebaselogin;

public class Expense {
    private String id;
    private String amount;
    private String description;
    private String date;
    private String category;

    // No-argument constructor is required for Firestore
    public Expense() {}

    // Constructor for convenience
    public Expense(String userId, String amount, String description, String date, String category) {
        this.id = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    // Getters and setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

