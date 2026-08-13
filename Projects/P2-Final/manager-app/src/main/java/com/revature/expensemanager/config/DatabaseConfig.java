package com.revature.expensemanager.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {

    /** Default database used when nothing overrides it (production / local run). */
    static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/expense_manager";

    /** Seeded database the JMeter plans run against when {@code DATABASE_ENV=testing}. */
    static final String TESTING_DB_URL = "jdbc:postgresql://localhost:5432/expense_manager_test";

    /** System property that overrides the database URL (e.g. {@code -Ddb.url=...}). */
    static final String DB_URL_PROPERTY = "db.url";

    /** Environment variable that overrides the database URL. */
    static final String DB_URL_ENV = "DB_URL";

    /** {@code .env} key that selects the load-test database. */
    static final String DATABASE_ENV_KEY = "DATABASE_ENV";

    /** Credentials. System property first, matching the URL's precedence. */
    static final String DB_USER_PROPERTY = "db.user";
    static final String DB_USER_ENV = "DB_USER";
    static final String DB_PASSWORD_PROPERTY = "db.password";
    static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    static final String DEFAULT_DB_USER = "expense";
    static final String DEFAULT_DB_PASSWORD = "expense";

    /**
     * Pools keyed by JDBC URL, capped so a test that redirects {@code db.url}
     * repeatedly cannot accumulate pools without bound. Access is synchronized;
     * the hit path below never reaches this map.
     */
    private static final int MAX_CACHED_POOLS = 4;
    private static final Map<String, HikariDataSource> POOLS = new LinkedHashMap<>();

    /**
     * The pool for the URL resolved on the previous call. Read without locking
     * on the hit path, which is every request under load.
     */
    private static volatile String activeUrl;
    private static volatile HikariDataSource activePool;

    public static Connection getConnection() throws SQLException {
        String url = resolveDbUrl();

        // Steady state: the URL never changes, so this is two volatile reads.
        HikariDataSource pool = activePool;
        if (pool != null && url.equals(activeUrl)) {
            return pool.getConnection();
        }

        return poolFor(url).getConnection();
    }

    private static synchronized HikariDataSource poolFor(String url) {
        HikariDataSource pool = POOLS.get(url);

        if (pool == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(resolve(DB_USER_PROPERTY, DB_USER_ENV, DEFAULT_DB_USER));
            config.setPassword(resolve(DB_PASSWORD_PROPERTY, DB_PASSWORD_ENV, DEFAULT_DB_PASSWORD));
            config.setMaximumPoolSize(intSetting("db.pool.maxSize", "DB_POOL_MAX_SIZE", 10));
            config.setMinimumIdle(intSetting("db.pool.minIdle", "DB_POOL_MIN_IDLE", 2));
            config.setPoolName("expense-manager-" + POOLS.size());

            pool = new HikariDataSource(config);

            // Evict in insertion order, but never the pool currently in use:
            // a caller may still be holding a connection out of it.
            if (POOLS.size() >= MAX_CACHED_POOLS) {
                POOLS.keySet().stream()
                        .filter(cached -> !cached.equals(activeUrl))
                        .findFirst()
                        .ifPresent(evicted -> POOLS.remove(evicted).close());
            }
            POOLS.put(url, pool);
        }

        activeUrl = url;
        activePool = pool;

        return pool;
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

    private static String resolve(String property, String envKey, String fallback) {
        String value = System.getProperty(property);
        if (value != null && !value.isBlank()) {
            return value;
        }

        value = System.getenv(envKey);
        if (value != null && !value.isBlank()) {
            return value;
        }

        return fallback;
    }

    private static int intSetting(String property, String envKey, int fallback) {
        try {
            return Integer.parseInt(resolve(property, envKey, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Closes every pool. Used by test teardown, so a suite that redirects
     * {@code db.url} at a temporary database can drop it afterwards.
     */
    public static synchronized void closePools() {
        POOLS.values().forEach(HikariDataSource::close);
        POOLS.clear();
        activeUrl = null;
        activePool = null;
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
