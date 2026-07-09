package com.revature.domain;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//Verification techniques - Confirming Mock Interactions

//verify() confirms methods were called
//verification modes: times(), never(), atLeast(), atMost()
//ArgumentCaptor: Capture and Inspect what was passed
//InOrder: verify call sequence
//verifyNoMoreInteractions: Strict verification


@ExtendWith(MockitoExtension.class)
@DisplayName("Verification Techniques Demo")
public class Demo_Verification_Techniques {

    @Mock
    private UserRepository repository;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private UserService userService;

    //verify()

    @Nested
    @DisplayName("Basic Verify")
    class BasicVerification {

        @Test
        @DisplayName("Verify method was called")
        void basicVerify() {
            //Setup
            when(repository.findById(1L))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));
            //Act
            userService.getUser(1L);

            //Verify the mock was colled
            verify(repository).findById(1L);

        }

        @Test
        @DisplayName("Verify Method was called with specific argument")
        void verifyWithArgument() {
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenReturn(new User(1L, "John", "john@test.com"));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.createUser("John", "john@test.com");

            //verify specific argument
            verify(repository).existsByEmail("john@test.com");
            verify(emailClient).send(eq("john@test.com"), anyString(), anyString());
        }

        @Test
        @DisplayName("Verify with argument matchers")
        void verifyWithMatchers() {
            when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));

            userService.getUser(42L);

            // Verify using matchers
            verify(repository).findById(anyLong());
            verify(repository).findById(argThat(id -> id > 0));
        }
    }

    // ==========================================================
    // SECTION 2: Verification Modes
    // ==========================================================

    @Nested
    @DisplayName("Verification Modes")
    class VerificationModes {

        @Test
        @DisplayName("times() - verify exact number of calls")
        void verifyTimes() {
            when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));

            // Call multiple times
            userService.getUser(1L);
            userService.getUser(2L);
            userService.getUser(3L);

            // Verify exact count
            verify(repository, times(3)).findById(anyLong());
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).findById(2L);
        }

        @Test
        @DisplayName("never() - verify method was NOT called")
        void verifyNever() {
            when(repository.existsByEmail(anyString())).thenReturn(true);

            // This should throw DuplicateUserException, not call save
            assertThrows(UserService.DuplicateUserException.class,
                    () -> userService.createUser("John", "existing@test.com"));

            // Verify save was NEVER called
            verify(repository, never()).save(any());
            verify(emailClient, never()).send(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("atLeast() and atMost()")
        void verifyAtLeastAtMost() {
            when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));

            userService.getUser(1L);
            userService.getUser(1L);
            userService.getUser(1L);

            verify(repository, atLeast(2)).findById(anyLong());
            verify(repository, atMost(5)).findById(anyLong());
            verify(repository, atLeastOnce()).findById(1L);
        }

        @Test
        @DisplayName("Combined verification example")
        void combinedVerification() {
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenReturn(new User(1L, "John", "john@test.com"));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.createUser("John", "john@test.com");

            // Multiple verifications
            verify(repository, times(1)).existsByEmail(anyString());
            verify(repository, times(1)).save(any());
            verify(emailClient, times(1)).send(anyString(), anyString(), anyString());
            verify(repository, never()).deleteById(anyLong());
        }
    }

    // ==========================================================
    // SECTION 3: ArgumentCaptor - Capture and Inspect Arguments
    // ==========================================================

    @Nested
    @DisplayName("ArgumentCaptor")
    class ArgumentCaptorDemo {

        @Captor
        private ArgumentCaptor<User> userCaptor;

        @Captor
        private ArgumentCaptor<String> stringCaptor;

        @Test
        @DisplayName("Capture single argument")
        void captureSingleArgument() {
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenReturn(new User(1L, "John", "john@test.com"));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.createUser("John", "john@test.com");

            // Capture what was passed to save()
            verify(repository).save(userCaptor.capture());

            // Inspect the captured user
            User savedUser = userCaptor.getValue();
            assertEquals("John", savedUser.getName());
            assertEquals("john@test.com", savedUser.getEmail());
            assertTrue(savedUser.isActive(), "New users should be active");
            assertNotNull(savedUser.getCreatedAt(), "Created timestamp should be set");
        }

        @Test
        @DisplayName("Capture multiple calls")
        void captureMultipleCalls() {
            when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(new User(1L, "John", "old@test.com")));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.updateEmail(1L, "new@test.com");

            // Capture all send() calls
            verify(emailClient, times(2)).send(
                    stringCaptor.capture(), anyString(), anyString());

            // Get all captured values
            var emails = stringCaptor.getAllValues();
            assertEquals(2, emails.size());
            assertTrue(emails.contains("old@test.com"), "Should notify old email");
            assertTrue(emails.contains("new@test.com"), "Should notify new email");
        }

        @Test
        @DisplayName("Capture and verify complex object")
        void captureComplexObject() {
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(100L);
                return u;
            });
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.createUser("Jane Doe", "jane@company.com");

            verify(repository).save(userCaptor.capture());
            User captured = userCaptor.getValue();

            // Complex assertions on captured object
            assertAll("Captured user validation",
                    () -> assertEquals("Jane Doe", captured.getName()),
                    () -> assertEquals("jane@company.com", captured.getEmail()),
                    () -> assertTrue(captured.isActive()),
                    () -> assertNotNull(captured.getCreatedAt())
            );
        }
    }

    // ==========================================================
    // SECTION 4: InOrder - Verify Call Sequence
    // ==========================================================

    @Nested
    @DisplayName("InOrder Verification")
    class InOrderDemo {

        @Test
        @DisplayName("Verify methods called in correct order")
        void verifyOrder() {
            when(repository.existsByEmail(anyString())).thenReturn(false);
            when(repository.save(any())).thenReturn(new User(1L, "John", "john@test.com"));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.createUser("John", "john@test.com");

            // Create InOrder verifier
            InOrder inOrder = inOrder(repository, emailClient);

            // Verify the sequence
            inOrder.verify(repository).existsByEmail(anyString()); // 1st
            inOrder.verify(repository).save(any());                // 2nd
            inOrder.verify(emailClient).send(anyString(), anyString(), anyString()); // 3rd
        }

        @Test
        @DisplayName("InOrder across multiple mocks")
        void verifyOrderAcrossMocks() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(emailClient.send(anyString(), anyString(), anyString())).thenReturn(true);

            userService.updateEmail(1L, "new@test.com");

            InOrder inOrder = inOrder(repository, emailClient);

            // First: find user
            inOrder.verify(repository).findById(1L);
            // Second: save user
            inOrder.verify(repository).save(any());
            // Third & Fourth: send emails (order between emails matters)
            inOrder.verify(emailClient, times(2)).send(anyString(), anyString(), anyString());
        }
    }

    // ==========================================================
    // SECTION 5: verifyNoMoreInteractions - Strict Verification
    // ==========================================================

    @Nested
    @DisplayName("No More Interactions")
    class NoMoreInteractionsDemo {

        @Test
        @DisplayName("Ensure no unexpected calls")
        void verifyNoUnexpectedCalls() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));

            userService.getUser(1L);

            // Verify expected call
            verify(repository).findById(1L);

            // Ensure no other repository methods were called
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("verifyNoInteractions - nothing should be called")
        void verifyNothingCalled() {
            // If we never call the service, mocks shouldn't be touched
            verifyNoInteractions(repository);
            verifyNoInteractions(emailClient);
        }
    }

    // ==========================================================
    // SECTION 6: Timeout Verification (for async code)
    // ==========================================================

    @Nested
    @DisplayName("Timeout Verification")
    class TimeoutDemo {

        @Test
        @DisplayName("Verify with timeout for async operations")
        void verifyWithTimeout() {
            // In real async code, this would wait for the call
            // For demo, we just show the syntax
            when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));

            userService.getUser(1L);

            // Verify with timeout (milliseconds)
            verify(repository, timeout(100)).findById(anyLong());
        }
    }
}


