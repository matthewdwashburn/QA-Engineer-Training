import logging
import logging.handlers
import os

# Basic logging vs print

print(" [print] Application started") # no level, no timestamp
print(" [print] something went wrong") # no way to filter
print("\n Now with logging")
logging.basicConfig(
    level = logging.DEBUG,
    format = "%(asctime)s |  %(levelname)-8s | %(message)s",
    datefmt = "%H:%M:%S",
    force=True #reset any previous config
)

# Log in stdout
logger = logging.getLogger("customLogger")
# logger.debug("Detailed debug information")
# logger.warning("This is a warning")
# logger.info("Info message")
# logger.error("Error message")
# logger.critical("Critial Message")

# Named loggers with handlers

# Create a Named Logger
logger = logging.getLogger("test_framewor")
logger.setLevel(logging.DEBUG)
logger.handlers.clear() # Clean slate for demo

# Console handler - INFO and above
console = logging.StreamHandler()
console.setLevel(logging.INFO)
console.setFormatter(logging.Formatter(
    " %(levelname)-8s | %(message)s"
))

# File Handler
log_file = "demo_output.log"
file_handler = logging.FileHandler(log_file, mode="w")
file_handler.setLevel(logging.DEBUG)
file_handler.setFormatter(logging.Formatter(
    "%(asctime)s |  %(levelname)-8s | %(name)s:%(lineno)d | %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
))
logger.addHandler(console)
logger.addHandler(file_handler)

# Log to file demo_output.log
logger.debug("Detailed debug information")
logger.warning("This is a warning")
logger.info("Info message")
logger.error("Error message")
logger.critical("Critial Message")

#logging Exceptions
def divide(a,b):
    try:
        return a/b
    except ZeroDivisionError:
        logger.error("Division by zero: %s/%s", a,b)
        return None
divide(10,0)


