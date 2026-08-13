import pytest
from shipping import get_shipping_tier

def test_express_returned_for_large_priority_order():
    assert get_shipping_tier(150,priority=True) == "EXPRESS"

def test_standard_returned_for_large_non_priority_order():
    assert get_shipping_tier(120.00,priority=False) == "STANDARD"

def test_no_free_shipping_returned_for_small_order():
    assert get_shipping_tier(50.00, priority=False) == "NO_FREE_SHIPPING"

# 100 Percent coverage does not imply 100 percent accuracy
# def test_wrong_oracle_still_100_percent():
#     result = get_shipping_tier(150.00, priority=True)
#     assert result == "STANDARD"