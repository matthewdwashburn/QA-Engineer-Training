Creating the pytest environment to run respective suites by command (e.g. unit, integration, etc.)
    Layer markers = @pytest.mark.
        unit: Validate business logic in isolation using mocks[cite: 5, 6].
        db: Simulate db transactions using a temp_db for repository actions.
        api: Verify endpoint functionality, status codes, and validation.
        e2e: Simulate complete user workflows through the UI.
    Behavioral markers: @pytest.mark.
        @pytest.mark.smoke / @pytest.mark.happy_path
        @pytest.mark.negative / @pytest.mark.error_handling
        @pytest.mark.edge_case
        @pytest.mark.regression
CHECK**

More Markers: @allure.severity_level ->
.BLOCKER: 
    If this fails, the app is unusable (login fails, database connection fails)
.CRITICAL: 
    If this fails, a core business feature is broken (cannot submit or view expenses).
.NORMAL: 
    Standard failure (a specific validation rule fails to trigger)
.MINOR: 
    Low impact failures (obscure edge cases)
CHECK**
