"""
Inventory Class — Product Inventory System
Week 1, Thursday | Pair Programming Exercise

Implement the Inventory class using lists, sets, deque,
and comprehension-powered query methods.

References:
    written/4-Thursday/lists.md
    written/4-Thursday/tuples.md
    written/4-Thursday/sets.md
    written/4-Thursday/deque-list.md
    written/4-Thursday/comprehension.md
"""

from collections import deque

from exceptions import ProductNotFoundError, InsufficientStockError
from product import Product


class Inventory:
    """A collection of products with search, filter, and transaction tracking.

    Internal Data Structures:
        products (dict):      {product_id (int): Product}
        categories (set):     Unique category strings — auto-maintained
        history (deque):      Recent transactions, maxlen=50
        _next_id (int):       Auto-incrementing product ID counter

    Key Constraints:
        - sell() must raise InsufficientStockError if stock < quantity
        - get_product() and remove_product() must raise ProductNotFoundError if ID missing
        - All query methods (search, by_category, in_stock, price_range) use comprehensions
    """

    def __init__(self):
        self.products: dict[int, Product] = {}
        self.categories: set[str] = set()
        self.history: deque[str] = deque(maxlen=50)
        self._next_id: int = 1

    # ── CRUD ─────────────────────────────────────────────────────────────────

    def add_product(self, product: Product) -> int:
        """Add a product to the inventory and return its assigned ID.

        Steps:
            1. Assign self._next_id as the product's ID (store on the product or as dict key).
            2. Add the product to self.products with that ID as key.
            3. Add the product's category to self.categories (set handles uniqueness).
            4. Append a transaction string to self.history: "ADD: {product.name} (ID={id})"
            5. Increment self._next_id.
            6. Return the assigned ID.
        """
        pass  # TODO

    def remove_product(self, product_id: int) -> Product:
        """Remove a product by ID and return it.

        Raises:
            ProductNotFoundError: If product_id is not in self.products.

        Steps:
            1. Raise ProductNotFoundError if ID not found.
            2. Pop the product from self.products.
            3. Append to history: "REMOVE: {product.name} (ID={product_id})"
            4. Return the removed product.
        """
        pass  # TODO

    def get_product(self, product_id: int) -> Product:
        """Retrieve a product by ID without removing it.

        Raises:
            ProductNotFoundError: If product_id is not in self.products.
        """
        pass  # TODO

    def sell(self, product_id: int, quantity: int) -> None:
        """Sell units of a product, reducing its stock.

        Raises:
            ProductNotFoundError:   If product_id is not in self.products.
            InsufficientStockError: If product.stock < quantity.

        Steps:
            1. get_product() — raises ProductNotFoundError if missing.
            2. Raise InsufficientStockError if stock < quantity.
            3. Reduce product.stock by quantity.
            4. Append to history: "SELL: {quantity}x {product.name} (ID={product_id})"
        """
        pass  # TODO

    def restock(self, product_id: int, quantity: int) -> None:
        """Add stock to an existing product.

        Raises:
            ProductNotFoundError: If product_id is not in self.products.

        Steps:
            1. get_product() — raises ProductNotFoundError if missing.
            2. Increase product.stock by quantity.
            3. Append to history: "RESTOCK: +{quantity} {product.name} (ID={product_id})"
        """
        pass  # TODO

    # ── Comprehension-Powered Queries ─────────────────────────────────────────

    def search(self, keyword: str) -> list[Product]:
        """Return products whose name contains keyword (case-insensitive).

        Hint: Use __contains__ dunder on Product — "keyword" in product
        Use a list comprehension over self.products.values().
        """
        pass  # TODO

    def by_category(self, category: str) -> list[Product]:
        """Return all products in the given category (case-insensitive).

        Use a list comprehension. Compare category.lower() to product.category.lower().
        """
        pass  # TODO

    def in_stock(self) -> list[Product]:
        """Return all products with stock > 0.

        Hint: Use __bool__ dunder on Product — bool(product) is True if in stock.
        Use a list comprehension with the bool() check.
        """
        pass  # TODO

    def price_range(self, min_price: float, max_price: float) -> list[Product]:
        """Return products priced between min_price and max_price (inclusive).

        Use a list comprehension.
        """
        pass  # TODO

    def summary(self) -> dict:
        """Return a summary dictionary of the inventory.

        Returns:
            {
                "total_products":   int — number of products,
                "total_value":      float — sum of (price * stock) for each product,
                "categories":       list[str] — sorted list of unique category names,
                "out_of_stock_count": int — products where stock == 0,
            }

        Use dict/list/generator comprehensions — avoid raw for loops.
        """
        pass  # TODO

    def __len__(self) -> int:
        """Return the number of products in the inventory."""
        return len(self.products)

    def __repr__(self) -> str:
        return f"Inventory({len(self)} products, categories={sorted(self.categories)})"
