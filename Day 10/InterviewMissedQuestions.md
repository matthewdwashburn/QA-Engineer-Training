## Abstraction in OOP

```
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

## Custom Exception
```
class ValueTooLowError(Exception):
    """Raised when the input value is too low."""
    pass

# Usage
raise ValueTooLowError("The provided value must be greater than 10.")
```

```
class APIError(Exception):
    def __init__(self, message, status_code, payload=None):
        super().__init__(message)  # Passes message to base Exception
        self.status_code = status_code
        self.payload = payload

# Usage
raise APIError("Unauthenticated request", status_code=401, payload={"user_id": 42})
```

## Pandas Dataframe
A Pandas DataFrame is a 2 dimensional data structure, like a 2 dimensional array, or a table with rows and columns.
```
import pandas as pd

# Define your data in a dictionary
data = {
  "calories": [420, 380, 390],
  "duration": [50, 40, 45]
}

#load data into a DataFrame object:
df = pd.DataFrame(data)

print(df) 

```

### Output
     calories  duration
  0       420        50
  1       380        40
  2       390        45