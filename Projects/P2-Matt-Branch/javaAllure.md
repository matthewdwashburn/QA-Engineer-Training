Add Epic - "Manager Portal Backend"
Add Parent Suite - "Repository Layer" OR "Service Layer" OR "Controller Layer"
Add Suite - "Authentication Service" OR "Expense Service" OR "Report Service"
Add Feature - "Authentication" OR "Expense Approvals" OR "Reporting"
Add Issue:Story - 
"KAN-20" : "Manager Login"
"KAN-21" : "Manager View Queue"
"KAN-22" : "Manager Approve Deny"
"KAN-23" : "Manager Reports"

Layer & Scope Markers
(Class-Level):
@Tag("unit"): Validates business logic in isolation using mocks. Apply this to your Service layer test classes.  
@Tag("db"): Tests database operations against temporary/test databases. Apply this to your Repository/DAO layer test classes.  
@Tag("api"): Verifies endpoint functionality, status codes, and validation. Apply this to your Controller layer test classes.  
@Tag("e2e"): Simulates complete user workflows. Apply this to your comprehensive Selenium/Cucumber tests. 

Behavioral Markers 
(Method-Level):
@Tag("smoke"): Critical happy-path workflows. Apply to tests that prove the core functions work (e.g., successful manager login, successful approval).  
@Tag("negative"): Validation failures and error handling paths. Apply to tests expecting exceptions, empty results, or rejected inputs (e.g., logging in with wrong credentials, denying an expense). 
@Tag("edge_case"): Boundary and minor behavior checks. Apply to boundary testing (e.g., filtering reports by exact start/end dates).  
@Tag("security"): JWT, token, and role-based authorization tests. Apply to middleware tests or tests ensuring an employee cannot access manager routes.  