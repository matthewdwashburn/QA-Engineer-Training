package com.revature.expensemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.service.AuthService;
import com.revature.expensemanager.service.JwtService;
import com.revature.expensemanager.allure.ParentSuite;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

/**
 * Unit coverage for {@link AuthController#login(Context)} against a mocked
 * {@link Context}.
 *
 * <p><strong>Why every test stubs {@code ctx.body()}.</strong> The controller's
 * first statement is {@code ctx.body().isBlank()}, added when {@code LoginIT}
 * showed that an empty or malformed request body returned {@code 500} instead
 * of {@code 400}. A Mockito mock returns {@code null} for any method nobody
 * stubbed, so that guard threw {@link NullPointerException} and took all three
 * tests in this class down at once - a failure with nothing to do with what
 * they were asserting.
 *
 * <p>A mock only knows what the test tells it. When production code starts
 * reading something new off {@code Context}, every test holding a mocked
 * {@code Context} has to be told about it too, or it fails for a reason the
 * test author never wrote down. The integration suite never saw this: it drives
 * a real server, so a real body was always there.
 */
@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Authentication")
@ParentSuite("Manager - Controller Layer")
@Tag("api")
@DisplayName("Authentication Controller")
class AuthControllerTest {

    private static final String INVALID_LOGIN_MESSAGE =
            "Invalid username or password. This login portal is for managers only.";

    private static final String INVALID_BODY_MESSAGE = "Invalid login request body.";

    /** Any non-blank body: enough to clear the guard so the test reaches its own subject. */
    private static final String NON_BLANK_BODY = "{\"username\":\"manager1\",\"password\":\"password123\"}";

    @Mock
    private Context ctx;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("smoke")
    void login_returnsOkAndToken_whenManagerIsAuthenticated() {
        LoginRequest request = new LoginRequest("manager1", "password123");
        LoginResponse response = new LoginResponse(10, "manager1", "manager");
        when(ctx.body()).thenReturn(NON_BLANK_BODY);
        when(ctx.bodyAsClass(LoginRequest.class)).thenReturn(request);
        when(authService.login(request)).thenReturn(Optional.of(response));
        when(jwtService.generateToken(10, "manager1", "manager")).thenReturn("test-token");
        when(ctx.status(HttpStatus.OK)).thenReturn(ctx);

        authController.login(ctx);

        assertEquals("test-token", response.getToken());
        verify(authService).login(request);
        verify(jwtService).generateToken(10, "manager1", "manager");
        verify(ctx).status(HttpStatus.OK);
        verify(ctx).json(response);
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsUnauthorizedWithoutToken_whenAuthenticationIsRejected() {
        LoginRequest request = new LoginRequest("manager1", "incorrect-password");
        when(ctx.body()).thenReturn(NON_BLANK_BODY);
        when(ctx.bodyAsClass(LoginRequest.class)).thenReturn(request);
        when(authService.login(request)).thenReturn(Optional.empty());
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authController.login(ctx);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(ctx).status(HttpStatus.UNAUTHORIZED);
        verify(ctx).json(errorCaptor.capture());
        assertEquals(INVALID_LOGIN_MESSAGE, errorCaptor.getValue().getMessage());
        verify(jwtService, never()).generateToken(anyInt(), anyString(), anyString());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsBadRequest_whenRequestBodyIsBlank() {
        // Guards the exact line whose introduction broke this class, so a future
        // reader can see why the ctx.body() stub above is not incidental.
        when(ctx.body()).thenReturn("");
        when(ctx.status(HttpStatus.BAD_REQUEST)).thenReturn(ctx);

        authController.login(ctx);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(ctx).status(HttpStatus.BAD_REQUEST);
        verify(ctx).json(errorCaptor.capture());
        assertEquals(INVALID_BODY_MESSAGE, errorCaptor.getValue().getMessage());
        verify(ctx, never()).bodyAsClass(LoginRequest.class);
        verify(authService, never()).login(any());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void login_returnsBadRequest_whenRequestBodyCannotBeDeserialised() {
        // Previously asserted 401 by way of authService.login(null). The body guard
        // now rejects an unreadable body before the service is consulted at all.
        when(ctx.body()).thenReturn(NON_BLANK_BODY);
        when(ctx.bodyAsClass(LoginRequest.class)).thenReturn(null);
        when(ctx.status(HttpStatus.BAD_REQUEST)).thenReturn(ctx);

        authController.login(ctx);

        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(ctx).status(HttpStatus.BAD_REQUEST);
        verify(ctx).json(errorCaptor.capture());
        assertEquals(INVALID_BODY_MESSAGE, errorCaptor.getValue().getMessage());
        verify(authService, never()).login(any());
        verify(jwtService, never()).generateToken(anyInt(), anyString(), anyString());
    }
}
