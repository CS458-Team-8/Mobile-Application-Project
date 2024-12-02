package com.example.firebaselogin;

public class UserManager {
    private static UserManager instance;
    private String role;
    private String email;

    // Private constructor for Singleton
    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void clear() {
        role = null;
        email = null;
    }
}
