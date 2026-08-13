package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import at.favre.lib.crypto.bcrypt.BCrypt;

import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.model.User;
import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Authentication")
@ParentSuite("Manager - Service Layer")
@DisplayName("Authentication Service")
@Tag("unit")
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthService authService;

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("smoke")
    void login_returnsManagerResponse_whenManagerCredentialsAreValid() {
        // Arrange: Hash password, create mock objects, and stub DAO lookup
        String rawPassword = "password123";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
        User manager = new User(10, "manager1", hashedPassword, "manager");
        LoginRequest request = new LoginRequest("manager1", rawPassword);
        when(userDAO.findByUsername("manager1")).thenReturn(Optional.of(manager));

        // Act: Invoke login method on auth service with manager role
        Optional<LoginResponse> result = authService.login(request);

        // Assert: Verify successful manager response payload
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getId());
        assertEquals("manager1", result.get().getUsername());
        assertEquals("manager", result.get().getRole());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsEmpty_whenEmployeeAttemptsLogin() {
        // Arrange: Create an employee account and stub DAO lookup
        String rawPassword = "password123";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
        User employee = new User(20, "employee1", hashedPassword, "employee");
        LoginRequest request = new LoginRequest("employee1", rawPassword);
        when(userDAO.findByUsername("employee1")).thenReturn(Optional.of(employee));

        // Act: Invoke login method with employee role
        Optional<LoginResponse> result = authService.login(request);

        // Assert: Verify returns an empty optional (no object assigned = no login success)
        assertTrue(result.isEmpty());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsEmpty_whenUsernameIsUnknown() {
        LoginRequest request = new LoginRequest("unknown-manager", "password123");
        when(userDAO.findByUsername("unknown-manager")).thenReturn(Optional.empty());

        Optional<LoginResponse> result = authService.login(request);

        assertTrue(result.isEmpty());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsEmpty_whenPasswordIsIncorrect() {
        String correctPassword = "password123";
        User manager = new User(
                10,
                "manager1",
                BCrypt.withDefaults().hashToString(12, correctPassword.toCharArray()),
                "manager");
        LoginRequest request = new LoginRequest("manager1", "incorrect-password");
        when(userDAO.findByUsername("manager1")).thenReturn(Optional.of(manager));

        Optional<LoginResponse> result = authService.login(request);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest(name = "username [{0}] returns empty")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsEmpty_withoutCallingDao_whenUsernameIsNullOrBlank(String username) {
        LoginRequest request = new LoginRequest(username, "password123");

        Optional<LoginResponse> result = authService.login(request);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userDAO);
    }

    @ParameterizedTest(name = "password [{0}] returns empty")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsEmpty_withoutCallingDao_whenPasswordIsNullOrBlank(String password) {
        LoginRequest request = new LoginRequest("manager1", password);

        Optional<LoginResponse> result = authService.login(request);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userDAO);
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_propagatesDaoException() {
        LoginRequest request = new LoginRequest("manager1", "password123");
        when(userDAO.findByUsername("manager1"))
                .thenThrow(new IllegalStateException("Database unavailable"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.login(request));

        assertEquals("Database unavailable", exception.getMessage());
    }
}
