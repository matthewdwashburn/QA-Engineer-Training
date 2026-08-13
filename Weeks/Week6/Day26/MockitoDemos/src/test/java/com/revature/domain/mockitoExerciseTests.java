package com.revature.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.domain.UserService.DuplicateUserException;
import com.revature.domain.UserService.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
public class mockitoExerciseTests {

    @Mock
    private UserRepository repository; // Mock dependency, interface, default implementation by mockito

    @Mock
    private EmailClient emailClient; // Mock dependency, interface, default implementation by mockito

    @InjectMocks
    private UserService userService; // Inject mocks automatically


    @Test
    void getUser_existingUser_returnsUser() {
        // Configure the mock
        User expectedUser = new User("John", "johntest@gmail.com");
        expectedUser.setId(45L);
        
        // Stubbing
        when(repository.findById(45L)).thenReturn(Optional.of(expectedUser));

        // Act: Call the method under the test
        User actualUser = userService.getUser(45L);

        // Assert: Verify the result
        assertEquals(expectedUser, actualUser);
        assertEquals("John", actualUser.getName());
    }

    @Test
    void getUser_nonExistentUser_throwsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(999L);
        });
    }

    @Test
    void successful_userCreation() {
        assertFalse(repository.existsByEmail("bruh@gmail.com"));
        User user = new User(45L, "john", "bruh@gmail.com");
        when(repository.save(user)).thenReturn(user); // Create test implementation without calling real db
        User savedUser = repository.save(user);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(user.getId(), savedUser.getId());
    }

    @Test
    void duplicateEmailRejection() {
        when(repository.existsByEmail("joe@gmail.com")).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> {userService.createUser("joe", "joe@gmail.com");});
    }

    @Test
    void invalidInputRejection() {
        assertThrows(IllegalArgumentException.class, () -> {userService.createUser(null, "bruh@gmail.com");});
        assertThrows(IllegalArgumentException.class, () -> {userService.createUser("joe", "bruhgmail.com");});
    }

    @Test
    void getActiveUsersValidation() {
        userService.getActiveUsers();
        verify(repository).findAllActive();
    }

    @Test
    void getUserCountValidation() {
        userService.getUserCount();
    }

}
