@SEVERITY=critical
@e2e @manager-e2e @smoke @manager-workflow
Feature: Manager review lifecycle
  As a manager
  I want to view the pending expense queue and approve or deny expenses
  So that I can process my team's reimbursement requests

  Background:
    Given I am on the welcome page
    When I log in as a manager with username "siri" and password "password"
    Then I should be on the manager dashboard

  Scenario Outline: Manager reviews a pending expense
    Given an employee has a pending expense "<description>" for "<amount>" in category "<category>"
    When I view the pending expense queue
    Then I should see the expense "<description>" with amount "<amount>" in the pending queue
    When I review the expense "<description>" as "<decision>" with comment "<comment>"
    Then I should see a confirmation that the review was submitted
    And the expense "<description>" should no longer appear in the pending queue

    Examples:
      | description                | amount | category | decision | comment                             |
      | Conference travel          | 450.00 | Travel   | approved | Approved, within budget             |
      | Personal software license  | 89.99  | Software | denied   | Not a reimbursable business expense |
