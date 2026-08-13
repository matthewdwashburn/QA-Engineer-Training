package com.revature.expensemanager.middleware;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.service.JwtService;
import com.revature.expensemanager.allure.ParentSuite;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Authentication")
@ParentSuite("Manager - Service Layer")
@Tag("unit")
@DisplayName("Authentication Middleware")
class AuthMiddlewareTest {

    @Mock
    private Context ctx;

    @Mock
    private JwtService jwtService;

    @Mock
    private DecodedJWT decodedJWT;

    @InjectMocks
    private AuthMiddleware authMiddleware;

    @ParameterizedTest(name = "authorization header [{0}] is unauthorized")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsUnauthorized_whenAuthorizationHeaderIsMissingOrBlank(String authorizationHeader) {
        when(ctx.header("Authorization")).thenReturn(authorizationHeader);
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header is required.");
        verify(ctx).skipRemainingHandlers();
        verifyNoInteractions(jwtService);
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsUnauthorized_whenAuthorizationHeaderDoesNotUseBearerScheme() {
        when(ctx.header("Authorization")).thenReturn("Basic encoded-credentials");
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header must use Bearer token.");
        verify(ctx).skipRemainingHandlers();
        verifyNoInteractions(jwtService);
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsUnauthorized_whenTokenVerificationFails() {
        when(ctx.header("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtService.validateToken("invalid-token"))
                .thenThrow(new JWTVerificationException("Invalid token"));
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
        verify(ctx).skipRemainingHandlers();
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("smoke")
    void requireManager_addsManagerIdentityAttributes_whenTokenIsValid() {
        mockValidTokenClaims(10, "manager1", "MANAGER");

        authMiddleware.requireManager(ctx);

        verify(ctx).attribute("userId", 10);
        verify(ctx).attribute("username", "manager1");
        verify(ctx).attribute("role", "MANAGER");
        verify(ctx, never()).status(any(HttpStatus.class));
        verify(ctx, never()).skipRemainingHandlers();
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsUnauthorized_whenTokenHasNoUserIdClaim() {
        mockValidTokenClaims(null, "manager1", "manager");
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token claims.");
        verify(ctx).skipRemainingHandlers();
    }

    @ParameterizedTest(name = "username claim [{0}] is unauthorized")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsUnauthorized_whenTokenHasNullOrBlankUsername(String username) {
        mockValidTokenClaims(10, username, "manager");
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token claims.");
        verify(ctx).skipRemainingHandlers();
    }

    @ParameterizedTest(name = "role claim [{0}] is unauthorized")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsUnauthorized_whenTokenHasNullOrBlankRole(String role) {
        mockValidTokenClaims(10, "manager1", role);
        when(ctx.status(HttpStatus.UNAUTHORIZED)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token claims.");
        verify(ctx).skipRemainingHandlers();
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void requireManager_returnsForbidden_whenAuthenticatedUserIsNotAManager() {
        mockValidTokenClaims(20, "employee1", "employee");
        when(ctx.status(HttpStatus.FORBIDDEN)).thenReturn(ctx);

        authMiddleware.requireManager(ctx);

        assertErrorResponse(HttpStatus.FORBIDDEN, "Manager access required.");
        verify(ctx).skipRemainingHandlers();
        verify(ctx, never()).attribute(anyString(), any());
    }

    private void mockValidTokenClaims(Integer userId, String username, String role) {
        when(ctx.header("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.validateToken("valid-token")).thenReturn(decodedJWT);
        when(jwtService.getUserId(decodedJWT)).thenReturn(userId);
        when(jwtService.getUsername(decodedJWT)).thenReturn(username);
        when(jwtService.getRole(decodedJWT)).thenReturn(role);
    }

    private void assertErrorResponse(HttpStatus expectedStatus, String expectedMessage) {
        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(ctx).status(expectedStatus);
        verify(ctx).json(errorCaptor.capture());
        assertEquals(expectedMessage, errorCaptor.getValue().getMessage());
    }
}
