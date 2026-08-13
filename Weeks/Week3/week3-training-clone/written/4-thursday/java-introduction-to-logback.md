# Introduction to Logback and SLF4J

## Learning Objectives

- Explain **SLF4J** as a logging **facade** and **Logback** as a common implementation.
- Locate **`logback.xml`** (or `logback-spring.xml`) and describe **appenders** and **loggers**.
- Contrast logging with `System.out.println` for applications.

## Why This Matters

Production services need **levels**, **structured output**, and **routing** (console, file, aggregation). SLF4J keeps code independent of the underlying implementation; Logback is widely used in Spring and enterprise Java—Friday dives into levels and formatting.

## The Concept

### SLF4J

**Simple Logging Facade for Java** — API in code:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

Logger log = LoggerFactory.getLogger(MyClass.class);
log.info("User {} logged in", userId);
```

Swap bindings (Logback, Log4j2, JUL) without changing every call site.

### Logback

Successor to Log4j 1.x; native SLF4J implementation. Configuration via XML (classic) or Groovy (less common now).

### Core configuration pieces

- **`<configuration>`** root.
- **`<appender>`** — where events go (`ConsoleAppender`, `RollingFileAppender`, …).
- **`<logger>`** — level and appender attachment per package/class.
- **`<root>`** — default level and appenders.

Minimal `logback.xml` idea:

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

Place on classpath (e.g. `src/main/resources`).

### Why not only `println`?

No levels, no timestamps, no correlation IDs, hard to disable in production, mixes with real stdout consumers.

## Code Example

```java
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void placeOrder(String id) {
        log.debug("Placing order {}", id);
        // ...
        log.info("Order placed {}", id);
    }
}
```

## Summary

- Log through SLF4J; back with Logback (typical) or another binding.
- Configure appenders and logger levels in `logback.xml`.
- Prefer structured logging over raw prints for real apps.

## Additional Resources

- [SLF4J Manual](https://www.slf4j.org/manual.html)
- [Logback Documentation — Configuration](https://logback.qos.ch/documentation.html)
