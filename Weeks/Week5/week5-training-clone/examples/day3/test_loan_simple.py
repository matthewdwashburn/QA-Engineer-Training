from loan_simple import assess_loan

def test_adult():
    assert assess_loan(30) == "DONE"

#Comment this out to demonstrate missing branch coverage
def test_minor():
    assert assess_loan(16) == "DONE"

# pytest test_loan_simple.py --cov=loan_simple --cov-report=term-missing --cov-branch