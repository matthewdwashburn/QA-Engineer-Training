# Day 19 - Java JDBC, DAO Pattern & SQL Practice

---

## Java JDBC

JDBC (Java Database Connectivity) is Java's standard API for connecting to and querying relational databases.

### Connection URL format

```java
private static final String URL = "jdbc:sqlite:week4_jdbc_demo.db";
// SQLite creates the .db file automatically if it doesn't exist
```

### `DriverManager.getConnection(url)` — open a database connection

```java
try (Connection conn = DriverManager.getConnection(URL)) {
    // use conn here
}
// try-with-resources automatically closes the connection
```

### `Statement` — for static SQL (no user input)

```java
try (Statement st = conn.createStatement()) {
    st.executeUpdate("DROP TABLE IF EXISTS customer");
    st.executeUpdate("""
        CREATE TABLE customer (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT NOT NULL UNIQUE,
            name TEXT NOT NULL
        )
    """);
}
```

### `PreparedStatement` — parameterized SQL, prevents SQL injection

```java
try (PreparedStatement ps = conn.prepareStatement(
    "INSERT INTO customer (email, name) VALUES (?,?)"
)) {
    ps.setString(1, "jdbc@example.com");   // 1-indexed
    ps.setString(2, "JDBC Explorer");
    ps.executeUpdate();
}
```

- `setString(n, val)`, `setLong(n, val)`, `setDouble(n, val)` — bind parameters by position
- `executeUpdate()` — for INSERT/UPDATE/DELETE
- `executeQuery()` — for SELECT, returns `ResultSet`

### SQL Injection Demo

```java
// UNSAFE: user input concatenated directly into SQL
String unsafeSQL = "SELECT name FROM user WHERE name = '" + userInput + "'";
// If userInput = "' OR '1'='1" — returns ALL rows

// SAFE: PreparedStatement treats input as a literal value, never executes it
String safeSQL = "SELECT name FROM user WHERE name = ?";
try (PreparedStatement ps = conn.prepareStatement(safeSQL)) {
    ps.setString(1, userInput);
}
```

### `ResultSet` — iterate query results

```java
try (ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {                   // advance cursor row by row
        long id     = rs.getLong("id");
        String name = rs.getString("name");
    }
}
```

- `rs.getString(columnName)` or `rs.getString(columnIndex)` (1-indexed)
- `rs.getLong()`, `rs.getDouble()`, etc.

### `Statement.RETURN_GENERATED_KEYS` — get auto-generated PK after INSERT

```java
try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    ps.executeUpdate();
    try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
            return keys.getLong(1);   // the new row's generated id
        }
    }
}
```

---

## DAO Pattern

**DAO (Data Access Object)** separates database logic from business logic. The interface defines what operations are possible; the JDBC implementation handles how.

### Structure

```
ProductDAO (interface)          — defines CRUD contract
JdbcProductDAO implements ProductDAO — JDBC implementation
Product (model)                — plain data class, no DB logic
Launcher                       — wires everything together
```

### DAO Interface

```java
public interface ProductDAO {
    long insert(Product product) throws SQLException;
    Optional<Product> findBySku(String sku) throws Exception;
    void updatePrice(String sku, double newPrice) throws SQLException;
    void deleteBySku(String sku) throws SQLException;
    List<Product> findAll() throws SQLException;
}
```

- Return `Optional<T>` for queries that may return no row (instead of null)
- `Optional.empty()` — no result; `Optional.of(obj)` — result found

### JDBC Implementation pattern

```java
public class JdbcProductDAO implements ProductDAO {
    private final Connection connection;   // injected via constructor

    public JdbcProductDAO(Connection connection) {
        this.connection = connection;
    }
}
```

### Private `mapRow` helper — convert a ResultSet row to a model object

```java
private Product mapRow(ResultSet rs) throws SQLException {
    return new Product(
        rs.getLong("id"),
        rs.getString("sku"),
        rs.getString("name"),
        rs.getDouble("price")
    );
}
```

### `Optional.ifPresent()` — consume result only if present

```java
Optional<Product> loaded = dao.findBySku("SKU-1");
loaded.ifPresent(System.out::println);
```

### Immutable model class

```java
public final class Product {
    private final long id;
    private final String sku;
    // ... all fields final, set in constructor, getters only
}
```

---

## SQL Exercises (StratascratchSQL / HackerRank)

### `FLOOR(AVG(...))` — truncate average to integer

```sql
SELECT co.CONTINENT, FLOOR(AVG(ci.population)) AS AvgCityPopulation
FROM COUNTRY co JOIN CITY ci ON co.CODE = ci.COUNTRYCODE
GROUP BY co.CONTINENT;
```

### `SUBSTR(str, -3)` — last 3 characters for ORDER BY

```sql
SELECT Name FROM Students
WHERE Marks > 75
ORDER BY SUBSTR(Name, -3), id;
-- negative index: count from end of string
```

### `ABS()` — absolute value

```sql
SELECT ABS(e.max_e - m.max_m) FROM max_marketing m
CROSS JOIN max_engineering e;
```

### CTEs to isolate max per group, then aggregate

```sql
WITH max_score_per_hacker_challenge AS (
    SELECT s.hacker_id, s.challenge_id, MAX(s.score) as max_score, h.name
    FROM submissions s
    JOIN hackers h ON h.hacker_id = s.hacker_id
    GROUP BY s.hacker_id, s.challenge_id, h.name
)
SELECT hacker_id, name, SUM(max_score) as total_score
FROM max_score_per_hacker_challenge
GROUP BY hacker_id, name
HAVING total_score > 0
ORDER BY total_score DESC, hacker_id;
```

### `COUNT(DISTINCT col)` with multi-condition WHERE

```sql
SELECT h.nationality, COUNT(DISTINCT u.unit_id) AS apart_count
FROM airbnb_hosts h
JOIN airbnb_units u ON h.host_id = u.host_id
WHERE h.age < 30 AND u.unit_type = 'Apartment'
GROUP BY h.nationality
ORDER BY apart_count DESC;
```

---

## Notes

**p0 Stretch Goals:** hash passwords, add REST endpoints, write unit & integration tests, output expense info to a file, put output specs in query params/headers
