package com.revature.domain;


//stubbing patterns - controlling mock behavior
// when().thenReturn() - basic return values
// when().thenThrow() - Simulate exceptions
// when().thenAnswer() - Dynamic Responses
// Consecutive calls - Different values each time
// doX.when() - for void methods and spies

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Stubbing Patterns Demo")
public class Demo_Stubbing_Patterns {

    @Mock
    private UserRepository repository;

    @Mock
    private EmailClient emailClient;

    @Test
    @DisplayName("Return different values for different arguments")
    void differentArgumentsDifferentResults() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "John", "john@test.com")));
        when(repository.findById(2L))
                .thenReturn(Optional.of(new User(2L, "Jane", "jane@test.com")));
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertEquals("John", repository.findById(1L).get().getName());
        assertEquals("Jane", repository.findById(2L).get().getName());
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("Return value for any argument using matcher")
    void anyArgumentMatcher() {
        User defaultUser = new User(1L, "Default", "default@test.com");
        when(repository.findById(anyLong())).thenReturn(Optional.of(defaultUser));

        //All IDS return the same user
        assertEquals("Default", repository.findById(1L).get().getName());
        assertEquals("Default", repository.findById(999L).get().getName());
        assertEquals("Default", repository.findById(12345L).get().getName());
    }


    // ==========================================================
    // SECTION 2: Stubbing Exceptions - when().thenThrow()
    // ==========================================================

    @Nested
    @DisplayName("Exception Stubbing with thenThrow()")
    class ExceptionStubbing {

        @Test
        @DisplayName("Throw exception for specific input")
        void throwForSpecificInput() {
            when(repository.findById(-1L))
                    .thenThrow(new IllegalArgumentException("ID cannot be negative"));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.findById(-1L)
            );
            assertEquals("ID cannot be negative", ex.getMessage());
        }

        @Test
        @DisplayName("Throw runtime exception")
        void throwRuntimeException() {
            when(repository.findAll())
                    .thenThrow(new RuntimeException("Database connection failed"));

            assertThrows(RuntimeException.class, () -> repository.findAll());
        }

        @Test
        @DisplayName("Simulate network failure")
        void simulateNetworkFailure() {
            when(emailClient.send(anyString(), anyString(), anyString()))
                    .thenThrow(new RuntimeException("Connection timeout"));

            assertThrows(RuntimeException.class, () ->
                    emailClient.send("test@test.com", "Subject", "Body"));
        }
    }

    // ==========================================================
    // SECTION 3: Dynamic Responses - when().thenAnswer()
    // ==========================================================

    @Nested
    @DisplayName("Dynamic Stubbing with thenAnswer()")
    class DynamicStubbing {

        @Test
        @DisplayName("Response based on input argument")
        void dynamicResponseBasedOnInput() {
            when(repository.findById(anyLong())).thenAnswer(invocation -> {
                Long id = invocation.getArgument(0);
                if (id <= 0) {
                    return Optional.empty();
                }
                return Optional.of(new User(id, "User" + id, "user" + id + "@test.com"));
            });

            // Negative ID returns empty
            assertTrue(repository.findById(-1L).isEmpty());

            // Zero returns empty
            assertTrue(repository.findById(0L).isEmpty());

            // Positive IDs return dynamic users
            assertEquals("User5", repository.findById(5L).get().getName());
            assertEquals("user100@test.com", repository.findById(100L).get().getEmail());
        }

        @Test
        @DisplayName("Save method returns input with ID assigned")
        void saveReturnsModifiedInput() {
            when(repository.save(any(User.class))).thenAnswer(invocation -> {
                User input = invocation.getArgument(0);
                input.setId(100L);  // Simulate database assigning ID
                return input;
            });

            User newUser = new User("Test", "test@test.com");
            assertNull(newUser.getId());

            User saved = repository.save(newUser);
            assertEquals(100L, saved.getId());
            assertEquals("Test", saved.getName());
        }

        @Test
        @DisplayName("Complex conditional logic in answer")
        void complexLogicInAnswer() {
            when(repository.existsByEmail(anyString())).thenAnswer(invocation -> {
                String email = invocation.getArgument(0);
                // Simulate that certain emails are already registered
                return email.endsWith("@existing.com");
            });

            assertTrue(repository.existsByEmail("john@existing.com"));
            assertTrue(repository.existsByEmail("jane@existing.com"));
            assertFalse(repository.existsByEmail("new@gmail.com"));
        }
    }

    // ==========================================================
    // SECTION 4: Consecutive Calls - Different Values Each Time
    // ==========================================================

    @Nested
    @DisplayName("Consecutive Calls Stubbing")
    class ConsecutiveCallsStubbing {

        @Test
        @DisplayName("Return different values on successive calls")
        void consecutiveReturns() {
            when(repository.count())
                    .thenReturn(0L)   // First call
                    .thenReturn(1L)   // Second call
                    .thenReturn(5L);  // Third and subsequent calls

            assertEquals(0L, repository.count());  // First
            assertEquals(1L, repository.count());  // Second
            assertEquals(5L, repository.count());  // Third
            assertEquals(5L, repository.count());  // Fourth (still 5)
            assertEquals(5L, repository.count());  // Fifth (still 5)
        }

        @Test
        @DisplayName("Simulate retry scenario - fail then succeed")
        void failThenSucceed() {
            when(emailClient.send(anyString(), anyString(), anyString()))
                    .thenReturn(false)  // First attempt fails
                    .thenReturn(true);  // Retry succeeds

            // First call fails
            assertFalse(emailClient.send("test@test.com", "Subject", "Body"));

            // Retry succeeds
            assertTrue(emailClient.send("test@test.com", "Subject", "Body"));
        }

        @Test
        @DisplayName("Throw then return - error recovery")
        void throwThenReturn() {
            when(repository.findAll())
                    .thenThrow(new RuntimeException("Database busy"))
                    .thenReturn(java.util.List.of(new User("John", "john@test.com")));

            // First call throws
            assertThrows(RuntimeException.class, () -> repository.findAll());

            // Retry succeeds
            var users = repository.findAll();
            assertEquals(1, users.size());
        }
    }

    // ==========================================================
    // SECTION 5: Void Method Stubbing - doX().when()
    // ==========================================================

    @Nested
    @DisplayName("Void Method Stubbing")
    class VoidMethodStubbing {

        @Test
        @DisplayName("doNothing() - explicit no-op")
        void doNothingExample() {
            // Explicit that delete does nothing (default behavior, but clear)
            doNothing().when(repository).deleteById(anyLong());

            // This doesn't throw or do anything
            repository.deleteById(1L);

            // We can verify it was called
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("doThrow() - void method throws exception")
        void doThrowExample() {
            doThrow(new RuntimeException("Cannot delete"))
                    .when(repository).deleteById(-1L);

            assertThrows(RuntimeException.class,
                    () -> repository.deleteById(-1L));
        }

        @Test
        @DisplayName("doAnswer() - custom behavior for void method")
        void doAnswerExample() {
            // Track what was deleted
            java.util.List<Long> deletedIds = new java.util.ArrayList<>();

            doAnswer(invocation -> {
                Long id = invocation.getArgument(0);
                deletedIds.add(id);
                System.out.println("Deleting user: " + id);
                return null;  // void methods return null
            }).when(repository).deleteById(anyLong());

            repository.deleteById(1L);
            repository.deleteById(2L);
            repository.deleteById(3L);

            assertEquals(3, deletedIds.size());
            assertTrue(deletedIds.contains(2L));
        }

        @Test
        @DisplayName("Consecutive void behaviors")
        void consecutiveVoidBehaviors() {
            doNothing()
                    .doThrow(new RuntimeException("Rate limited"))
                    .when(repository).deleteById(anyLong());

            // First call succeeds
            repository.deleteById(1L);  // No exception

            // Second call throws
            assertThrows(RuntimeException.class,
                    () -> repository.deleteById(2L));
        }
    }

    // ==========================================================
    // SECTION 6: Argument Matchers Deep Dive
    // ==========================================================

    @Nested
    @DisplayName("Argument Matchers")
    class ArgumentMatchers {

//        @Test
//        @DisplayName("Common matchers")
//        void commonMatchers() {
//            // anyLong(), anyString(), anyList(), etc.
//            when(repository.findById(anyLong())).thenReturn(Optional.empty());
//
//            // eq() for exact match when mixing with other matchers
//            when(repository.existsByEmail(eq("admin@test.com"))).thenReturn(true);
//            when(repository.existsByEmail(argThat(s -> s.endsWith("@test.com"))))
//                    .thenReturn(false);
//
//            // isNull()
//            when(repository.save(isNull())).thenThrow(new NullPointerException());
//        }

        @Test
        @DisplayName("Custom argument matcher")
        void customMatcher() {
            when(repository.save(argThat(user ->
                    user != null &&
                            user.getName() != null &&
                            user.getName().startsWith("VIP_")
            ))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);  // VIP users get low IDs
                return u;
            });

            User vip = new User("VIP_John", "john@test.com");
            User saved = repository.save(vip);
            assertEquals(1L, saved.getId());
        }
    }
}
