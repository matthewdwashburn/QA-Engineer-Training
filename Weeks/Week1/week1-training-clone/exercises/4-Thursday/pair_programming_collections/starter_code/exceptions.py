"""
Custom Exceptions — Product Inventory System
Week 1, Thursday | Pair Programming Exercise

Define your custom exception hierarchy here.
All inventory exceptions should inherit from InventoryError.
"""


class InventoryError(Exception):
    """Base exception for all inventory-related errors.

    Catch this if you want to handle any inventory error regardless of type.
    """
    pass


class ProductNotFoundError(InventoryError):
    """Raised when a product lookup by ID fails.

    Attributes:
        product_id: The ID that was searched for.
    """

    def __init__(self, product_id):
        self.product_id = product_id
        super().__init__(f"Product not found: ID={product_id}")


class InsufficientStockError(InventoryError):
    """Raised when a sell operation requests more units than are available.

    Attributes:
        product_name: Name of the product.
        requested:    Number of units requested.
        available:    Number of units currently in stock.
    """

    def __init__(self, product_name, requested, available):
        self.product_name = product_name
        self.requested = requested
        self.available = available
        super().__init__(
            f"Cannot fulfill {requested} units of '{product_name}'. "
            f"Only {available} in stock."
        )
