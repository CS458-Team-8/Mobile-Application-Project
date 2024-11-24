package com.example.firebaselogin;

public class User {
    private String id;    // Optional: Firestore document ID
    private String email; // User email
    private String role;  // User role

    // No-argument constructor required by Firestore
    public User() {
    }

    // Constructor for initializing email and role
    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    // Constructor for initializing id, email, and role
    public User(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // Getter and setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
