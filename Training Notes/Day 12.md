# Day 12 - Java OOP Deep Dive, Interfaces, JDBC & Python Interview Review

---

## Interfaces

An **interface** is a contract — it declares *what* a class must do, not *how*. A class can implement multiple interfaces (Java's answer to multiple inheritance).

```java
public interface Carnivore {
    default void eatMeat() {           // default method — provides a body, can be overridden
        System.out.println("Eating meat");
    }
}

public interface Herbivore {
    default void eatPlants() {
        System.out.println("Eating plants");
    }
}
```

```java
// Extend one class AND implement multiple interfaces
public class Bear extends Animal implements Carnivore, Herbivore {

    @Override
    public void makeSound() { System.out.println("Grrrr"); }

    @Override
    public void eatPlants() {
        Herbivore.super.eatPlants();   // call the default interface method explicitly
        System.out.println("Eating Berries");
    }
}
```

`Herbivore.super.eatPlants()` — required disambiguation when two implemented interfaces have a `default` method with the same name.

---

## Abstract Class vs Interface

| | Abstract Class | Interface |
|---|---|---|
| Keyword | `abstract class` | `interface` |
| Extend / Implement | `extends` (one only) | `implements` (many) |
| Can have fields | Yes | No (only constants) |
| Can have concrete methods | Yes | Only as `default` |
| Use when | sharing code + state across related classes | defining a capability contract across unrelated classes |

```java
public abstract class Animal {
    String species;                    // fields allowed

    public void eat() { ... }          // concrete method — subclasses inherit it

    public abstract void makeSound();  // abstract — subclasses MUST implement
}
```

---

## Polymorphism — Upcasting & Downcasting

A parent reference can hold a child object (**upcasting** — always safe). The method called is always the child's version (dynamic dispatch). But you can only access methods defined on the parent type.

```java
Parent pc = new Child();   // upcast — Child IS-A Parent, so this is always valid
pc.work();                 // calls Child's work() — dynamic dispatch
// pc.play();              // compile error — Parent doesn't know about play()
```

**Downcasting** — casting a parent reference back to a child. Only safe if the object is actually that child type:

```java
Child c1 = (Child) pc;     // safe — pc is actually a Child object underneath
c1.play();                 // now child-only methods are accessible

// Child c0 = (Child) p;   // ClassCastException at runtime — p is actually a Parent
```

**Covariant return type** — an overriding method can return a more specific type than the parent:

```java
// Parent:
public Parent someMethod() { return new Parent(); }

// Child override — returns Child instead of Parent, which is still valid:
@Override
public Child someMethod() { return new Child(); }
```

---

## Constructor Overloading

Multiple constructors with different parameter lists — same rules as method overloading:

```java
public Building() { area = 500; walls = 8; }             // no-arg default
public Building(int area) { this.area = area; }           // one-arg
public Building(int value, boolean forWalls) { ... }      // two-arg

System.out.println(this);   // inside constructor — calls toString() on this instance
```

---

## `protected` Access Modifier

`protected` members are accessible within the same package **and** from subclasses in other packages, but not from unrelated classes:

```java
public class Employee {
    protected void setPassword(String p) { this.password = p; }  // protected
}

// Different package, unrelated class:
// Bob.setPassword("x");   // compile error — not accessible

// Different package, but subclass:
public class DemoEmployee extends Employee {
    public void updatePassword(String p) {
        setPassword(p);    // allowed — DemoEmployee IS a subclass of Employee
    }
}
```

---

## `final` Class

A `final` class cannot be subclassed — no `extends` allowed:

```java
public final class Money { ... }   // nobody can extend Money
```

Use for security-sensitive or value types where you don't want behavior changed through inheritance.

---

## `long` Type

64-bit integer — use when values may exceed `int`'s ~2 billion limit:

```java
long amountMinor = 100000L;   // L suffix denotes a long literal
```

---

## `HashSet<T>`

Unordered collection with **no duplicates**. Uses `hashCode()` to bucket items and `equals()` to check for duplicates — so both must be correctly overridden for custom objects:

```java
import java.util.HashSet;

HashSet<Money> moneySet = new HashSet<>();
moneySet.add(m1);
moneySet.add(m2);          // if m1.equals(m2) and same hashCode — only stored once
moneySet.size();           // number of unique elements
```

---

## Polymorphism with Collections

Store different subtypes in a list typed to the parent/interface — loop calls the right implementation on each:

```java
ArrayList<Vehicle> vehicles = new ArrayList<>();
vehicles.add(new ElectricCar("Tesla", 2026));
vehicles.add(new GasCar("Ford", 2010));

for (Vehicle v : vehicles) {
    v.fuelCostPer100m();   // calls ElectricCar's or GasCar's version — polymorphic dispatch
}
```

---

## DAO Pattern (Data Access Object)

A design pattern that **separates database logic from business logic**. You define what operations exist in an interface, then implement them in a DAO class. The rest of the app only talks to the interface.

```
EmployeeDAOInterface  ← contract (what can be done)
        ↑ implements
EmployeeDAO           ← implementation (how the DB is queried)

Launcher              ← business logic, only uses EmployeeDAO, doesn't touch SQL directly
```

```java
public interface EmployeeDAOInterface {
    ArrayList<Employee> getEmployees();
    Employee insertEmployee(Employee employee);
}

public class EmployeeDAO implements EmployeeDAOInterface {
    @Override
    public ArrayList<Employee> getEmployees() { ... }

    @Override
    public Employee insertEmployee(Employee employee) { ... }
}
```

---

## JDBC — Java Database Connectivity

Java's standard API for talking to relational databases. Requires a driver for your DB flavor (e.g., `sqlite-jdbc`).

### Connection setup

```java
import java.sql.*;

// Register the driver for your DB flavor
Class.forName("org.sqlite.JDBC");   // throws ClassNotFoundException if driver jar is missing

// JDBC URL format: jdbc:<flavor>:<path or host>
String url = "jdbc:sqlite:/path/to/file.db";

Connection conn = DriverManager.getConnection(url);   // throws SQLException
```

### `try-with-resources`

Auto-closes the connection when the block exits — no manual `conn.close()` needed:

```java
try (Connection conn = ConnectionUtil.getConnection()) {
    // use conn here
} catch (SQLException e) {
    e.printStackTrace();   // prints full stack trace of the exception
}
```

### `Statement` — simple queries (no user input)

```java
Statement s = conn.createStatement();
ResultSet rs = s.executeQuery("SELECT * FROM employees");

while (rs.next()) {                         // advance cursor; false when no more rows
    int id    = rs.getInt("employee_id");   // read column by name
    String fn = rs.getString("first_name");
}
```

### `PreparedStatement` — parameterized queries (use for any user input)

Prevents SQL injection by keeping data separate from the SQL string:

```java
String sql = "INSERT INTO employees (first_name, last_name) VALUES (?, ?)";
PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

ps.setString(1, employee.getFirst_name());   // 1-indexed — sets first ?
ps.setString(2, employee.getLast_name());    // sets second ?

ps.executeUpdate();   // run INSERT / UPDATE / DELETE (not SELECT)

// Retrieve auto-generated primary key
ResultSet keys = ps.getGeneratedKeys();
if (keys.next()) {
    employee.setEmployee_id(keys.getInt(1));
}
```

### `throws SQLException`

Methods that do JDBC work must declare they can throw `SQLException` — or handle it with try/catch:

```java
public static Connection getConnection() throws SQLException { ... }
```

### `ConnectionUtil` pattern

A utility class with a static method that centralizes connection creation — the rest of the app just calls `ConnectionUtil.getConnection()`:

```java
public class ConnectionUtil {
    public static Connection getConnection() throws SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:/path/to/db");
    }
}
```

### JDBC quick reference

| Object | Purpose |
|---|---|
| `Connection` | active link to the database |
| `Statement` | execute plain SQL strings |
| `PreparedStatement` | execute parameterized SQL — use for any user input |
| `ResultSet` | cursor over rows returned by a query |
| `DriverManager` | creates connections from a JDBC URL |
| `SQLException` | checked exception thrown by all JDBC operations |

---

## Python Interview Review — Cleaning Strings to Alphanumeric

Three approaches to strip non-alphanumeric characters from strings.

### `str.isalnum()` with list comprehension (no imports)
```python
mixed_list = ["user_123!", "hello#world", "python3.14"]
clean_list = ["".join(char for char in item if char.isalnum()) for item in mixed_list]
# ['user123', 'helloworld', 'python314']
```

### Same pattern on a single string
```python
text = "Hello, World! 123 @Python$"
cleaned = "".join([char for char in text if char.isalnum()])
# 'HelloWorld123Python'
```

### `re.sub()` with negated character class
```python
import re
text = "User_Name! 2026 #Data$"
cleaned = re.sub(r"[^a-zA-Z0-9]", "", text)
# 'UserName2026Data'
# [^a-zA-Z0-9] matches anything that is NOT a letter or digit — replace with ""
```
