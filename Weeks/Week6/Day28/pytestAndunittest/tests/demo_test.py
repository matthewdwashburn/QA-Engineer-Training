import pytest
def test_shared_calculator_add(shared_calculator):
    assert shared_calculator.add(10,5) == 15

def test_sample_users(sample_users):
    """Use shared user data"""
    assert len(sample_users) == 3
    assert sample_users[0]["name"]=="Alice"
    assert sample_users[2]["age"]==35