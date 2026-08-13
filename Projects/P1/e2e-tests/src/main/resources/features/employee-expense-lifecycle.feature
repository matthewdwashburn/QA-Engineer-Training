@SEVERITY=critical
@e2e @employee-e2e @smoke @employee-workflow
Feature: Employee expense lifecycle
  As an employee
  I want to submit, edit, and delete my own expenses
  So that I can manage my reimbursement requests before a manager reviews them

  Background:
    Given I am on the welcome page
    When I log in as an employee with username "brian" and password "password"
    Then I should be on the employee dashboard

  Scenario: Employee submits an expense, confirms it is pending, edits it, then deletes it
    When I submit an expense with description "Client dinner", amount "125.50", category "Meals", and date "2026-07-28"
    Then I should see a confirmation that the expense was submitted
    When I view my pending expenses
    Then I should see the expense "Client dinner" with status "Pending" in my pending expenses
    When I view my expense history
    Then I should not see the expense "Client dinner" in my expense history
    When I view my pending expenses
    And I edit the expense "Client dinner" to description "Client dinner - team offsite" and amount "150.00"
    Then I should see the expense "Client dinner - team offsite" with amount "150.00" in my pending expenses
    When I delete the expense "Client dinner - team offsite"
    Then the expense "Client dinner - team offsite" should no longer appear in my pending expenses
