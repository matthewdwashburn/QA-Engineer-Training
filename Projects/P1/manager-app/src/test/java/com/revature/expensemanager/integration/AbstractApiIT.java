package com.revature.expensemanager.integration;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.revature.expensemanager.AppFactory;
import com.revature.expensemanager.config.AppConfig;
import com.revature.expensemanager.config.DatabaseConfig;
import com.revature.expensemanager.service.JwtService;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.Javalin;
import io.restassured.RestAssured;

/**
 * Shared real-HTTP test foundation for manager API integration tests.
 *
 * <p>Each test starts from the repository's schema and seed SQL files, but uses
 * a temporary SQLite database selected through {@link DatabaseConfig}'s
 * {@code db.url} system-property override.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractApiIT {
    private static final String DB_URL_PROPERTY = "db.url";
    private static final String JWT_ISSUER = "revature-expense-manager-manager-app";
    private static final int MANAGER_ID = 3;
    private static final String MANAGER_USERNAME = "siri";
    private static final int EMPLOYEE_ID = 1;
    private static final String EMPLOYEE_USERNAME = "brian";

    /** Cost-12 hash of "password", computed once per JVM rather than once per test. */
    private static final String HASHED_PASSWORD =
            BCrypt.withDefaults().hashToString(12, "password".toCharArray());

    private Javalin app;
    private Path databaseFile;
    private String previousDatabaseUrl;
    private AppConfig appConfig;
    private JwtService jwtService;

    @BeforeAll
    void startApplication() throws IOException {
        previousDatabaseUrl = System.getProperty(DB_URL_PROPERTY);
        databaseFile = Files.createTempFile("manager-api-it-", ".db");
        System.setProperty(DB_URL_PROPERTY, "jdbc:sqlite:" + databaseFile.toAbsolutePath());

        appConfig = new AppConfig();
        jwtService = new JwtService(appConfig.getJwtSecret(), appConfig.getJwtExpirationHours());
        rebuildDatabase();

        app = AppFactory.build().start(0);
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = app.port();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void resetDatabase() throws IOException {
        rebuildDatabase();
    }

    /** Logs in through the real endpoint and returns its JWT response field. */
    protected String loginAsManager() {
        return given()
                .contentType("application/json")
                .body(Map.of("username", MANAGER_USERNAME, "password", "password"))
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    /** Returns a production-compatible, unexpired manager JWT. */
    protected String validManagerToken() {
        return jwtService.generateToken(MANAGER_ID, MANAGER_USERNAME, "manager");
    }

    /** Returns a production-compatible, unexpired employee JWT. */
    protected String validEmployeeToken() {
        return jwtService.generateToken(EMPLOYEE_ID, EMPLOYEE_USERNAME, "employee");
    }

    /** Returns a correctly signed manager JWT whose expiry is in the past. */
    protected String expiredManagerToken() {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(JWT_ISSUER)
                .withSubject(MANAGER_USERNAME)
                .withClaim("userId", MANAGER_ID)
                .withClaim("username", MANAGER_USERNAME)
                .withClaim("role", "manager")
                .withIssuedAt(now.minus(2, ChronoUnit.HOURS))
                .withExpiresAt(now.minus(1, ChronoUnit.HOURS))
                .sign(Algorithm.HMAC256(appConfig.getJwtSecret()));
    }

    @AfterAll
    void stopApplication() throws IOException {
        try {
            if (app != null) {
                app.stop();
            }
        } finally {
            RestAssured.reset();
            if (previousDatabaseUrl == null) {
                System.clearProperty(DB_URL_PROPERTY);
            } else {
                System.setProperty(DB_URL_PROPERTY, previousDatabaseUrl);
            }
            if (databaseFile != null) {
                Files.deleteIfExists(databaseFile);
            }
        }
    }

    private void rebuildDatabase() throws IOException {
        try (Connection connection = DatabaseConfig.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            executeSqlFile(statement, databaseScript("schema.sql"));
            seedUsers(connection);
            executeSqlFile(statement, databaseScript("seed.sql"));
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to rebuild the integration-test database.", e);
        }
    }

    private void seedUsers(Connection connection) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            insertUser(statement, "brian", "employee");
            insertUser(statement, "landon", "employee");
            insertUser(statement, "siri", "manager");
        }
    }

    private void insertUser(PreparedStatement statement, String username, String role) throws SQLException {
        statement.setString(1, username);
        statement.setString(2, HASHED_PASSWORD);
        statement.setString(3, role);
        statement.executeUpdate();
    }

    private void executeSqlFile(Statement statement, Path script) throws IOException, SQLException {
        // The project scripts are the source of truth for schema and non-user seed data.
        for (String sql : Files.readString(script).split(";")) {
            if (!sql.isBlank()) {
                statement.execute(sql);
            }
        }
    }

    private Path databaseScript(String name) {
        Path fromManagerApp = Path.of("..", "database", name);
        if (Files.isRegularFile(fromManagerApp)) {
            return fromManagerApp;
        }

        Path fromRepositoryRoot = Path.of("database", name);
        if (Files.isRegularFile(fromRepositoryRoot)) {
            return fromRepositoryRoot;
        }

        throw new IllegalStateException("Cannot locate database script: " + name);
    }
}
