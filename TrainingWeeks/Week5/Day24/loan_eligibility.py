"""
Loan assessment function for conditional testing demo

Business rules:
 - Age must be 18-65 inclusive
 - Annual income must be >=25000
 - Credit score must be >=600
 - Applicant must be currently employes

returns a string result indicating eligibility or the first disqualifying condition
encountered (short-circuit evaluation)
"""

# def assess_loan(age, income):
#     if age>= 18 and income >=25000:
#         return "ELIGIBLE"
#     return "INELIGIBLE"

def assess_loan(age: int, income: float, credit_score: int, employed: bool)-> str:
    """Assess a loan application against eligibility criteria.
    
    Returns:
        'ELIGIBLE','INELIGIBLE_AGE','INELIGIBLE_INCOME', 'INELIGIBLE_CREDIT',or 'INELIGIBLE_EMPLOYMENT'
        """
    
    if age <18 or age>65:
        return "INELIGIBLE_AGE"
    if income <25000:
        return "INELIGIBLE_INCOME"
    if credit_score<600:
        return "INELIGIBLE_CREDIT"
    if not employed:
        return "INELIGIBLE_EMPLOYMENT"
    else:
        return "ELIGIBLE"

