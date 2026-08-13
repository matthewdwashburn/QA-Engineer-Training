@SEVERITY=blocker
@e2e @cross-role-e2e @smoke @cross-role-workflow
Feature: Cross-role expense lifecycle
  As an employee and a manager
  I want an expense to move from pending to reviewed and be reflected for both roles
  So that the employee's ledger always matches the manager's decision

  Scenario: Manager's decision on a submitted expense is reflected in the employee's ledger
    Given I am on the welcome page
    When I log in as an employee with username "landon" and password "password"
    Then I should be on the employee dashboard
    When I submit an expense with description "Team lunch", amount "62.40", category "Meals", and date "2026-07-28"
    Then I should see a confirmation that the expense was submitted
    When I view my pending expenses
    Then I should see the expense "Team lunch" with status "Pending" in my pending expenses
    When I log out
    And I log in as a manager with username "siri" and password "password"
    Then I should be on the manager dashboard
    When I view the pending expense queue
    Then I should see the expense "Team lunch" with amount "62.40" in the pending queue
    When I review the expense "Team lunch" as "approved" with comment "Approved, receipt on file"
    Then the expense "Team lunch" should no longer appear in the pending queue
    When I log out
    And I log in as an employee with username "landon" and password "password"
    Then I should be on the employee dashboard
    When I view my expense history
    Then I should see the expense "Team lunch" with status "approved" and comment "Approved, receipt on file" in my expense history
    And the expense "Team lunch" should no longer appear in my pending expenses
