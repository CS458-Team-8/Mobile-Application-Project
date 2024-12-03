package com.example.firebaselogin;

/**
 * Represents a user in the system with attributes such as ID, email, role, and group association.
 * This class is used to store and retrieve user data from Firestore.
 */
public class User {

    /**
     * The unique identifier for the user, typically the Firestore document ID.
     */
    private String id;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The role of the user (e.g., "admin", "viewer", "editor") that defines their permissions.
     */
    private String role;

    /**
     * The identifier of the group this user belongs to, linking them to an admin or a team.
     */
    private String adminGroup;

    /**
     * Default no-argument constructor required by Firestore for deserialization.
     */
    public User() {}

    /**
     * Constructs a User object with the specified ID, email, role, and group association.
     *
     * @param id         The unique identifier for the user.
     * @param email      The email address of the user.
     * @param role       The role of the user (e.g., "admin", "viewer").
     * @param adminGroup The group ID that the user belongs to.
     */
    public User(String id, String email, String role, String adminGroup) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.adminGroup = adminGroup;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id The user's ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The user's email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the role of the user.
     *
     * @return The user's role (e.g., "admin", "viewer").
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role The user's role (e.g., "admin", "viewer").
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the group ID that the user belongs to.
     *
     * @return The user's group ID.
     */
    public String getAdminGroup() {
        return adminGroup;
    }

    /**
     * Sets the group ID that the user belongs to.
     *
     * @param adminGroup The user's group ID.
     */
    public void setAdminGroup(String adminGroup) {
        this.adminGroup = adminGroup;
    }
}
