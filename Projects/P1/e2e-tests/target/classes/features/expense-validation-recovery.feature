@SEVERITY=normal
@e2e @employee-e2e @negative @regression @validation-recovery
Feature: Expense validation and recovery
  As an employee
  I want invalid expense submissions to be rejected clearly without creating a record
  So that I can correct my mistake and successfully resubmit

  Background:
    Given I am on the welcome page
    When I log in as an employee with username "brian" and password "password"
    Then I should be on the employee dashboard

  Scenario: Invalid expense submission is rejected without creating a record, then a corrected resubmission succeeds
    When I submit an expense with description "Office chair", amount "150.00", category "Office Supplies", and date "not-a-date"
    Then I should see an expense error "Date must be in YYYY-MM-DD format (e.g., 2026-06-25)."
    When I view my pending expenses
    Then the expense "Office chair" should no longer appear in my pending expenses
    When I return to the dashboard
    And I submit an expense with description "Office chair", amount "150.00", category "Office Supplies", and date "2026-07-28"
    Then I should see a confirmation that the expense was submitted
