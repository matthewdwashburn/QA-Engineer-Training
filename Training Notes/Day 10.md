# Day 10 - Python: OOP, Custom Exceptions & Pandas

---

## Abstraction in OOP

Hiding complex implementation details and exposing only the essential interface. Use Python's `abc` module to define abstract base classes.

```python
from abc import ABC, abstractmethod

class CoffeeMachine(ABC):  # Abstract Class
    @abstractmethod
    def brew_coffee(self):
        pass

class EspressoMachine(CoffeeMachine):
    def brew_coffee(self):
        # The user just calls this; the complex heating logic stays inside
        return "Boiling water, grinding beans, forcing steam..."

my_pot = EspressoMachine()
print(my_pot.brew_coffee())
```

---

## Custom Exceptions

Extend the built-in `Exception` class to create domain-specific errors.

**Simple custom exception:**
```python
class ValueTooLowError(Exception):
    """Raised when the input value is too low."""
    pass

# Usage
raise ValueTooLowError("The provided value must be greater than 10.")
```

**Custom exception with extra fields:**
```python
class APIError(Exception):
    def __init__(self, message, status_code, payload=None):
        super().__init__(message)  # Passes message to base Exception
        self.status_code = status_code
        self.payload = payload

# Usage
raise APIError("Unauthenticated request", status_code=401, payload={"user_id": 42})
```

---

## Pandas DataFrame

A Pandas DataFrame is a 2-dimensional data structure — like a table with rows and columns.

```python
import pandas as pd

# Define your data in a dictionary
data = {
    "calories": [420, 380, 390],
    "duration": [50, 40, 45]
}

# Load data into a DataFrame object
df = pd.DataFrame(data)

print(df)
```

### Output
```
   calories  duration
0       420        50
1       380        40
2       390        45
```
