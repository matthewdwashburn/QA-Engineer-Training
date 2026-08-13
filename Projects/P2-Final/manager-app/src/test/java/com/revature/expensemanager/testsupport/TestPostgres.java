package com.revature.expensemanager.testsupport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.testcontainers.containers.PostgreSQLContainer;

import com.revature.expensemanager.config.DatabaseConfig;

/**
 * The one Postgres container the whole test suite shares.
 *
 * <p>Started once per JVM and never stopped: Testcontainers' Ryuk sidecar
 * removes it when the JVM exits. Surefire runs the suite in a single fork, so a
 * container per test class would multiply several seconds of startup across
 * every class for no isolation the databases below do not already give.
 *
 * <p>Isolation is one database per test class, handed out by
 * {@link #databaseUrlFor(String)}. Sharing a single database would couple the
 * classes through {@link DatabaseConfig}'s pool cache, which is keyed by URL.
 */
public final class TestPostgres {

    /**
     * Points the suite at an already-running Postgres instead of starting a
     * container, e.g. {@code jdbc:postgresql://localhost:5432/expense_manager}
     * for the one in the compose stack. Set {@code TEST_POSTGRES_USER} and
     * {@code TEST_POSTGRES_PASSWORD} alongside it.
     *
     * <p>Needed where Testcontainers cannot reach the Docker daemon. Isolation
     * is unaffected: {@link #databaseUrlFor(String)} still gives each test class
     * a database of its own, it is just created on the external server.
     */
    private static final String EXTERNAL_URL_ENV = "TEST_POSTGRES_URL";

    /** Matches the image the compose stack runs, so tests and production agree. */
    private static final PostgreSQLContainer<?> CONTAINER =
            usingExternalServer() ? null : new PostgreSQLContainer<>("postgres:17-alpine");

    static {
        if (CONTAINER != null) {
            CONTAINER.start();
        }
    }

    private TestPostgres() {
    }

    private static boolean usingExternalServer() {
        String url = System.getenv(EXTERNAL_URL_ENV);
        return url != null && !url.isBlank();
    }

    /** The server's own URL, whose database the per-class ones are created beside. */
    private static String serverUrl() {
        return CONTAINER == null ? System.getenv(EXTERNAL_URL_ENV) : CONTAINER.getJdbcUrl();
    }

    /**
     * Creates a database of the given name and returns its JDBC URL.
     *
     * <p>Call from {@code @BeforeAll}. Names should be unique per test class;
     * an existing database of that name is dropped first, so a rerun in the
     * same JVM starts clean.
     */
    public static String databaseUrlFor(String databaseName) {
        try (Connection connection = adminConnection();
                Statement statement = connection.createStatement()) {

            statement.execute("DROP DATABASE IF EXISTS " + databaseName);
            statement.execute("CREATE DATABASE " + databaseName);

        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create test database: " + databaseName, e);
        }

        // Swap the database off the end of the server's own URL, keeping the
        // port (which Testcontainers maps at random).
        String url = serverUrl();
        int lastSlash = url.lastIndexOf('/');
        int queryStart = url.indexOf('?', lastSlash);
        String suffix = queryStart == -1 ? "" : url.substring(queryStart);

        return url.substring(0, lastSlash + 1) + databaseName + suffix;
    }

    public static String username() {
        return CONTAINER == null
                ? envOrDefault("TEST_POSTGRES_USER", "expense")
                : CONTAINER.getUsername();
    }

    public static String password() {
        return CONTAINER == null
                ? envOrDefault("TEST_POSTGRES_PASSWORD", "expense")
                : CONTAINER.getPassword();
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    /**
     * Points {@link DatabaseConfig} at the given URL and returns whatever
     * {@code db.url} held before, for the matching {@link #release(String)}.
     */
    public static String claim(String jdbcUrl) {
        String previous = System.getProperty("db.url");

        System.setProperty("db.url", jdbcUrl);
        System.setProperty("db.user", username());
        System.setProperty("db.password", password());

        return previous;
    }

    /**
     * Restores {@code db.url} and closes the pools opened against the test
     * database, so its connections do not outlive the class that made them.
     */
    public static void release(String previousUrl) {
        DatabaseConfig.closePools();

        System.clearProperty("db.user");
        System.clearProperty("db.password");

        if (previousUrl == null) {
            System.clearProperty("db.url");
        } else {
            System.setProperty("db.url", previousUrl);
        }
    }

    private static Connection adminConnection() throws SQLException {
        return DriverManager.getConnection(serverUrl(), username(), password());
    }
}
