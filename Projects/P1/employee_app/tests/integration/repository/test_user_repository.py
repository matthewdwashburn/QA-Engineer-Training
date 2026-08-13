# 5 User repository tests: 3 positive and 2 negative

import allure
import pytest

from repository.user_model import User
from repository.user_repository import UserRepository


pytestmark = [
    pytest.mark.db,
]



@allure.epic("Employee Portal Backend")
@allure.feature("Authentication")
@allure.parent_suite("Employee - Repository Layer")
@allure.suite("User Repository")
@allure.issue("KAN-16", "KAN-16")
@allure.story("Employee Login and Authentication")
class TestUserRepository:


    @allure.title("Find by username returns existing user")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.smoke
    def test_find_by_username_returns_user(self, temp_db_path):
        # Arrange: Create an instance of UserRepository with temporary database
        repo = UserRepository(temp_db_path)
    
        # Act: Retrieve user by username
        user = repo.find_by_username("alice")
    
        # Assert: Verify returned user data
        assert user is not None
        assert user.id == 1
        assert user.username == "alice"
        assert user.password == "alicepass"
        assert user.role == "Employee"
    
    
    @allure.title("Find by username returns none for missing user")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    def test_find_by_username_returns_none_when_user_missing(self, temp_db_path):
        # Arrange: Create an instance of UserRepository
        repo = UserRepository(temp_db_path)
    
        # Act: Retrieve user by non-existent username
        user = repo.find_by_username("unknown")
    
        # Assert : Verify returned user is none
        assert user is None
    
    
    @allure.title("Find by id returns existing user")
    @allure.severity(allure.severity_level.MINOR)
    @pytest.mark.smoke
    def test_find_by_id_returns_user(self, temp_db_path):
        # Arrange: Create an instance of UserRepository
        repo = UserRepository(temp_db_path)
    
        # Act : Retrieve user by ID
        user = repo.find_by_id(1)
    
        # Assert : Verify returned user
        assert user is not None
        assert user.id == 1
        assert user.username == "alice"
        assert user.role == "Employee"
    
    
    @allure.title("Find by id returns none for missing user")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    def test_find_by_id_returns_none_when_missing(self, temp_db_path):
        # Arrange: Create an instance of UserRepository
        repo = UserRepository(temp_db_path)
    
        # Act: Retrieves user with non-existent ID
        user = repo.find_by_id(999)
    
        # Assert: Verify returned user is None
        assert user is None
    
    
    @allure.title("Create user inserts record and assigns id")
    @allure.severity(allure.severity_level.MINOR)
    @pytest.mark.smoke
    def test_create_user_inserts_user_and_assigns_id(self, temp_db_path):
        # Arrange
        repo = UserRepository(temp_db_path)
    
        user = User(
            id=None,
            username="newuser",
            password="hashed_password",
            role="Employee",
        )
    
        # Act
        created_user = repo.create_user(user)
    
        # Assert
        assert created_user.id is not None
        assert created_user.username == "newuser"
        assert created_user.role == "Employee"
    
        # Verify persistence
        saved_user = repo.find_by_username("newuser")
    
        assert saved_user is not None
        assert saved_user.username == "newuser"
        assert saved_user.password == "hashed_password"
