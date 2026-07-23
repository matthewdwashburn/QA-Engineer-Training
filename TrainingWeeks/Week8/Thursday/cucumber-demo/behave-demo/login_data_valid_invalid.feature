Feature: User Authentication
    As a registered User
    I want to log into my account
    So that I can access my personalized dashboard

    #Background runs before EVERY scenario in this feature
    #Use for common preconditions
    Background:
        Given the application is running
        And the test database is seeded with users

    #Basic scenario with Given/When/Then
    Scenario Outline: Successful login with valid credentials
        #Given - sets up the preconditions
        Given the user is on the login page

        #When - Describes the action being tested
        When the user enters username: "<username>"
        And the user enters password: "<password>"
        And the user clicks the login button

        #Then - describes the expected outcome
        Then the "<expected_result>" should be displayed
        And the user should be on the "<expected_page>" page

        #Valid credentials
        Examples: Valid Credentials
        | username | password            | expected_result | expected_page |
        | tomsmith | SuperSecretPassword | success message | secure |

        #Invalid credentials
        Examples: Invalid Credentials
        | username      | password            | expected_result | expected_page |
        | wrongusername | SuperSecretPassword | error message   | login         |
        | tomsmith      | wrongpassword       | error message   | login         |
        | wrongusername | wrongpassword       | error message   | login         |
        |               | SuperSecretPassword | error message   | login         |
        | tomsmith      |                     | error message   | login         |
        