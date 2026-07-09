package com.revature.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demo: Real-World Mocking - Testing a Service Layer with Full Isolation
 *
 * 1. This demonstrates a complete, realistic test class
 * 2. Shows the AAA pattern: Arrange, Act, Assert
 * 3. Tests multiple scenarios: success, failure, edge cases
 * 4. Combines stubbing and verification techniques
 * 5. Shows how mocking enables testing complex business logic
  */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Real World Mocking Demo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Demo_Real_World_Mocking {

    @Mock
    private UserRepository repository;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<String> emailCaptor;

    // ==========================================================
    // SECTION 1: Happy Path Tests
    // ==========================================================

    @Nested
    @DisplayName("User Creation - Success Scenarios")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UserCreationSuccess {

        @Test
        @Order(1)
        @DisplayName("Create user with valid data succeeds")
        void createUser_validData_savesAndNotifies() {
            // ARRANGE
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            // ACT
            User created = userService.createUser("John Doe", "john@example.com");

            // ASSERT - verify result
            assertNotNull(created);
            assertEquals(1L, created.getId());
            assertEquals("John Doe", created.getName());
            assertEquals("john@example.com", created.getEmail());
            assertTrue(created.isActive());

            // ASSERT - verify interactions
            verify(repository).existsByEmail("john@example.com");
            verify(repository).save(userCaptor.capture());
            verify(emailClient).send(eq("john@example.com"), eq("Welcome!"), anyString());

            // ASSERT - verify saved user details
            User savedUser = userCaptor.getValue();
            assertEquals("John Doe", savedUser.getName());
            assertNotNull(savedUser.getCreatedAt());
        }

        @Test
        @Order(2)
        @DisplayName("Email is sent with welcome message")
        void createUser_validData_sendsWelcomeEmail() {
            // ARRANGE
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenReturn(new User(1L, "Jane", "jane@test.com"));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            // ACT
            userService.createUser("Jane", "jane@test.com");

            // ASSERT - capture and verify email content
            ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
            verify(emailClient).send(eq("jane@test.com"), eq("Welcome!"), bodyCaptor.capture());

            String emailBody = bodyCaptor.getValue();
            assertTrue(emailBody.contains("Jane"), "Email should contain user's name");
            assertTrue(emailBody.contains("Welcome"), "Email should be a welcome message");
        }
    }

    // ==========================================================
    // SECTION 2: Error Handling Tests
    // ==========================================================

    @Nested
    @DisplayName("User Creation - Error Scenarios")
    class UserCreationErrors {

        @Test
        @DisplayName("Null name throws IllegalArgumentException")
        void createUser_nullName_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser(null, "test@test.com")
            );

            assertEquals("Name is required", ex.getMessage());

            // Verify no interactions with mocks
            verifyNoInteractions(repository);
            verifyNoInteractions(emailClient);
        }

        @Test
        @DisplayName("Empty name throws IllegalArgumentException")
        void createUser_emptyName_throwsException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser("   ", "test@test.com")
            );

            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Invalid email throws IllegalArgumentException")
        void createUser_invalidEmail_throwsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.createUser("John", "invalid-email")
            );

            assertTrue(ex.getMessage().contains("email"));
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Duplicate email throws DuplicateUserException")
        void createUser_duplicateEmail_throwsException() {
            // ARRANGE
            when(repository.existsByEmail("existing@test.com")).thenReturn(true);

            // ACT & ASSERT
            UserService.DuplicateUserException ex = assertThrows(
                    UserService.DuplicateUserException.class,
                    () -> userService.createUser("John", "existing@test.com")
            );

            assertTrue(ex.getMessage().contains("existing@test.com"));

            // Verify no save or email
            verify(repository, never()).save(any());
            verify(emailClient, never()).send(anyString(), anyString(), anyString());
        }
    }

    // ==========================================================
    // SECTION 3: Testing User Retrieval
    // ==========================================================

    @Nested
    @DisplayName("User Retrieval")
    class UserRetrieval {

        @Test
        @DisplayName("Get existing user returns user")
        void getUser_existingId_returnsUser() {
            User mockUser = new User(42L, "Found User", "found@test.com");
            when(repository.findById(42L)).thenReturn(Optional.of(mockUser));

            User result = userService.getUser(42L);

            assertEquals("Found User", result.getName());
            assertEquals("found@test.com", result.getEmail());
        }

        @Test
        @DisplayName("Get non-existing user throws UserNotFoundException")
        void getUser_nonExistingId_throwsException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            UserService.UserNotFoundException ex = assertThrows(
                    UserService.UserNotFoundException.class,
                    () -> userService.getUser(999L)
            );

            assertTrue(ex.getMessage().contains("999"));
        }
    }

    // ==========================================================
    // SECTION 4: Testing User Updates
    // ==========================================================

    @Nested
    @DisplayName("User Updates")
    class UserUpdates {

        @Test
        @DisplayName("Update email notifies both old and new addresses")
        void updateEmail_validUpdate_notifiesBothAddresses() {
            // ARRANGE
            User existingUser = new User(1L, "John", "old@test.com");
            when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            // ACT
            User updated = userService.updateEmail(1L, "new@test.com");

            // ASSERT
            assertEquals("new@test.com", updated.getEmail());

            // Verify both emails received notifications
            verify(emailClient, times(2)).send(emailCaptor.capture(), anyString(), anyString());
            List<String> notifiedEmails = emailCaptor.getAllValues();
            assertTrue(notifiedEmails.contains("old@test.com"));
            assertTrue(notifiedEmails.contains("new@test.com"));
        }

        @Test
        @DisplayName("Update email for non-existing user throws exception")
        void updateEmail_nonExistingUser_throwsException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(
                    UserService.UserNotFoundException.class,
                    () -> userService.updateEmail(999L, "new@test.com")
            );

            verify(repository, never()).save(any());
            verifyNoInteractions(emailClient);
        }
    }

    // ==========================================================
    // SECTION 5: Testing Deactivation and Deletion
    // ==========================================================

    @Nested
    @DisplayName("User Deactivation and Deletion")
    class UserDeactivationDeletion {

        @Test
        @DisplayName("Deactivate user sets active to false and notifies")
        void deactivateUser_existingUser_deactivatesAndNotifies() {
            // ARRANGE
            User activeUser = new User(1L, "John", "john@test.com");
            activeUser.setActive(true);
            when(repository.findById(1L)).thenReturn(Optional.of(activeUser));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            // ACT
            userService.deactivateUser(1L);

            // ASSERT
            verify(repository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertFalse(savedUser.isActive(), "User should be deactivated");

            verify(emailClient).send(
                    eq("john@test.com"),
                    eq("Account Deactivated"),
                    anyString()
            );
        }

        @Test
        @DisplayName("Delete user removes from repository and notifies")
        void deleteUser_existingUser_deletesAndNotifies() {
            // ARRANGE
            User user = new User(1L, "ToDelete", "delete@test.com");
            when(repository.findById(1L)).thenReturn(Optional.of(user));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            // ACT
            userService.deleteUser(1L);

            // ASSERT - verify correct order
            InOrder inOrder = inOrder(repository, emailClient);
            inOrder.verify(repository).findById(1L);
            inOrder.verify(repository).deleteById(1L);
            inOrder.verify(emailClient).send(
                    eq("delete@test.com"),
                    eq("Account Deleted"),
                    anyString()
            );
        }
    }

    // ==========================================================
    // SECTION 6: Edge Cases and Special Scenarios
    // ==========================================================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Service works without email client (graceful degradation)")
        void createUser_noEmailClient_stillCreatesUser() {
            // Create service without email client
            UserService serviceWithoutEmail = new UserService(repository);

            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });

            // Should work without email
            User created = serviceWithoutEmail.createUser("John", "john@test.com");
            assertNotNull(created);
            assertEquals("John", created.getName());

            // Email client not called (it's null)
            verifyNoInteractions(emailClient);
        }

        @Test
        @DisplayName("Email failure doesn't prevent user creation")
        void createUser_emailFails_userStillCreated() {
            // ARRANGE
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });
            when(emailClient.send(anyString(), anyString(), anyString()))
                    .thenReturn(false);  // Email fails

            // ACT
            User created = userService.createUser("John", "john@test.com");

            // ASSERT - user still created despite email failure
            assertNotNull(created);
            verify(repository).save(any());
        }
    }
}
