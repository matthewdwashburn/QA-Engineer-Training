"""
Business rule:
    - Orders over $100 with priority flag -> EXPRESS SHIPPING
    - Orders over $100 without priority flag -> STANDARD SHIPPING
    - Orders $100 or under -> NO_FREE_SHIPPING (customer pays)
"""

def get_shipping_tier (order_total: float, priority: bool) -> str:
    """Return the shipping teir for an order.
    
    Args:
        order_total: Total order value in dollars
        priority: True if the customer has a priority membership

        Returns:
            'EXPRESS', 'STANDARD', or 'NO_FREE_SHIPPING'
    
    """
    if order_total > 100:
        if priority:
            return "EXPRESS"
        return "STANDARD"
    return "NO_FREE_SHIPPING"