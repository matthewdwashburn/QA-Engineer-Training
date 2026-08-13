package cuc.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Restores the shared SQLite database to its seeded baseline before every
 * scenario.
 *
 * <p>Without this, the suite is only green on a freshly initialised database.
 * Several scenarios leave rows behind on purpose — the validation-recovery
 * scenario ends by successfully submitting the very expense an earlier step
 * asserts is absent — so the second run of the suite fails on data the first
 * run created. Others leave rows behind only when they fail partway, which
 * turns one failure into a permanently poisoned database.
 *
 * <p>The reset deliberately rewrites only {@code expenses} and
 * {@code approvals}, leaving {@code users} untouched. Rebuilding users would
 * mean re-hashing their passwords here, and those hashes have to stay readable
 * by the Python employee app as well as the Java manager app — a risk with no
 * upside, since no end-to-end scenario writes to that table.
 *
 * <p>Both applications open a fresh connection per request
 * ({@code ConnectToDB.get_connection} and {@code DatabaseConfig.getConnection}),
 * so neither caches rows across the reset and neither needs restarting. The
 * hook also runs before the browser is launched, so no request can be in
 * flight while the tables are being rewritten.
 */
final class DatabaseState {

    /** Overrides the database location, e.g. {@code -De2e.db.path=/tmp/x.db}. */
    private static final String DB_PATH_PROPERTY = "e2e.db.path";

    /** Users the seed data references: {@code userId} 1 and 2, reviewer 3. */
    private static final Map<Integer, String> EXPECTED_USERS = Map.of(
            1, "brian",
            2, "landon",
            3, "siri");

    private DatabaseState() {
    }

    /**
     * Truncates the expense tables and replays {@code database/seed.sql}, so
     * each scenario starts from the same eight expenses and their approvals.
     */
    static void resetToSeedBaseline() {
        Path seedScript = locate("seed.sql");
        String seedSql = readScript(seedScript);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile())) {
            // PRAGMA foreign_keys is a no-op inside a transaction, so it has to
            // be set while the connection is still auto-committing.
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }

            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                requireSeededUsers(statement);

                // approvals first — it has the foreign key onto expenses.
                statement.execute("DELETE FROM approvals");
                statement.execute("DELETE FROM expenses");
                // seed.sql's approvals reference expense ids 1-8 literally, so the
                // AUTOINCREMENT counters have to go back to zero along with the rows.
                statement.execute("DELETE FROM sqlite_sequence WHERE name IN ('expenses', 'approvals')");

                executeScript(statement, seedSql);
                connection.commit();
            } catch (RuntimeException | SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to reset the end-to-end test database.", e);
        }
    }

    /**
     * Fails early, and with an actionable message, when the database has never
     * been initialised. The alternative is a seed that inserts expenses for
     * users who do not exist, surfacing later as an unexplained empty queue.
     */
    private static void requireSeededUsers(Statement statement) throws SQLException {
        Map<Integer, String> actual = new HashMap<>();
        try (ResultSet rows = statement.executeQuery("SELECT id, username FROM users WHERE id IN (1, 2, 3)")) {
            while (rows.next()) {
                actual.put(rows.getInt("id"), rows.getString("username"));
            }
        }

        if (!actual.equals(EXPECTED_USERS)) {
            throw new IllegalStateException(
                    "The database is missing its seeded users (expected " + EXPECTED_USERS + ", found " + actual
                            + "). Run: python database/init_db.py");
        }
    }

    /**
     * Runs a project SQL script one statement at a time. The scripts are the
     * source of truth for seed data, so they are replayed rather than
     * duplicated here — the same approach {@code AbstractApiIT} takes in the
     * manager app.
     */
    private static void executeScript(Statement statement, String sql) throws SQLException {
        for (String singleStatement : sql.split(";")) {
            if (!singleStatement.isBlank()) {
                statement.execute(singleStatement);
            }
        }
    }

    private static String databaseFile() {
        String override = System.getProperty(DB_PATH_PROPERTY);
        if (override != null && !override.isBlank()) {
            return Path.of(override).toAbsolutePath().toString();
        }
        return locate("expense_manager.db").toAbsolutePath().toString();
    }

    /**
     * Resolves a file under {@code database/}, whether the suite was started
     * from the {@code e2e-tests} module or from the repository root.
     */
    private static Path locate(String name) {
        Path fromModule = Path.of("..", "database", name);
        if (Files.isRegularFile(fromModule)) {
            return fromModule;
        }

        Path fromRepositoryRoot = Path.of("database", name);
        if (Files.isRegularFile(fromRepositoryRoot)) {
            return fromRepositoryRoot;
        }

        throw new IllegalStateException("Cannot locate database/" + name
                + " from " + Path.of("").toAbsolutePath() + ". Run: python database/init_db.py");
    }

    private static String readScript(Path script) {
        try {
            return Files.readString(script);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + script, e);
        }
    }
}
