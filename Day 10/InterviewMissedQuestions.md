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
```
import pandas as pd

# Define your data in a dictionary
data = {
    "Product": ["Laptop", "Mouse", "Monitor"],
    "Price": [999, 25, 150],
    "In_Stock": [True, True, False]
}

# Load the dictionary into a DataFrame object
df = pd.DataFrame(data)

print(df)
```