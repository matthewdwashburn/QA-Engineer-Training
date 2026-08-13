package com.revature.expensemanager;

import com.revature.expensemanager.config.DatabaseSeeder;

public class Main {

    /** Port used when {@code PORT} is not set, matching the documented local setup. */
    private static final int DEFAULT_PORT = 7001;

    /** Environment variable that overrides the listen port. */
    private static final String PORT_ENV = "PORT";

    public static void main(String[] args) {
        // No-op unless DATABASE_ENV=testing selects the load-test database.
        DatabaseSeeder.seedIfTesting();

        AppFactory.build().start(resolvePort());
    }

    /**
     * Resolves the listen port from the {@code PORT} environment variable,
     * falling back to {@link #DEFAULT_PORT}.
     *
     * <p>Read from the environment rather than {@code .env} for the same reason
     * as {@code DB_URL}: the port is a property of wherever the app is deployed,
     * not of the developer's local configuration.
     */
    private static int resolvePort() {
        String value = System.getenv(PORT_ENV);

        if (value == null || value.isBlank()) {
            return DEFAULT_PORT;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    PORT_ENV + " must be an integer, but was '" + value + "'.", e);
        }
    }
}
