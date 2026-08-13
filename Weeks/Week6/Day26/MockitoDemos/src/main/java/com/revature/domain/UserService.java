package com.revature.domain;

import java.util.List;
import java.util.Optional;

/**
 * UserService - The System Under Test (SUT) for Mockito demos.
 * 
 * This service depends on:
 * - UserRepository (for data access)
 * - EmailClient (for notifications)
 * 
 * This demonstrates the typical service layer pattern where
 * we inject dependencies through the constructor.
 */
public class UserService {
    
    private final UserRepository repository;
    private final EmailClient emailClient;
    
    /**
     * Constructor injection - makes the class testable!
     */
    public UserService(UserRepository repository, EmailClient emailClient) {
        this.repository = repository;
        this.emailClient = emailClient;
    }
    
    /**
     * Overloaded constructor when email is not needed.
     */
    public UserService(UserRepository repository) {
        this(repository, null);
    }
    
    /**
     * Get a user by ID.
     */
    public User getUser(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }
    
    /**
     * Find user by ID, returning Optional.
     */
    public Optional<User> findUser(Long id) {
        return repository.findById(id);
    }
    
    /**
     * Get all active users.
     */
    public List<User> getActiveUsers() {
        return repository.findAllActive();
    }
    
    /**
     * Create a new user with email notification.
     */
    public User createUser(String name, String email) {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        
        // Check for duplicate
        if (repository.existsByEmail(email)) {
            throw new DuplicateUserException("Email already registered: " + email);
        }
        
        // Create and save user
        User user = new User(name, email);
        User savedUser = repository.save(user);
        
        // Send welcome email
        if (emailClient != null) {
            emailClient.send(email, "Welcome!", "Welcome to our platform, " + name + "!");
        }
        
        return savedUser;
    }
    
    /**
     * Update user's email with notification.
     */
    public User updateEmail(Long userId, String newEmail) {
        User user = getUser(userId);
        String oldEmail = user.getEmail();
        
        user.setEmail(newEmail);
        User savedUser = repository.save(user);
        
        // Notify both old and new email
        if (emailClient != null) {
            emailClient.send(oldEmail, "Email Changed", 
                "Your email has been changed to " + newEmail);
            emailClient.send(newEmail, "Email Confirmed", 
                "This email is now associated with your account");
        }
        
        return savedUser;
    }
    
    /**
     * Deactivate a user.
     */
    public void deactivateUser(Long userId) {
        User user = getUser(userId);
        user.setActive(false);
        repository.save(user);
        
        if (emailClient != null) {
            emailClient.send(user.getEmail(), "Account Deactivated", 
                "Your account has been deactivated");
        }
    }
    
    /**
     * Delete a user.
     */
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        repository.deleteById(userId);
        
        if (emailClient != null) {
            emailClient.send(user.getEmail(), "Account Deleted", 
                "Your account has been permanently deleted");
        }
    }
    
    /**
     * Get user count.
     */
    public long getUserCount() {
        return repository.count();
    }
    
    // Custom exceptions
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class DuplicateUserException extends RuntimeException {
        public DuplicateUserException(String message) {
            super(message);
        }
    }
}

