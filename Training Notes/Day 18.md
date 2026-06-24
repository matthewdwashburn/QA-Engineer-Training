# Day 18 - PostgreSQL Joins, Functions & Stored Procedures + Java Exercises

---

## SQL Joins

### INNER JOIN — only rows with a match on both sides

```sql
SELECT c.email, oh.order_id, oh.status
FROM customer c
INNER JOIN order_header oh ON oh.customer_id = c.customer_id;
-- customers with no orders are excluded
```

### LEFT JOIN — all left rows, NULLs for unmatched right

```sql
SELECT c.email, oh.order_id, oh.status
FROM customer c
LEFT JOIN order_header oh ON oh.customer_id = c.customer_id;
-- customers with no orders appear; their order columns are NULL
```

### RIGHT JOIN — all right rows, NULLs for unmatched left

```sql
SELECT c.email, oh.order_id, oh.status
FROM customer c
RIGHT JOIN order_header oh ON oh.customer_id = c.customer_id;
-- all orders appear; if FK enforced, customer cols will never be NULL
```

### FULL OUTER JOIN — all rows from both sides, NULLs where no match

```sql
SELECT c.email, oh.order_id, oh.status
FROM customer c
FULL OUTER JOIN order_header oh ON oh.customer_id = c.customer_id
ORDER BY c.email NULLS LAST, oh.order_id NULLS LAST;
```

### CROSS JOIN — every combination (Cartesian product)

```sql
SELECT c.email, p.sku
FROM customer c
CROSS JOIN product p;
-- n customers × m products → n*m rows; no ON clause
```

### `NULLS LAST` — sort NULLs to the bottom

```sql
ORDER BY c.email NULLS LAST   -- NULLs appear after all non-null values
```

---

## PL/pgSQL Functions

Functions return a value and can be called inside a `SELECT`.

### Structure

```sql
CREATE OR REPLACE FUNCTION function_name(param_name TYPE)
RETURNS return_type
LANGUAGE plpgsql
AS $$
DECLARE
    v_variable TYPE;        -- declare local variables here
BEGIN
    -- logic
    RETURN value;
END;
$$;
```

### `SELECT … INTO` — assign a query result to a variable

```sql
SELECT COALESCE(SUM(total_amount), 0)
INTO v_total_spent
FROM orders
WHERE customer_id = p_customer_id;
```

### `COALESCE(val, fallback)` — return first non-NULL value

```sql
COALESCE(SUM(total_amount), 0)  -- if SUM is NULL (no rows), return 0
```

### `CASE` expression inside a function

```sql
RETURN CASE
    WHEN v_total_spent >= 10000 THEN 0.20
    WHEN v_total_spent >= 5000  THEN 0.10
    WHEN v_total_spent >= 1000  THEN 0.05
    ELSE 0.00
END;
```

### Calling a function

```sql
SELECT calculate_discount_rate(2);   -- call inline like any expression
```

---

## PL/pgSQL Stored Procedures

Procedures don't return a value (use for side effects like `UPDATE`). Called with `CALL`.

### Structure

```sql
CREATE OR REPLACE PROCEDURE procedure_name(param_name TYPE)
LANGUAGE plpgsql
AS $$
DECLARE
    v_variable TYPE;
BEGIN
    -- logic
END;
$$;
```

### `:=` — variable assignment

```sql
v_discount_rate := calculate_discount_rate(p_customer_id);
-- functions can be called on the right-hand side
```

### `EXISTS (SELECT 1 …)` — check if a row exists

```sql
SELECT EXISTS (SELECT 1 FROM customer WHERE customer_id = p_customer_id)
INTO v_exists;
```

### `IF … THEN … END IF` — conditional

```sql
IF NOT v_exists THEN
    RAISE NOTICE 'Customer % not found', p_customer_id;
    RETURN;   -- early exit from procedure
END IF;
```

### `RAISE NOTICE` — print a message to the client

```sql
RAISE NOTICE 'Customer % discount updated to %', p_customer_id, v_discount_rate * 100;
-- % is a placeholder filled left-to-right by the trailing arguments
```

### Calling a procedure

```sql
CALL apply_customer_discount(1);
```

---

## Function vs Procedure

| | Function | Procedure |
|---|---|---|
| Returns value | Yes (`RETURN`) | No |
| Called with | `SELECT fn()` | `CALL proc()` |
| Use for | Calculations, lookups | Side effects (INSERT/UPDATE) |
| Can call functions | Yes | Yes |

---

## Java Exercises

### Arrays & Loops (`ArrayLoopsLab.java`)

**In-place array reversal — two-pointer swap**
```java
int start = 0, end = data.length - 1;
while (start < end) {
    int temp = data[start];
    data[start] = data[end];
    data[end] = temp;
    start++; end--;
}
```

**Min/max using sentinel values**
```java
int min = Integer.MAX_VALUE;   // start high so any element beats it
int max = Integer.MIN_VALUE;   // start low so any element beats it
```

**Selection sort — nested loops, no library**
```java
for (int i = 0; i < data.length; i++)
    for (int j = i; j < data.length; j++)
        if (data[j] < data[i]) { swap(data, i, j); }
```

**`Arrays.toString(arr)`** — prints array contents as `[1, 2, 3]`

---

### String Splitting (`StringSplit.java`)

Split a string into pairs of characters; last char padded with `_` if odd length.

**Key patterns:**
```java
s.length() % 2          // check even/odd
s.charAt(i * 2)         // index into every other character
String.valueOf(char)    // convert char → String
stringArr[i] = firstLetter + secondLetter;  // string concat
```

---

### Linked List (`Node.java`, `ReverseLinkedList.java`)

**Node — simple linked list node with public fields**
```java
public class Node {
    public int data;
    public Node next;
    public Node(int data) { this.data = data; }
}
```

**Iterative reversal — 3-pointer technique**

Walk the list re-pointing each node backward. Classic interview pattern.

```java
Node curr = head, prev = null, next = null;
while (curr != null) {
    next = curr.next;   // save next before overwriting
    curr.next = prev;   // reverse the pointer
    prev = curr;        // advance prev
    curr = next;        // advance curr
}
return prev;            // prev is now the new head
```

**Traversing a linked list**
```java
while (node != null) {
    System.out.println(node.data);
    node = node.next;
}
```

---

### OOP — `Student` Class (`Student.java`, `StudentDemo.java`)

**Static vs instance fields**
```java
private static int nextId = 1;      // shared across ALL instances
private final int id;               // set once in constructor, never changes
```

**Auto-incrementing ID in constructor**
```java
this.id = nextId++;   // assign current value, then increment the static counter
```

**`@Override toString()`** — controls what `System.out.println(obj)` prints
```java
@Override
public String toString() {
    return "Student [id=" + id + ", name=" + name + "]";
}
```

**`@Override equals()` + `hashCode()`** — define what makes two objects "equal"

Must override both together: if two objects are `.equals()`, they must have the same `.hashCode()`.

```java
@Override
public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Student s = (Student) o;
    return s.id == id && Objects.equals(name, s.name);
}

@Override
public int hashCode() {
    return Objects.hash(id, name);   // hash based on same fields as equals
}
```

**`==` vs `.equals()`**
```java
s1 == s2          // reference equality — same object in memory?
s1.equals(s2)     // value equality — same id and name?
```

**`Objects.equals(a, b)`** — null-safe field comparison (won't NPE if field is null)

**Calling a static method on the class (not an instance)**
```java
Student.getEnrollmentCount();   // static — no object needed
```
