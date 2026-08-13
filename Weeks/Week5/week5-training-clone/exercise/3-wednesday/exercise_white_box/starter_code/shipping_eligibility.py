"""
Exercise artifact — shipping promo eligibility.
Trainees analyze coverage; do not modify this file for the exercise.
"""


def shipping_eligibility(cart_subtotal: float, region: str, is_member: bool) -> bool:
    """
    Returns True if the cart qualifies for free shipping under current promo rules.

    Rules:
    - EU regions: FR, DE, NL always eligible if subtotal >= 25 (membership ignored).
    - US: eligible if subtotal >= 35 OR (is_member AND subtotal >= 50).
    - Other regions: not eligible unless subtotal >= 100.
    """
    r = region.strip().upper()
    if r in ("FR", "DE", "NL"):
        return cart_subtotal >= 25
    if r == "US":
        if cart_subtotal >= 35:
            return True
        if is_member and cart_subtotal >= 50:
            return True
        return False
    return cart_subtotal >= 100
