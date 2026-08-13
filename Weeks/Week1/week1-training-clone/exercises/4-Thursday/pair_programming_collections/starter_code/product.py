"""
Product Class — Product Inventory System
Week 1, Thursday | Pair Programming Exercise

Implement the Product class with the required dunder methods.
See the README for the full specification and validation tests.

Reference: written/4-Thursday/dunder-methods.md
"""


class Product:
    """A single product in the inventory.

    Args:
        name (str):     Product name.
        price (float):  Unit price.
        stock (int):    Units available. Default 0.
        category (str): Product category. Default 'general'.

    Required Dunder Methods:
        __str__      — Human-friendly:  "Laptop ($999.99) — 15 in stock"
        __repr__     — Dev-friendly:    "Product('Laptop', 999.99, stock=15, category='electronics')"
        __eq__       — Equal if same name AND category (case-insensitive)
        __hash__     — Based on (name.lower(), category.lower()) — enables use in sets/dicts
        __lt__       — Compare by price (enables sorted())
        __bool__     — True if stock > 0
        __contains__ — True if substring found in product name (case-insensitive)

    Class Attributes:
        total_products (int): Running count of all Product instances ever created.
    """

    total_products = 0  # Class attribute — shared across all instances

    def __init__(self, name: str, price: float, stock: int = 0, category: str = "general"):
        # TODO: Store all parameters as instance attributes.
        # TODO: Increment Product.total_products by 1.
        pass

    def __str__(self) -> str:
        # TODO: Return "Laptop ($999.99) — 15 in stock"
        pass

    def __repr__(self) -> str:
        # TODO: Return "Product('Laptop', 999.99, stock=15, category='electronics')"
        pass

    def __eq__(self, other) -> bool:
        # TODO: Two products are equal if name AND category match (case-insensitive).
        # Hint: check isinstance(other, Product) first.
        pass

    def __hash__(self) -> int:
        # TODO: Return hash((self.name.lower(), self.category.lower()))
        # Required because we defined __eq__ — Python removes __hash__ by default.
        pass

    def __lt__(self, other) -> bool:
        # TODO: Compare by price. Enables sorted(list_of_products).
        pass

    def __bool__(self) -> bool:
        # TODO: Return True if stock > 0, False if out of stock.
        pass

    def __contains__(self, item: str) -> bool:
        # TODO: Return True if item (string) is a substring of self.name (case-insensitive).
        # Enables: "laptop" in product
        pass


# ── Validation ───────────────────────────────────────────────────────────────
# Run this file directly to check your implementation:
#   python product.py

if __name__ == "__main__":
    p1 = Product("Laptop", 999.99, stock=15, category="electronics")
    p2 = Product("Laptop", 1099.99, stock=5, category="electronics")
    p3 = Product("Mouse", 29.99, stock=50, category="electronics")
    p4 = Product("Notebook", 5.99, stock=0, category="stationery")

    print("=== __str__ ===")
    print(p1)                           # Laptop ($999.99) — 15 in stock

    print("\n=== __repr__ ===")
    print(repr(p1))                     # Product('Laptop', 999.99, stock=15, category='electronics')

    print("\n=== __eq__ and __hash__ ===")
    print(f"p1 == p2: {p1 == p2}")     # True (same name + category)
    print(f"p1 == p3: {p1 == p3}")     # False (different name)
    print(f"Set: {len({p1, p2})}")     # 1 — p1 and p2 are equal, so only one in set

    print("\n=== __lt__ (sorted) ===")
    products = [p1, p3, p2]
    print(sorted(products))             # Sorted by price: Mouse, Laptop x2

    print("\n=== __bool__ ===")
    print(f"p1 in stock: {bool(p1)}")  # True
    print(f"p4 in stock: {bool(p4)}")  # False

    print("\n=== __contains__ ===")
    print(f"'laptop' in p1: {'laptop' in p1}")   # True
    print(f"'pro' in p1:    {'pro' in p1}")       # False

    print("\n=== Class attribute ===")
    print(f"Total products created: {Product.total_products}")  # 4
