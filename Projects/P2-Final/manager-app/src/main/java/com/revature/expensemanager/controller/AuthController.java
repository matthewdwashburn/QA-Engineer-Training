package com.revature.expensemanager.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JacksonException;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.service.AuthService;
import com.revature.expensemanager.service.JwtService;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public void login(Context ctx) {
        LoginRequest loginRequest;

        if (ctx.body().isBlank()) {
            respondWithInvalidBody(ctx);
            return;
        }

        try {
            loginRequest = ctx.bodyAsClass(LoginRequest.class);
        } catch (Exception e) {
            if (e instanceof JacksonException) {
                logger.warn("Login failed: request body was malformed.");
                logger.debug("Login request deserialization failure.", e);

                respondWithInvalidBody(ctx);
                return;
            }

            throw new IllegalStateException("Unable to read login request body.", e);
        }

        if (loginRequest == null) {
            respondWithInvalidBody(ctx);
            return;
        }

        String username = loginRequest.getUsername();

        logger.info("Login request received for username={}", username);

        Optional<LoginResponse> loginResponse = authService.login(loginRequest);

        if (loginResponse.isEmpty()) {
            logger.warn("Login failed for username={}", username);

            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse(
                            "Invalid username or password. This login portal is for managers only."));
            return;
        }

        LoginResponse response = loginResponse.orElseThrow();

        String token = jwtService.generateToken(
                response.getId(),
                response.getUsername(),
                response.getRole());

        response.setToken(token);

        logger.info(
                "Manager login successful: userId={}, username={}",
                response.getId(),
                response.getUsername());

        ctx.status(HttpStatus.OK).json(response);
    }

    public void me(Context ctx) {
        Integer userId = ctx.attribute("userId");
        String username = ctx.attribute("username");
        String role = ctx.attribute("role");

        ctx.status(HttpStatus.OK).json(new LoginResponse(userId, username, role));
    }

    private void respondWithInvalidBody(Context ctx) {
        logger.warn("Login failed: request body was missing or invalid.");

        ctx.status(HttpStatus.BAD_REQUEST)
                .json(new ErrorResponse("Invalid login request body."));
    }
}
