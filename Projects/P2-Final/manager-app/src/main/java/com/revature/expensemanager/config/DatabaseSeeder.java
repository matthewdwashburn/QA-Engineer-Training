package com.revature.expensemanager.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class DatabaseSeeder {

    @SuppressWarnings("unused")
    private static final Random RANDOM = new Random();

    /**
     * Truncates and repopulates the load-test database, but only when
     * {@code DATABASE_ENV=testing}.
     *
     * <p>The check is delegated to {@link DatabaseConfig} rather than reading
     * {@code .env} again here. Two independent reads could disagree - this
     * method deletes every row in {@code expenses} and {@code approvals}, so it
     * must never conclude "testing" against a connection {@code DatabaseConfig}
     * resolved to a different database.
     */
    public static void seedIfTesting() {

        if (!DatabaseConfig.isTestingEnvironment()) {
            return;
        }

        System.out.println("Testing environment detected.");
        System.out.println("Resetting and seeding database...");

        try (Connection conn = DatabaseConfig.getConnection()) {

            conn.setAutoCommit(false);

            clearDatabase(conn);
            seedExpenses(conn, 5000);

            conn.commit();

            System.out.println("Database seeded successfully.");

        } catch (Exception e) {
            throw new RuntimeException("Unable to seed database.", e);
        }
    }

    private static void clearDatabase(Connection conn) throws SQLException {

        // One statement replaces the delete-then-reset-counters dance: TRUNCATE
        // clears both tables and RESTART IDENTITY puts the id sequences back to
        // 1, which seedExpenses below relies on when it pairs approvals to
        // expense ids 1..amount. approvals is named alongside expenses because
        // it holds the foreign key onto it.
        try (var statement = conn.createStatement()) {
            statement.execute("TRUNCATE approvals, expenses RESTART IDENTITY");
        }
    }

private static void seedExpenses(Connection conn, int amount)
        throws SQLException {

    String[] categories = {
            "TRAVEL",
            "MEALS",
            "LODGING",
            "OFFICE_SUPPLIES",
            "EQUIPMENT",
            "SOFTWARE",
            "TRAINING",
            "OTHER"
    };

    PreparedStatement expenseStmt = conn.prepareStatement(
            """
            INSERT INTO expenses
            (userId, amount, description, category, date)
            VALUES (?, ?, ?, ?, ?)
            """
    );

    PreparedStatement approvalStmt = conn.prepareStatement(
            """
            INSERT INTO approvals
            (expenseId, status)
            VALUES (?, ?)
            """
    );

    Random random = new Random();

    for (int i = 1; i <= amount; i++) {

        // Alternate between employee IDs 2 and 3
        int userId = (i % 2 == 0) ? 2 : 3;

        expenseStmt.setInt(1, userId);
        expenseStmt.setDouble(2, 25 + random.nextDouble() * 975);
        expenseStmt.setString(3, "Performance Test Expense " + i);
        expenseStmt.setString(4, categories[random.nextInt(categories.length)]);
        expenseStmt.setString(5,
                LocalDate.now().minusDays(random.nextInt(90)).toString());

        expenseStmt.addBatch();

        // Every seeded expense starts as pending
        approvalStmt.setInt(1, i);
        approvalStmt.setString(2, "PENDING");

        approvalStmt.addBatch();
    }

    // Batched rather than executed per row: against a file this was 10,000
    // local writes, but against Postgres it would be 10,000 network round trips.
    expenseStmt.executeBatch();
    approvalStmt.executeBatch();

    expenseStmt.close();
    approvalStmt.close();
}

}