package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

@Epic("Manager Portal Backend")
@Feature("Authentication")
@ParentSuite("Manager - Service Layer")
@Tag("unit")
@DisplayName("JWT Service")
class JwtServiceTest {

    private static final String SECRET = "test-jwt-secret";
    private static final String ISSUER = "revature-expense-manager-manager-app";
    private static final int EXPIRATION_HOURS = 2;

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void generateToken_returnsNonBlankToken_forValidManagerInformation() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);

        String token = jwtService.generateToken(10, "manager1", "manager");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void generateToken_includesExpectedManagerClaims() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);

        DecodedJWT token = JWT.decode(jwtService.generateToken(10, "manager1", "manager"));

        assertEquals(10, token.getClaim("userId").asInt());
        assertEquals("manager1", token.getClaim("username").asString());
        assertEquals("manager", token.getClaim("role").asString());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void generateToken_includesExpectedIssuer() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);

        DecodedJWT token = JWT.decode(jwtService.generateToken(10, "manager1", "manager"));

        assertEquals(ISSUER, token.getIssuer());
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void generateToken_includesExpirationAfterIssueTime() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);

        DecodedJWT token = JWT.decode(jwtService.generateToken(10, "manager1", "manager"));

        assertNotNull(token.getIssuedAt());
        assertNotNull(token.getExpiresAt());
        assertTrue(token.getExpiresAt().after(token.getIssuedAt()));
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void validateToken_returnsDecodedToken_whenTokenIsValid() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);
        String token = jwtService.generateToken(10, "manager1", "manager");

        DecodedJWT decodedToken = jwtService.validateToken(token);

        assertEquals(10, jwtService.getUserId(decodedToken));
        assertEquals("manager1", jwtService.getUsername(decodedToken));
        assertEquals("manager", jwtService.getRole(decodedToken));
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void validateToken_rejectsExpiredToken() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);
        String expiredToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject("manager1")
                .withIssuedAt(Instant.now().minusSeconds(7_200))
                .withExpiresAt(Instant.now().minusSeconds(3_600))
                .sign(Algorithm.HMAC256(SECRET));

        assertThrows(JWTVerificationException.class, () -> jwtService.validateToken(expiredToken));
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void validateToken_rejectsTokenSignedWithDifferentSecret() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);
        String incorrectlySignedToken = JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(Instant.now().plusSeconds(3_600))
                .sign(Algorithm.HMAC256("different-test-jwt-secret"));

        assertThrows(JWTVerificationException.class, () -> jwtService.validateToken(incorrectlySignedToken));
    }

    @Test
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("security")
    void validateToken_rejectsTokenWithIncorrectIssuer() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION_HOURS);
        String incorrectIssuerToken = JWT.create()
                .withIssuer("untrusted-issuer")
                .withExpiresAt(Instant.now().plusSeconds(3_600))
                .sign(Algorithm.HMAC256(SECRET));

        assertThrows(JWTVerificationException.class, () -> jwtService.validateToken(incorrectIssuerToken));
    }

    @ParameterizedTest(name = "secret [{0}] is rejected")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void constructor_rejectsNullOrBlankSecret(String secret) {
        assertThrows(IllegalStateException.class, () -> new JwtService(secret, EXPIRATION_HOURS));
    }

    @ParameterizedTest(name = "expiration hours [{0}] are rejected")
    @ValueSource(ints = {0, -1})
    @Issue("KAN-20")
    @Story("Manager Login")
    @Tag("negative")
    void constructor_rejectsNonPositiveExpirationHours(int expirationHours) {
        assertThrows(IllegalStateException.class, () -> new JwtService(SECRET, expirationHours));
    }
}
