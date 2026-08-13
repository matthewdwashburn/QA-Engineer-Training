@SEVERITY=critical
@e2e @employee-e2e @security @negative @route-protection
Feature: Authentication and route protection
  As the system
  I want protected screens to require a valid login and reject the wrong role's credentials
  So that employee and manager data stay properly separated

  Scenario: Unauthenticated access to the employee dashboard redirects to a recoverable sign-in state
    Given I am on the welcome page
    When I navigate directly to the employee dashboard
    Then I should be redirected to the welcome page
    When I log in as an employee with username "brian" and password "password"
    Then I should be on the employee dashboard

  Scenario: Unauthenticated access to the manager dashboard redirects to a recoverable sign-in state
    Given I am on the welcome page
    When I navigate directly to the manager dashboard
    Then I should be redirected to the welcome page
    When I log in as a manager with username "siri" and password "password"
    Then I should be on the manager dashboard

  Scenario: Manager credentials are rejected on the employee login
    Given I am on the welcome page
    When I log in as an employee with username "siri" and password "password"
    Then I should see a login error "Access Denied: Managers must manage tasks and authenticate exclusively through the corporate Job Application portal."
    And I should still be on the login page

  Scenario: Employee credentials are rejected on the manager login
    Given I am on the welcome page
    When I log in as a manager with username "brian" and password "password"
    Then I should see a login error "Invalid username or password. This login portal is for managers only."
    And I should still be on the login page

  Scenario: Wrong password is rejected with a clear error
    Given I am on the welcome page
    When I log in as an employee with username "brian" and password "wrongpassword"
    Then I should see a login error "Invalid username or password."
    And I should still be on the login page
