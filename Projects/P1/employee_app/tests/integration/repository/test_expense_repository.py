# 4 Expense repository tests: 4 positive
import allure
import pytest

from repository.expense_model import Expense
from repository.expense_repository import ExpenseRepository


pytestmark = [
    pytest.mark.db,
]


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - Repository Layer")
@allure.suite("Expense Repository")
class TestExpenseRepository:

    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Get expenses by user returns sorted joined rows")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_expenses_by_user_returns_sorted_joined_rows(self, temp_db_path):
        # Arrange: Create an instance of the ExpenseRepository with the temporary database path
        repo = ExpenseRepository(temp_db_path)
    
        # Act: Retrieve expenses for the user with ID 1
        rows = repo.get_expenses_by_user(1)
    
        # Assert: Verify that the expenses are sorted by status and contain the expected data
        assert [row.expense_id for row in rows] == [2, 1]
        assert rows[0].status == "approved"
        assert rows[1].status == "pending"
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Find expense with status returns joined row data")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_find_expense_with_status_returns_joined_data(self, temp_db_path):
        # Arrange: Set up the ExpenseRepository
        repo = ExpenseRepository(temp_db_path)
    
        # Act: Use tested method to find the expense with status for the expense ID 2
        row = repo.find_expense_with_status(2)
    
        # Assert: Verify that the returned row contains the expected joined data
        assert row is not None
        assert row.expense_id == 2
        assert row.user_id == 1
        assert row.status == "approved"
        assert row.manager_comment == "Looks good"
        assert row.review_date == "2026-07-04"
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Update expense persists amount and description")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_update_expense_persists_amount_and_description(self, temp_db_path):
        # Arrange: Set up the ExpenseRepository instance
        repo = ExpenseRepository(temp_db_path)
    
        # Act: Use tested method to update the expense with ID 1
        updated = repo.update_expense(1, 111.11, "Updated meal")
    
        # Assert: Verify that the update was successful and the changes are persisted
        assert updated is True
        row = repo.find_expense_with_status(1)
        assert row is not None
        assert row.amount == 111.11
        assert row.description == "Updated meal"
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Delete expense removes approval and expense rows")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_delete_expense_removes_expense_and_approval(self, temp_db_path):
        # Arrange: Set up the ExpenseRepository instance
        repo = ExpenseRepository(temp_db_path)
    
        # Act: Use tested method to remove the expense with ID 1
        deleted = repo.delete_expense(1)
    
        # Assert: Verify deleted returns successfully and the ID is None
        assert deleted is True
        assert repo.find_expense_with_status(1) is None
    
    
    @allure.issue("KAN-17", "KAN-17")
    @allure.story("Submit New Expense")
    @allure.title("Create expense inserts expense and default pending approval")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_create_expense_inserts_expense_and_pending_approval(self, temp_db_path):
        repo = ExpenseRepository(temp_db_path)
    
        expense = Expense(
            id=None,
            user_id=1,
            amount=88.25,
            description="Hotel",
            category="LODGING",
            date="2026-07-07",
        )
    
        created = repo.create_expense(expense)
    
        assert created.id is not None
        joined_row = repo.find_expense_with_status(created.id)
        assert joined_row is not None
        assert joined_row.user_id == 1
        assert joined_row.amount == 88.25
        assert joined_row.description == "Hotel"
        assert joined_row.status.lower() == "pending"
    
    
    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Find by user id returns only that users expenses")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_find_by_user_id_returns_only_target_users_rows(self, temp_db_path):
        repo = ExpenseRepository(temp_db_path)
    
        rows = repo.find_by_user_id(1)
    
        assert len(rows) == 2
        assert {row.id for row in rows} == {1, 2}
        assert {row.user_id for row in rows} == {1}
