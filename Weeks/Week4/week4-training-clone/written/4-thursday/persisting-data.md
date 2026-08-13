# Persisting Data with JDBC

## Learning Objectives

- Perform **CRUD** operations through JDBC (`INSERT`, `UPDATE`, `DELETE`, `SELECT`).
- Use `executeUpdate`, generated keys, and affected row counts.
- Describe **connection pooling** basics and why raw `DriverManager` is insufficient at scale.

## Why This Matters

Applications exist to **change** system state safely. JDBC CRUD is the minimal skill to wire Java services to the relational models you designed Monday–Wednesday. Pooling is how services sustain concurrency without exhausting database connections.

## The Concept

### CRUD vocabulary (quick definition)

- **Create**: `INSERT`
- **Read**: `SELECT`
- **Update**: `UPDATE`
- **Delete**: `DELETE`

### INSERT with generated keys

```java
String sql = "INSERT INTO customer (email) VALUES (?)";
try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    ps.setString(1, email);
    int inserted = ps.executeUpdate();
    try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
            long id = keys.getLong(1);
        }
    }
}
```

### UPDATE / DELETE

`executeUpdate()` returns **rows affected**—useful for optimistic checks.

```java
String sql = "UPDATE inventory SET qty = qty - ? WHERE sku = ? AND qty >= ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setInt(1, delta);
    ps.setString(2, sku);
    ps.setInt(3, delta);
    int updated = ps.executeUpdate();
    if (updated == 0) {
        // handle insufficient stock or missing sku
    }
}
```

### SELECT (read)

Use `executeQuery()` and stream through `ResultSet`—prefer mapping rows to domain objects in a dedicated layer (DAO pattern—next reading).

### Transactions: when you must turn off auto-commit

If you need multiple statements to succeed/fail together, disable auto-commit:

```java
conn.setAutoCommit(false);
try {
    // 1) update inventory
    // 2) insert order
    conn.commit();
} catch (Exception ex) {
    conn.rollback();
    throw ex;
} finally {
    conn.setAutoCommit(true);
}
```

This prevents “half persisted” workflows when the second statement fails.

### Batch processing

Already introduced: `addBatch` / `executeBatch` for bulk writes inside a transaction.

### Connection pooling basics

Opening a TCP/SSL connection and authenticating is **expensive**. A **pool** keeps warm connections; your code **borrows** and **returns** them. Frameworks configure pools with max size, timeouts, and health checks. Leaks occur when connections are not closed—always **try-with-resources**.

### Common pitfalls (what to watch for)

- **Not checking row counts**: `executeUpdate()` returning 0 is often a business condition (missing row, failed optimistic condition).
- **Using `SELECT *`** in production DAOs: schema changes can break mapping or waste bandwidth; prefer explicit columns.
- **Swallowing SQL exceptions**: log/translate them; don’t silently ignore.
- **N+1 queries**: calling a DAO in a loop that runs one query per item; consider joins or bulk queries.

## Summary

- **CRUD** maps cleanly to JDBC: `executeUpdate` for writes, `executeQuery` for reads.
- **Generated keys** and **row counts** support robust application flows.
- **Pools** (`DataSource`) are standard in production; always release connections promptly.

## Additional Resources

- [Oracle JDBC: Retrieving Auto Generated Keys](https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html)
- [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
- [Spring JDBC Template (optional framework layer)](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html)
