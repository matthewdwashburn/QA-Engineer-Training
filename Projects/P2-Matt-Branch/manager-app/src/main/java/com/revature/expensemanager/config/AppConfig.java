package com.revature.expensemanager.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {
    private final String jwtSecret;
    private final int jwtExpirationHours;

    public AppConfig() {
        // A missing .env is ignored rather than fatal. Containers supply these
        // values as real environment variables, which dotenv-java falls back to
        // when a key is absent from the file, so no .env has to be shipped in
        // the image. Local runs are unaffected: their .env is still read.
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        this.jwtSecret = getRequired(dotenv, "JWT_SECRET");
        this.jwtExpirationHours = getRequiredInt(dotenv, "JWT_EXPIRATION_HOURS");
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtExpirationHours() {
        return jwtExpirationHours;
    }

    private String getRequired(Dotenv dotenv, String key) {
        String value = dotenv.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(key + " is required.");
        }

        return value;
    }

    private int getRequiredInt(Dotenv dotenv, String key) {
        String value = getRequired(dotenv, key);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(key + " must be an integer.", e);
        }
    }
}