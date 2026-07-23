package com.revature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class exceptionTests {
    UserValidation userValidation = new UserValidation();
    @Test
    @DisplayName("Email Validation") 
    void emailValidation() {
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {userValidation.validateEmail(null);});
        assertEquals(exception1.getMessage(), "Email cannot be null");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {userValidation.validateEmail("");});
        assertEquals(exception2.getMessage(), "Email cannot be empty");

        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () -> {userValidation.validateEmail("noAtSign");});
        assertEquals(exception3.getMessage(), "Email must contain @");
    }
}
