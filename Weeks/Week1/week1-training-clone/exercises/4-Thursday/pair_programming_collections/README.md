# Pair Programming: Collections, Dunder Methods & Exception Handling

**Mode:** Collaborative (Pair Programming — Driver/Navigator)
**Duration:** 3–4 hours
**Day:** Thursday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

This is a **collaborative pair programming exercise** that synthesizes the entire week's Python topics. You and your partner will build a **Product Inventory System** that uses collections, dunder methods, comprehensions, and custom exception handling.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Lists | `written/4-Thursday/lists.md` |
| Tuples | `written/4-Thursday/tuples.md` |
| Sets | `written/4-Thursday/sets.md` |
| Deque | `written/4-Thursday/deque-list.md` |
| Iterators | `written/4-Thursday/iterators-iterables.md` |
| Comprehensions | `written/4-Thursday/comprehension.md` |
| Dunder Methods | `written/4-Thursday/dunder-methods.md` |
| Exceptions | `written/4-Thursday/exception-handling-custom-exceptions.md` |
| Try-Except | `written/4-Thursday/try-except.md` |
| All prior material | Days 1–3 |

---

## Pair Programming Rules

| Role | Responsibility |
|------|---------------|
| **Driver** | Types the code. Focuses on syntax and implementation details. |
| **Navigator** | Directs strategy. Reviews each line as it's written. Thinks about edge cases. |

- **Swap roles every 25 minutes** (use a timer).
- Both partners must understand every line of code.
- Use **one computer** — the navigator does NOT type.

---

## The Project: Product Inventory System

Build a system with 3 classes, 2 custom exceptions, and a main program that ties everything together.

---

## Round 1: Partner A Drives — Build the Core Classes (50 min)

### Step 1: Custom Exceptions

Create `starter_code/exceptions.py`:

```python
class InventoryError(Exception):
    """Base exception for inventory system."""
    pass

class ProductNotFoundError(InventoryError):
    """Raised when a product lookup fails."""
    def __init__(self, product_id):
        self.product_id = product_id
        super().__init__(f"Product not found: {product_id}")

class InsufficientStockError(InventoryError):
    """Raised when stock is too low for an operation."""
    def __init__(self, product_name, requested, available):
        self.product_name = product_name
        self.requested = requested
        self.available = available
        super().__init__(
            f"Cannot fulfill {requested} units of '{product_name}'. "
            f"Only {available} in stock."
        )
```

### Step 2: Product Class with Dunder Methods

Create `starter_code/product.py`:

```python
class Product:
    """A product in the inventory.

    Must implement these dunder methods:
        __str__     — "Laptop ($999.99) — 15 in stock"
        __repr__    — "Product('Laptop', 999.99, stock=15, category='electronics')"
        __eq__      — Equal if same name AND category
        __lt__      — Compare by price (enables sorting)
        __hash__    — Based on name + category (enables use in sets)
        __bool__    — True if in stock (stock > 0)
        __contains__ — Check if substring in product name

    Class attributes:
        total_products (int): Count of all Product instances
    """
    pass  # TODO: Implement
```

**Validate with these tests:**
```python
p1 = Product("Laptop", 999.99, stock=15, category="electronics")
p2 = Product("Laptop", 1099.99, stock=5, category="electronics")
p3 = Product("Mouse", 29.99, stock=50, category="electronics")

print(p1)                    # Laptop ($999.99) — 15 in stock
print(repr(p1))              # Product('Laptop', 999.99, stock=15, category='electronics')
print(p1 == p2)              # True (same name + category)
print(p1 < p3)               # False (999.99 > 29.99)
print(sorted([p1, p3]))      # Sorted by price
print(bool(p1))              # True (in stock)
print("laptop" in p1)        # True (case-insensitive search)
print({p1, p2})              # Set with ONE item (they're equal)
```

---

## Round 2: Partner B Drives — Build the Inventory (50 min)

### Step 3: Inventory Class

Create `starter_code/inventory.py`:

```python
from collections import deque

class Inventory:
    """A collection of products with search, filter, and transaction capabilities.

    Features:
        - Add/remove products
        - Search by name or category
        - Transaction history (deque with maxlen=50)
        - Restock and sell operations with exception handling
    """

    def __init__(self):
        self.products = {}          # {product_id: Product}
        self.categories = set()     # Unique categories
        self.history = deque(maxlen=50)  # Recent transactions
        self._next_id = 1

    def add_product(self, product):
        """Add a product to inventory. Return the assigned ID."""
        pass  # TODO

    def remove_product(self, product_id):
        """Remove a product. Raise ProductNotFoundError if missing."""
        pass  # TODO

    def get_product(self, product_id):
        """Get a product by ID. Raise ProductNotFoundError if missing."""
        pass  # TODO

    def sell(self, product_id, quantity):
        """Sell units of a product.
        Raise ProductNotFoundError if ID doesn't exist.
        Raise InsufficientStockError if not enough stock.
        Record transaction in history.
        """
        pass  # TODO

    def restock(self, product_id, quantity):
        """Add stock. Raise ProductNotFoundError if missing."""
        pass  # TODO

    # --- Comprehension-powered queries ---

    def search(self, keyword):
        """Return products containing keyword (case-insensitive).
        Use a list comprehension and the __contains__ dunder.
        """
        pass  # TODO

    def by_category(self, category):
        """Return products in a category. Use a list comprehension."""
        pass  # TODO

    def in_stock(self):
        """Return products with stock > 0. Use __bool__ dunder + filter."""
        pass  # TODO

    def price_range(self, min_price, max_price):
        """Return products in the price range. Use a list comprehension."""
        pass  # TODO

    def summary(self):
        """Return a dict with:
        - total_products
        - total_value (sum of price * stock for each product)
        - categories (sorted list)
        - out_of_stock_count
        Use dict/list comprehensions.
        """
        pass  # TODO
```

---

## Round 3: Swap Again — Integration & Error Handling (50 min)

### Step 4: Main Program

Create `starter_code/main.py`:

```python
"""
Product Inventory System — Main Program
Demonstrates the full system with exception handling.
"""

from product import Product
from inventory import Inventory
from exceptions import ProductNotFoundError, InsufficientStockError


def main():
    inv = Inventory()

    # 1. Add at least 8 products across 3+ categories
    # TODO

    # 2. Display all products (sorted by price)
    # TODO: Use sorted() with the __lt__ dunder

    # 3. Search for products containing "pro"
    # TODO: Use inv.search()

    # 4. Show products in a specific category
    # TODO

    # 5. Sell products — include at least one that fails
    try:
        inv.sell(1, 3)   # Should succeed
        inv.sell(1, 999) # Should raise InsufficientStockError
    except InsufficientStockError as e:
        print(f"❌ {e}")
        print(f"   Requested: {e.requested}, Available: {e.available}")

    # 6. Try to access a product that doesn't exist
    try:
        inv.get_product(999)
    except ProductNotFoundError as e:
        print(f"❌ {e}")

    # 7. Show transaction history
    # TODO: Print recent entries from inv.history

    # 8. Show inventory summary (using comprehension-powered summary())
    # TODO

    # 9. Use set operations on categories
    # TODO: Show union, intersection with another set

    # 10. Use a tuple to store immutable product configurations
    # TODO: Create product configs as tuples, iterate over them


if __name__ == "__main__":
    main()
```

---

## Round 4: Both Together — Review & Polish (30 min)

1. **Code Review:** Both partners review all files together.
2. **Test edge cases:**
   - Sell exactly all remaining stock (stock → 0).
   - Remove a product and try to sell it.
   - Add duplicate products (same name, same category) — what happens?
3. **Add at least 2 more tests** for each custom exception.
4. **Commit to Git** with meaningful messages for each file.

---

## Deliverable

A Git repository with the following structure:
```
product-inventory/
├── starter_code/
│   ├── product.py
│   ├── inventory.py
│   ├── exceptions.py
│   └── main.py
└── README.md (this file)
```

---

## Definition of Done

- [ ] Both partners have driven AND navigated.
- [ ] `Product` class implements all 7 dunder methods listed.
- [ ] `Inventory` uses dict, set, deque, list/dict comprehensions.
- [ ] At least 2 custom exceptions are raised and caught.
- [ ] `main.py` demonstrates the full workflow with error handling.
- [ ] `git log` shows commits from both partners (or paired commits).
- [ ] All scripts run without errors.
