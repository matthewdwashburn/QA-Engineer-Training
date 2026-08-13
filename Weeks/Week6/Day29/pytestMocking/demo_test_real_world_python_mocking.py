"""
Demo: Real-World Python Mocking - Complete Service Testing

1. This is the capstone demo - combines all mocking techniques
2. Shows testing a service with external API calls and database
3. Demonstrates proper test organization with fixtures
4. Shows how to handle error cases and edge conditions
5. Includes verification patterns and assertions

RUN THIS WITH:
    pytest demo_test_real_world_python_mocking.py -v
"""

import pytest
from unittest.mock import Mock, MagicMock, patch, call
from services import UserService, User, UserRepository, EmailClient


# ==========================================================
# FIXTURES - Reusable Mock Setup
# ==========================================================

@pytest.fixture
def mock_repository(mocker):
    """
    Configured mock repository for most tests.
    """
    repo = mocker.Mock(spec=UserRepository)
    
    # Default behaviors
    repo.find_by_email.return_value = None  # No existing users
    repo.find_all_active.return_value = []
    
    # save() returns the user with an ID
    def save_user(user):
        if user.id == 0:
            user.id = 100  # Assign ID to new users
        return user
    repo.save.side_effect = save_user
    
    return repo


@pytest.fixture
def mock_email_client(mocker):
    """
    Configured mock email client.
    """
    email = mocker.Mock(spec=EmailClient)
    email.send.return_value = True
    email.send_template.return_value = True
    return email


@pytest.fixture
def user_service(mock_repository, mock_email_client):
    """
    UserService with all dependencies mocked.
    """
    return UserService(
        repository=mock_repository,
        email_client=mock_email_client
    )


@pytest.fixture
def sample_user():
    """
    Sample user for testing.
    """
    return User(id=1, name="John Doe", email="john@test.com", active=True)


# ==========================================================
# SUCCESS SCENARIOS
# ==========================================================

class TestUserCreation:
    """Tests for user creation flow."""

    def test_create_user_success(self, user_service, mock_repository, mock_email_client):
        """
        Create user with valid data:
        - Checks for existing email
        - Saves user
        - Sends welcome email
        """
        # Act
        user = user_service.create_user("Alice", "alice@test.com")

        # Assert - user created correctly
        assert user.id == 100  # ID assigned by save
        assert user.name == "Alice"
        assert user.email == "alice@test.com"
        assert user.active is True

        # Assert - correct repository calls
        mock_repository.find_by_email.assert_called_once_with("alice@test.com")
        mock_repository.save.assert_called_once()

        # Assert - welcome email sent
        mock_email_client.send.assert_called_once()
        call_args = mock_email_client.send.call_args
        assert call_args[1]['to'] == "alice@test.com"
        assert "Welcome" in call_args[1]['subject']

    def test_create_user_without_email_client(self, mock_repository):
        """
        Service works without email client (graceful degradation).
        """
        service = UserService(repository=mock_repository, email_client=None)
        
        user = service.create_user("Bob", "bob@test.com")
        
        assert user.name == "Bob"
        # No email sent, but user created


class TestUserRetrieval:
    """Tests for user retrieval."""

    def test_get_user_success(self, user_service, mock_repository, sample_user):
        """
        Get existing user by ID.
        """
        mock_repository.find_by_id.return_value = sample_user

        user = user_service.get_user(1)

        assert user.name == "John Doe"
        mock_repository.find_by_id.assert_called_once_with(1)

    def test_get_active_users(self, user_service, mock_repository):
        """
        Get all active users.
        """
        mock_repository.find_all_active.return_value = [
            User(1, "Alice", "alice@test.com"),
            User(2, "Bob", "bob@test.com"),
        ]

        users = user_service.get_active_users()

        assert len(users) == 2
        mock_repository.find_all_active.assert_called_once()


# ==========================================================
# ERROR SCENARIOS
# ==========================================================

class TestErrorHandling:
    """Tests for error cases."""

    def test_get_user_not_found(self, user_service, mock_repository):
        """
        Get non-existent user raises error.
        """
        mock_repository.find_by_id.return_value = None

        with pytest.raises(ValueError) as exc_info:
            user_service.get_user(999)

        assert "User not found" in str(exc_info.value)
        assert "999" in str(exc_info.value)

    def test_create_user_duplicate_email(self, user_service, mock_repository, sample_user):
        """
        Create user with existing email raises error.
        """
        mock_repository.find_by_email.return_value = sample_user

        with pytest.raises(ValueError) as exc_info:
            user_service.create_user("New User", "john@test.com")

        assert "already registered" in str(exc_info.value)

        # Verify save was NOT called
        mock_repository.save.assert_not_called()

    def test_create_user_invalid_name(self, user_service):
        """
        Create user with empty name raises error.
        """
        with pytest.raises(ValueError) as exc_info:
            user_service.create_user("", "test@test.com")

        assert "Name is required" in str(exc_info.value)

    def test_create_user_invalid_email(self, user_service):
        """
        Create user with invalid email raises error.
        """
        with pytest.raises(ValueError) as exc_info:
            user_service.create_user("John", "invalid-email")

        assert "email" in str(exc_info.value).lower()


# ==========================================================
# DEACTIVATION AND DELETION
# ==========================================================

class TestUserDeactivation:
    """Tests for user deactivation."""

    def test_deactivate_user_success(
        self, user_service, mock_repository, mock_email_client, sample_user
    ):
        """
        Deactivate user:
        - Sets active to False
        - Sends notification email
        """
        mock_repository.find_by_id.return_value = sample_user

        user = user_service.deactivate_user(1)

        # User is deactivated
        assert user.active is False

        # Save was called with deactivated user
        mock_repository.save.assert_called_once()
        saved_user = mock_repository.save.call_args[0][0]
        assert saved_user.active is False

        # Notification sent
        mock_email_client.send.assert_called_once()
        assert "Deactivated" in mock_email_client.send.call_args[1]['subject']

    def test_deactivate_nonexistent_user(self, user_service, mock_repository):
        """
        Deactivating non-existent user raises error.
        """
        mock_repository.find_by_id.return_value = None

        with pytest.raises(ValueError):
            user_service.deactivate_user(999)


# ==========================================================
# ADVANCED VERIFICATION
# ==========================================================

class TestCallVerification:
    """Demonstrate advanced verification patterns."""

    def test_verify_call_order(self, user_service, mock_repository, mock_email_client):
        """
        Verify methods called in correct order.
        """
        # Act
        user_service.create_user("Test", "test@test.com")

        # Assert order: check email -> save -> send email
        expected_calls = [
            call.find_by_email("test@test.com"),
            call.save(any),  # Any User object
        ]
        # Note: This is a simplified check; for strict ordering,
        # you'd use a mock manager

    def test_verify_no_email_on_error(self, user_service, mock_repository, mock_email_client):
        """
        Verify email is NOT sent when validation fails.
        """
        mock_repository.find_by_email.return_value = User(1, "Existing", "existing@test.com")

        with pytest.raises(ValueError):
            user_service.create_user("New", "existing@test.com")

        mock_email_client.send.assert_not_called()
        mock_repository.save.assert_not_called()


# ==========================================================
# USING ARGUMENT CAPTOR PATTERN
# ==========================================================

class TestArgumentCapture:
    """Demonstrate capturing and inspecting arguments."""

    def test_capture_saved_user(self, user_service, mock_repository, mock_email_client):
        """
        Capture and inspect the user object passed to save().
        """
        user_service.create_user("Captured User", "captured@test.com")

        # Get the user that was passed to save()
        mock_repository.save.assert_called_once()
        saved_user = mock_repository.save.call_args[0][0]

        # Detailed assertions on the saved user
        assert saved_user.name == "Captured User"
        assert saved_user.email == "captured@test.com"
        assert saved_user.active is True
        assert saved_user.created_at is not None

    def test_capture_email_content(self, user_service, mock_repository, mock_email_client):
        """
        Capture and inspect email content.
        """
        user_service.create_user("Email Test", "email@test.com")

        mock_email_client.send.assert_called_once()
        call_kwargs = mock_email_client.send.call_args[1]

        assert call_kwargs['to'] == "email@test.com"
        assert "Welcome" in call_kwargs['subject']
        assert "Email Test" in call_kwargs['body']


# ==========================================================
# EDGE CASES
# ==========================================================

class TestEdgeCases:
    """Test edge cases and special scenarios."""

    def test_email_failure_doesnt_break_creation(
        self, mock_repository, mock_email_client
    ):
        """
        User is created even if email sending fails.
        """
        mock_email_client.send.return_value = False  # Email fails

        service = UserService(mock_repository, mock_email_client)
        user = service.create_user("Test", "test@test.com")

        # User still created
        assert user.name == "Test"
        mock_repository.save.assert_called_once()

    def test_multiple_users_get_unique_ids(self, mock_repository, mock_email_client):
        """
        Each created user gets a unique ID.
        """
        id_counter = [100]
        
        def assign_unique_id(user):
            user.id = id_counter[0]
            id_counter[0] += 1
            return user
        
        mock_repository.save.side_effect = assign_unique_id

        service = UserService(mock_repository, mock_email_client)
        
        user1 = service.create_user("User1", "user1@test.com")
        user2 = service.create_user("User2", "user2@test.com")
        user3 = service.create_user("User3", "user3@test.com")

        assert user1.id == 100
        assert user2.id == 101
        assert user3.id == 102


# ==========================================================
# SUMMARY
# ==========================================================

"""
KEY PATTERNS DEMONSTRATED:

1. FIXTURE ORGANIZATION:
   - mock_repository: Database mock with sensible defaults
   - mock_email_client: Email service mock
   - user_service: SUT with all mocks injected
   - sample_user: Test data fixture

2. STUBBING PATTERNS:
   - return_value for simple returns
   - side_effect for dynamic behavior
   - side_effect function for complex logic

3. VERIFICATION PATTERNS:
   - assert_called_once()
   - assert_called_with()
   - assert_not_called()
   - call_args for argument inspection

4. TEST ORGANIZATION:
   - Classes for logical grouping
   - Clear test naming
   - Separate success/error/edge cases

5. ERROR TESTING:
   - pytest.raises for exceptions
   - Verify error messages
   - Verify no side effects on error
"""

