"""
Main Program — Product Inventory System
Week 1, Thursday | Pair Programming Exercise

Wire everything together here. Complete each numbered section.
Run with:  python main.py

References:
    written/4-Thursday/lists.md
    written/4-Thursday/tuples.md
    written/4-Thursday/sets.md
    written/4-Thursday/exception-handling-custom-exceptions.md
    written/4-Thursday/try-except.md
"""

from product import Product
from inventory import Inventory
from exceptions import ProductNotFoundError, InsufficientStockError


def section(title: str) -> None:
    print(f"\n{'─' * 55}")
    print(f"  {title}")
    print(f"{'─' * 55}")


def main():
    inv = Inventory()

    # ── 1. Add at least 8 products across 3+ categories ───────────────────
    section("1. Loading Inventory")

    # TODO: Create and add at least 8 Product instances.
    # Use at least 3 different categories (e.g., "electronics", "accessories", "software").
    # Example:
    #   p = Product("Laptop", 999.99, stock=15, category="electronics")
    #   product_id = inv.add_product(p)
    #   print(f"  Added: {p} → ID={product_id}")

    # ── 2. Display all products sorted by price ────────────────────────────
    section("2. All Products (sorted by price)")

    # TODO: Use sorted() with the __lt__ dunder to sort inv.products.values().
    # Print each product using its __str__ representation.

    # ── 3. Search products by keyword ─────────────────────────────────────
    section("3. Search: 'pro'")

    # TODO: Call inv.search("pro") and print the results.
    # This uses the __contains__ dunder on Product.

    # ── 4. Filter by category ─────────────────────────────────────────────
    section("4. Category: 'electronics'")

    # TODO: Call inv.by_category("electronics") and print the results.

    # ── 5. Sell products — one should succeed, one should fail ────────────
    section("5. Sell Operations")

    # TODO: Attempt to sell a quantity that succeeds, then one that exceeds stock.
    # Use try/except to catch InsufficientStockError and print the error details.
    # Access e.requested and e.available from the exception object.

    # ── 6. Access a non-existent product ID ───────────────────────────────
    section("6. Non-Existent Product Lookup")

    # TODO: Try inv.get_product(9999) and catch ProductNotFoundError.

    # ── 7. Transaction history ────────────────────────────────────────────
    section("7. Recent Transaction History")

    # TODO: Print each entry in inv.history.
    # Remember: history is a deque — you can iterate over it directly.

    # ── 8. Inventory summary ──────────────────────────────────────────────
    section("8. Inventory Summary")

    # TODO: Call inv.summary() and print each key-value pair neatly.

    # ── 9. Set operations on categories ───────────────────────────────────
    section("9. Set Operations on Categories")

    my_wishlist = {"electronics", "gaming", "software"}

    # TODO: Use inv.categories (a set) and my_wishlist to show:
    #   - Union:        All categories across both sets
    #   - Intersection: Categories in BOTH my_wishlist and the inventory
    #   - Difference:   Categories in my_wishlist but NOT in the inventory
    # Use the |, &, - operators (ref: written/4-Thursday/sets.md)

    # ── 10. Tuple-based product configurations ────────────────────────────
    section("10. Product Configs as Tuples")

    # TODO: Define at least 3 product configurations as tuples:
    #   configs = [
    #       ("Monitor", 349.99, 8, "electronics"),
    #       ("USB Hub",  24.99, 30, "accessories"),
    #       ...
    #   ]
    # Iterate over configs and add each as a Product to the inventory.
    # Print the updated total using len(inv).
    # This demonstrates tuples as immutable, structured data records.
    # (ref: written/4-Thursday/tuples.md — "Tuples as Fixed Records")


if __name__ == "__main__":
    main()
