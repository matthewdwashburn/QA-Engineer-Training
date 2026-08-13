package com.revature.expensemanager.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {

    /** Default database used when nothing overrides it (production / local run). */
    static final String DEFAULT_DB_URL = "jdbc:sqlite:../database/expense_manager.db";

    /** Seeded database the JMeter plans run against when {@code DATABASE_ENV=testing}. */
    static final String TESTING_DB_URL = "jdbc:sqlite:../database/expense_manager_test.db";

    /** System property that overrides the database URL (e.g. {@code -Ddb.url=...}). */
    static final String DB_URL_PROPERTY = "db.url";

    /** Environment variable that overrides the database URL. */
    static final String DB_URL_ENV = "DB_URL";

    /** {@code .env} key that selects the load-test database. */
    static final String DATABASE_ENV_KEY = "DATABASE_ENV";

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found.", e);
        }

        return DriverManager.getConnection(resolveDbUrl());
    }

    /**
     * Resolves the JDBC URL fresh on every call so integration tests can point
     * the DAOs at a throwaway database. Precedence: {@code -Ddb.url} system
     * property, then {@code DB_URL} environment variable, then
     * {@code DATABASE_ENV=testing} from {@code .env}, then the default.
     *
     * <p>The explicit overrides are deliberately checked before {@code .env}.
     * A developer who leaves {@code DATABASE_ENV=testing} set for a JMeter run
     * would otherwise redirect the integration suite onto the seeded load-test
     * database, which {@link DatabaseSeeder} truncates.
     */
    static String resolveDbUrl() {
        String property = System.getProperty(DB_URL_PROPERTY);
        if (property != null && !property.isBlank()) {
            return property;
        }

        String env = System.getenv(DB_URL_ENV);
        if (env != null && !env.isBlank()) {
            return env;
        }

        if (isTestingEnvironment()) {
            return TESTING_DB_URL;
        }

        return DEFAULT_DB_URL;
    }

    /**
     * Whether {@code .env} selects the load-test database.
     *
     * <p>Reading {@code .env} is deferred to a holder class rather than done
     * per call: {@code getConnection()} runs once per JMeter sample, and a file
     * read on that path would show up in the very numbers the plans measure.
     * Callers that set {@code db.url} return above without loading it at all.
     */
    static boolean isTestingEnvironment() {
        return DotenvHolder.TESTING;
    }

    /**
     * Loaded on first use. A missing {@code .env} is ignored rather than fatal:
     * the load-test database is opt-in, and the build has to run without one.
     */
    private static final class DotenvHolder {
        static final boolean TESTING = "testing".equalsIgnoreCase(
                Dotenv.configure()
                        .ignoreIfMissing()
                        .load()
                        .get(DATABASE_ENV_KEY));
    }
}
