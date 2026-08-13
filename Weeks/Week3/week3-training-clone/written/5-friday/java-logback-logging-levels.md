# Logback Logging Levels and Formatting

## Learning Objectives

- Use **TRACE, DEBUG, INFO, WARN, ERROR** appropriately.
- Configure **thresholds** per logger and inherit from parent loggers.
- Customize **patterns** and understand filtering at appenders.

## Why This Matters

Levels let operators tune **verbosity** without redeploying code: INFO in production, DEBUG during incidents. Mis-levelled logs either hide problems or flood storage—this closes the logging thread from Thursday’s Logback intro.

## The Concept

### What a “log level” is (definition)

A **log level** is a severity/verbosity category attached to each log event. Logback uses levels to decide:

- whether an event should be **recorded** (filtered by logger/appender configuration)
- where it should be **routed** (console, file, etc.)

You choose levels so different audiences can get the right signal:
- developers (debugging)
- operators (production monitoring)
- auditors (security/traceability)

### Levels (typical use)

| Level | When |
|-------|------|
| **TRACE** | Very fine-grained tracing (often off in prod) |
| **DEBUG** | Developer diagnostics, request flow |
| **INFO** | Business-significant events, lifecycle |
| **WARN** | Recoverable oddities, deprecations |
| **ERROR** | Failures needing attention |

**Ordering:** TRACE < DEBUG < INFO < WARN < ERROR. If logger is set to **INFO**, DEBUG/TRACE are **disabled** for that logger (unless separate additive rules—see Logback docs).

### Logger hierarchy

Loggers are named (usually package/class). Child loggers **inherit** from ancestors unless explicitly set. **`root`** logger is the ultimate parent.

```xml
<logger name="com.myapp.api" level="DEBUG"/>
<root level="INFO">
  <appender-ref ref="STDOUT"/>
</root>
```

#### Additivity (why logs sometimes “duplicate”)

By default, Logback logger events are **additive**: a child logger can send events to its own appenders and also to its parents’ appenders (including `root`). If you see duplicates, you may need:

```xml
<logger name="com.myapp.api" level="DEBUG" additivity="false">
  <appender-ref ref="STDOUT"/>
</logger>
```

### Formatting

`<encoder><pattern>...</pattern></encoder>` controls timestamp, thread, level, logger name, message, MDC, exceptions (`%ex`). Use consistent patterns across services for log aggregation (ELK, Splunk, etc.).

Common pattern tokens:

| Token | Meaning |
|------|---------|
| `%d{...}` | timestamp |
| `%thread` | thread name |
| `%-5level` | padded level |
| `%logger{36}` | logger name (truncated) |
| `%msg` | message |
| `%ex` / `%throwable` | exception stacktrace |
| `%X{key}` | MDC value for `key` |
| `%n` | newline |

### Threshold filters

Appenders can attach **filters** to drop events below a level or by marker—useful for splitting security audit logs from general logs.

Example: send INFO+ to console, DEBUG+ to a rolling file.

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="DEBUG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/app-debug.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>logs/app-debug.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>7</maxHistory>
    </rollingPolicy>
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
      <level>DEBUG</level>
    </filter>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n%ex</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="STDOUT"/>
    <appender-ref ref="DEBUG_FILE"/>
  </root>
</configuration>
```

## Code Example

```java
log.trace("Entering method");
log.debug("Query params {}", params);
log.info("Order {} submitted", orderId);
log.warn("Retry {} of {}", attempt, max);
log.error("Payment failed", exception);
```

### Practical guidance: what to log (and what not to)

- Prefer **parameterized** logging (`{}`) over string concatenation; it avoids unnecessary work when the level is disabled.
- Log **once per failure**, and include context (ids, counts, path), plus the exception as the last argument.
- Avoid logging secrets (passwords, tokens, full credit card numbers). Redact or hash sensitive fields.

## Summary

- Choose levels by audience: operators vs developers vs auditors.
- Configure package-level loggers; root catches the default.
- Patterns and filters shape operational usability—align with your observability stack.

## Additional Resources

- [Logback — Chapter on configuration](https://logback.qos.ch/manual/configuration.html)
- [SLF4J — Logging levels](https://www.slf4j.org/faq.html#logging_performance)
