# Pytest-Mock Basics
# pytest-mock provides the "mocker" fixture
# automatically cleans up patches after each test
# No context managers or decorators needed
# Same API as unittest.mock but more Pythonic
# Install with: pip install pytest-mock

import pytest
import os
from services import UserService, User, UserRepository, EmailClient

#basic mock creation
def test_mocker_creates_mocks(mocker):
    """
    The mocker fixture creates mock objects.

    mocker.Mock() creates a basic mock.
    mocker.MagicMock() creates a mock with magic methods.
    """
    #Creates basic mock
    mock_func = mocker.Mock()
    mock_func.return_value = 42

    result = mock_func()

    assert result == 42
    mock_func.assert_called_once()

def test_mocker_mock_with_spec(mocker):
    """
    Use spec to ensure mock has same interface as real object.

    Catches typos in method names!
    """
    mock_repo = mocker.Mock(spec=UserRepository)

    #This works -find_by_id exists on UserRepository
    mock_repo.find_by_id.return_value = User(1,"John","john@test.com")

    #This would reaise AttributeError if uncommented:
    # mock_repo.find_by_idd.return_value = None #Typo!

    user = mock_repo.find_by_id(1)
    assert user.name == "John"

# mocker.patch() - patching made easy

def test_mocker_patch(mocker):
    """
    mocker.patch() replaces objects during test
    automatically cleaned up after test
    """
    #Patch os.path.exists to always return True
    mock_exists = mocker.patch('os.path.exists',return_value=True)

    assert os.path.exists('/any/path/at/all') is True
    assert os.path.exists('/fake/path') is True

    mock_exists.assert_called()

def test_mocker_patch_dict(mocker):
    """
    mocker.patch.dict() patches dictionary contents.

    great for environmental variables!
    """
    mocker.patch.dict(os.environ, {
        'API_KEY':'test-key-123',
        'DEBUG':'true'
    })

    assert os.environ['API_KEY'] == 'test-key-123'
    assert os.environ['DEBUG'] == 'true'

def test_mocker_patch_object(mocker):
    """
    mocker.patch.object() patches a specific method on an object.
    """
    user = User(1,"original","original@test.com")

    #Patch just the email property
    mocker.patch.object(user, 'email','patched@test.com')

    assert user.email == 'patched@test.com'
    assert user.name == "original" #Not patched

#testing with mocked dependencies

def test_user_service_with_mock_repo(mocker):
    """
    Create UserService with mocked repository.
    """

    #Create the mock repository
    mock_repo = mocker.Mock(spec=UserRepository)
    mock_repo.find_by_id.return_value = User(1,"John","john@test.com")

    #Create service with mock
    service = UserService(repository=mock_repo)

    #Test
    user = service.get_user(1)

    assert user.name == "John"
    mock_repo.find_by_id.assert_called_once_with(1)

def test_user_service_create_user(mocker):
    """
    Test user creation with mocked repo and email client
    """

    #Create mocks
    mock_repo = mocker.Mock(spec=UserRepository)
    mock_email = mocker.Mock(spec=EmailClient)

    #Setup Behavior
    mock_repo.find_by_email.return_value = None # no existing user
    mock_repo.save.side_effect = lambda u: User(id=100, name = u.name, email = u.email)

    mock_email.send.return_value = True

    #Create service and test
    service = UserService(repository=mock_repo, email_client=mock_email)
    user = service.create_user("Alice", "alice@test.com")

    #Verify
    assert user.id == 100
    assert user.name == "Alice"
    mock_repo.save.assert_called_once()
    mock_email.send.assert_called_once()

#mocker.spy() - Track real function calls
def test_mocker_spy(mocker):

    """
    Spy wraps a real function to track calls
    The function still executes, but you can verify it was called
    """
    #Create a real list

    #Arrange
    #Create mocks
    mock_repo = mocker.Mock(spec=UserRepository)
    mock_email = mocker.Mock(spec=EmailClient)

    service = UserService(mock_repo, mock_email)

    user = User(id=1,name="Alice",email="alice@example.com")

    mock_repo.find_by_id.return_value = user
    mock_repo.save.return_value = user

    #spy on the REAL get_user method
    spy = mocker.spy(service, "get_user")

    #Act
    result = service.deactivate_user(1)

    #Assert
    #the real method executed
    assert result.active is False

    # spy recorded the call
    spy.assert_called_once_with(1)

    mock_repo.save.assert_called_once_with(user)
    mock_email.send.assert_called_once()

# Combine mocker with Pytest Fixtures
@pytest.fixture
def mock_repository(mocker):
    """ Fixture that provides a configured mock repository"""
    mock = mocker.Mock(spec=UserRepository)
    mock.find_by_id.return_value = User(1,"Test User", "test@test.com")
    mock.find_by_email.return_value = None
    mock.save.side_effect = lambda u: User(id=u.id, name = u.name, email=u.email)
    return mock

@pytest.fixture
def mock_email_client(mocker):
    """Fixture that provides a mock email client."""
    mock = mocker.Mock(spec=EmailClient)
    mock.send.return_value = True
    return mock

@pytest.fixture
def user_service(mock_repository, mock_email_client):
    """UserService with all dependencies mocked."""
    return UserService(repository=mock_repository,
                       email_client= mock_email_client)

def test_with_fixtures(user_service, mock_repository):
    "Use the fixture-provided service and mocks."
    user = user_service.get_user(1)

    assert user.name == "Test User"
    mock_repository.find_by_id.assert_called_once_with(1)

def test_create_user_with_fixtures(user_service, mock_email_client):
    """Test user creation with fixtures"""
    user = user_service.create_user("New User","new@test.com")
    assert user.name == "New User"
    mock_email_client.send.assert_called_once()



    

