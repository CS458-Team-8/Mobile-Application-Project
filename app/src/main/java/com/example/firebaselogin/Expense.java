package com.example.firebaselogin;

/**
 * Represents an expense associated with a specific group.
 */
public class Expense {
    private String id;         // Unique ID of the expense
    private String amount;     // Amount of the expense
    private String description;// Description of the expense
    private String date;       // Date of the expense
    private String category;   // Category of the expense
    private String group;      // Group ID associated with the expense

    // No-argument constructor required for Firestore
    public Expense() {}

    // Constructor with all fields
    public Expense(String id, String amount, String description, String date, String category, String group) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
        this.group = group;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}
