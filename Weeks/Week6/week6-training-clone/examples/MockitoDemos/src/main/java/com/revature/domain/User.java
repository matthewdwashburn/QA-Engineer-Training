package com.revature.domain;

import java.time.LocalDateTime;

/**
 * User domain object for Mockito demos.
 */
public class User {
    private Long id;
    private String name;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;

    public User() {}

    public User(String email) {
        this.email = email;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(Long id, String name, String email) {
        this(name, email);
        this.id = id;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

