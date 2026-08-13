# Pair programming: JDBC persistence for two entities

## Learning goals

- Implement **CRUD** with **`PreparedStatement`** (no string concatenation of user input).
- Separate **DAO** (SQL + mapping) from a thin **service** layer that coordinates use cases.
- Practice **driver/navigator** communication and **swap** halfway.

## Scenario

Build a tiny **inventory + orders** console app backed by **SQLite** (file `pair_store.db`):

1. **`Product`** — `id`, `sku` (unique), `name`, `price`, `stock_qty`
2. **`CustomerOrder`** — `id`, `customer_email`, `status` (`OPEN` / `PAID`), `placed_at` (optional timestamp)
3. **`OrderLine`** — composite identity `(order_id, line_no)`, `product_id` (or sku reference), `qty`, `unit_price`

You may simplify types (e.g. `REAL` for money) but must keep **foreign key** from line → order and line → product.

## Rotation 1 — Product (Partner A drives DAO)

**Partner A (Driver):**

- Define `ProductDao` interface: `long insert(Product p)`, `Optional<Product> findBySku(String sku)`, `List<Product> findAll()`, `void updateStock(String sku, int newQty)`, `void deleteBySku(String sku)` (or equivalent).
- Implement `JdbcProductDao` using **`try-with-resources`**.

**Partner B (Navigator):**

- Implement `ProductService` (or `App` section) that calls the DAO and prints results.
- Write a **`main`** flow: insert two products, list all, update stock, fetch by sku.

## Rotation 2 — Order + lines (swap drivers)

**Partner B (Driver):**

- `OrderDao` (or split `OrderHeaderDao` / `OrderLineDao` if you prefer) with:
  - create order with status `OPEN`
  - add line items (validate product exists, qty > 0)
  - mark order `PAID`

**Partner A (Navigator):**

- Extend **`main`** (or tests) to: create order for an email, add lines, pay order, print order total.

## Hard requirements

- **No** `Statement` for SQL containing user-controlled values — **`PreparedStatement`** only for those paths.
- **Close** connections/statement/result sets — **`try-with-resources`**.
- **`ON DELETE CASCADE`** or explicit deletes — document your choice in `PAIR_REPORT.md`.

## Stretch

- Batch insert lines with **`addBatch`** / **`executeBatch`** inside a **manual transaction** (`setAutoCommit(false)` … `commit` / `rollback`).

## Definition of done

- `mvn -q exec:java` (or documented `java -cp ...` command) runs without error and prints plausible output.
- `PAIR_REPORT.md` lists **who** implemented which DAO and **one** JDBC debugging story.

## Reference

- `written/4-thursday/jdbc-dao-design-pattern.md`
- `written/4-thursday/simple-and-prepared-statements.md`
- `demos/4-thursday/code/demo_dao_pattern.java`
