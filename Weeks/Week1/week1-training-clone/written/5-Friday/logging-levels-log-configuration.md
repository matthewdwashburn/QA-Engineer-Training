# Logging Levels and Configuration

## Learning Objectives
- Configure logging levels, handlers, and formatters.
- Set up logging to multiple destinations (console + file).
- Use configuration files and dictionaries for logging setup.

---

## Why This Matters

> **Weekly Epic Connection:** The previous reading introduced *why* to log. This reading covers *how* to configure logging for real projects — routing different severity levels to different destinations, formatting output for readability, and using configuration files for maintainability.

---

## The Concept

### The Five Logging Levels

```python
import logging

# Levels in order of severity (lowest to highest)
logging.DEBUG      # 10 — Detailed diagnostic information
logging.INFO       # 20 — Confirmation of expected behavior
logging.WARNING    # 30 — Something unexpected (default level)
logging.ERROR      # 40 — An operation failed
logging.CRITICAL   # 50 — Program may not continue
```

Setting the level determines the **minimum severity** that gets logged:

```python
logging.basicConfig(level=logging.DEBUG)    # Show everything
logging.basicConfig(level=logging.WARNING)  # Show WARNING and above only
```

### Handlers — Where Logs Go

Handlers route log messages to destinations:

```python
import logging

logger = logging.getLogger("my_app")
logger.setLevel(logging.DEBUG)

# Console handler — shows INFO and above
console_handler = logging.StreamHandler()
console_handler.setLevel(logging.INFO)

# File handler — captures DEBUG and above
file_handler = logging.FileHandler("app.log")
file_handler.setLevel(logging.DEBUG)

logger.addHandler(console_handler)
logger.addHandler(file_handler)
```

Common handler types:

| Handler | Destination |
|---------|-------------|
| `StreamHandler` | Console (stdout/stderr) |
| `FileHandler` | A file on disk |
| `RotatingFileHandler` | File with size-based rotation |
| `TimedRotatingFileHandler` | File with time-based rotation |
| `SMTPHandler` | Email |
| `SocketHandler` | Network socket |

### Formatters — How Logs Look

```python
# Create a formatter
formatter = logging.Formatter(
    "%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
)

# Apply formatter to handlers
console_handler.setFormatter(formatter)
file_handler.setFormatter(formatter)
```

Output:
```
2026-04-05 09:30:15 | INFO     | my_app | Application started
2026-04-05 09:30:16 | ERROR    | my_app | Database connection failed
```

**Available format fields:**

| Field | Output |
|-------|--------|
| `%(asctime)s` | Timestamp |
| `%(levelname)s` | Level name (DEBUG, INFO, etc.) |
| `%(name)s` | Logger name |
| `%(message)s` | The log message |
| `%(filename)s` | Source filename |
| `%(lineno)d` | Line number |
| `%(funcName)s` | Function name |
| `%(module)s` | Module name |

### Complete Configuration Example

```python
import logging
from logging.handlers import RotatingFileHandler

def setup_logging():
    """Configure logging for the application."""
    logger = logging.getLogger("test_framework")
    logger.setLevel(logging.DEBUG)

    # Console: INFO and above, concise format
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    console.setFormatter(logging.Formatter(
        "%(levelname)-8s | %(message)s"
    ))

    # File: DEBUG and above, detailed format, rotates at 5MB
    file_handler = RotatingFileHandler(
        "test_framework.log",
        maxBytes=5_000_000,
        backupCount=3
    )
    file_handler.setLevel(logging.DEBUG)
    file_handler.setFormatter(logging.Formatter(
        "%(asctime)s | %(levelname)-8s | %(name)s:%(lineno)d | %(message)s"
    ))

    logger.addHandler(console)
    logger.addHandler(file_handler)

    return logger
```

### Dictionary-Based Configuration

For larger projects, use a dictionary:

```python
import logging.config

LOGGING_CONFIG = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "standard": {
            "format": "%(asctime)s | %(levelname)-8s | %(name)s | %(message)s"
        }
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "level": "INFO",
            "formatter": "standard"
        },
        "file": {
            "class": "logging.FileHandler",
            "filename": "app.log",
            "level": "DEBUG",
            "formatter": "standard"
        }
    },
    "loggers": {
        "": {  # Root logger
            "handlers": ["console", "file"],
            "level": "DEBUG"
        }
    }
}

logging.config.dictConfig(LOGGING_CONFIG)
logger = logging.getLogger(__name__)
```

### Best Practices

1. **One `getLogger(__name__)` per module** — creates a hierarchy matching your package structure.
2. **Configure logging once** at the entry point (main script), not in library modules.
3. **Use `%s` formatting**, not f-strings, in log calls — avoids formatting cost if the message won't be logged:
   ```python
   # ✅ Lazy formatting (string only built if level is enabled)
   logger.debug("Processing user %s with %d items", user_id, count)
   
   # ❌ Always formats the string, even if DEBUG is disabled
   logger.debug(f"Processing user {user_id} with {count} items")
   ```
4. **Use `exc_info=True`** to include tracebacks:
   ```python
   except Exception as e:
       logger.error("Operation failed", exc_info=True)
   ```
5. **For library code — use `NullHandler`:** Libraries should never configure logging themselves. Add only a `NullHandler` so library users can configure the output however they want:
   ```python
   import logging
   
   # In your library's __init__.py
   logging.getLogger("mylib").addHandler(logging.NullHandler())
   # Now the library logs go nowhere unless the application configures a handler
   ```
6. **Check `isEnabledFor()` before expensive log arguments:**
   ```python
   # When building the log message is itself expensive
   if logger.isEnabledFor(logging.DEBUG):
       details = expensive_to_compute(data)   # Only computed if DEBUG is active
       logger.debug("Detailed state: %s", details)
   ```

---

## Summary

- **Levels:** DEBUG < INFO < WARNING < ERROR < CRITICAL — set the threshold for what gets logged.
- **Handlers** route logs to destinations (console, file, email, network).
- **Formatters** control the output format (timestamp, level, module, line number).
- Use **RotatingFileHandler** to prevent log files from growing forever.
- Configure once at the application entry point; use `getLogger(__name__)` in modules.
- Use `%s`-style formatting in log calls for performance.

---

## Additional Resources
- [Python Docs — Logging Cookbook](https://docs.python.org/3/howto/logging-cookbook.html)
- [Python Docs — logging.config](https://docs.python.org/3/library/logging.config.html)
- [Real Python — Logging in Python (Advanced)](https://realpython.com/python-logging/)
