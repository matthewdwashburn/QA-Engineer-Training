import pytest
from loan_eligibility import assess_loan

def test_happy_path_eligible():
    """All condition False -> ELIGIBLE. One test, many uncovered branches"""
    assert assess_loan(age=30,income=30000,credit_score=700,employed=True) == "ELIGIBLE"

def test_ineligible_age_too_young():
    assert assess_loan(age=16, income=30000, credit_score=700, employed=True) == "INELIGIBLE_AGE"

def test_ineligible_age_too_old():
    assert assess_loan(age=85, income=30000, credit_score=700, employed=True) == "INELIGIBLE_AGE"

def test_ineligible_income_too_little():
    assert assess_loan(age=21, income=20000, credit_score=700, employed=True) == "INELIGIBLE_INCOME"

def test_ineligible_bad_credit(): 
    assert assess_loan(age=21, income=50000, credit_score=400, employed=True) == "INELIGIBLE_CREDIT"

def test_ineligible_not_employed():
    assert assess_loan(age=21, income=30000, credit_score=700, employed=False) == "INELIGIBLE_EMPLOYMENT"


@pytest.mark.parametrize("age, expected",[
    (17, "INELIGIBLE_AGE"),
    (18, "ELIGIBLE"),
    (65, "ELIGIBLE"),
    (66, "INELIGIBLE_AGE"),
])
def test_age_boundary_values(age, expected):
    result = assess_loan(age=age, income=30000, credit_score=700, employed=True)
    assert result == expected, f"age={age}:expected {expected}, got {result}"