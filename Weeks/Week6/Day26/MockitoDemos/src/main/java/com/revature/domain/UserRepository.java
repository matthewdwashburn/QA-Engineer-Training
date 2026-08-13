package com.revature.domain;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository interface - This will be mocked in our tests.
 * 
 * INSTRUCTOR NOTE: 
 * This represents a typical data access layer interface.
 * We mock this to avoid real database calls during unit tests.
 */
public interface UserRepository {
    
    /**
     * Find a user by their unique ID.
     */
    Optional<User> findById(Long id);
    
    /**
     * Find a user by their email address.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users in the system.
     */
    List<User> findAll();
    
    /**
     * Find all active users.
     */
    List<User> findAllActive();
    
    /**
     * Save a user (create or update).
     */
    User save(User user);
    
    /**
     * Delete a user by ID.
     */
    void deleteById(Long id);
    
    /**
     * Check if a user exists with the given email.
     */
    boolean existsByEmail(String email);
    
    /**
     * Count total users.
     */
    long count();
}

