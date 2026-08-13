# 7 Expense service unit tests: 4 Positive and 3 Negative
import allure
import pytest

from repository.expense_model import Expense
from repository.ledger_entry_model import LedgerEntry
from service.expense_service import ExpenseService

pytestmark = [
    pytest.mark.unit,
]

def _entry(expense_id: int, user_id: int, status: str, amount: float = 10.0) -> LedgerEntry:
    # Helper Function: Creates a LedgerEntry instance with the given parameters and default values for other fields
    return LedgerEntry(
        expense_id=expense_id,
        user_id=user_id,
        amount=amount,
        description="sample",
        category="MEALS",
        expense_date="2026-07-05",
        status=status,
        manager_comment=None,
        review_date=None,
    )


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - Service Layer")
@allure.suite("Expense Service")
class TestExpenseService:

    @allure.issue("KAN-17", "KAN-17")
    @allure.story("Submit New Expense")
    @allure.title("Currency formatter normalizes amount to two decimals")
    @allure.severity(allure.severity_level.MINOR)
    @pytest.mark.edge_case
    def test_format_currency_amount_normalizes_to_two_decimals(self):
        # Act & Assert
        assert ExpenseService.format_currency_amount("46") == "46.00"
        assert ExpenseService.format_currency_amount("46.5") == "46.50"
    
    
    @allure.issue("KAN-17", "KAN-17")
    @allure.story("Submit New Expense")
    @allure.title("Expense creation sends cleaned values to repository")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_create_expense_calls_repo_with_clean_values(self, mocker):
        # Arrange: Mock setup and stubbed repo return value
        repo = mocker.Mock()
        service = ExpenseService(repo)
    
        repo.create_expense.return_value = Expense(
            id=99,
            user_id=1,
            amount=46.5,
            description="Lunch",
            category="MEALS",
            date="2026-07-05",
        )
    
        # Act: Create a new expense using the service
        result = service.create_expense(
            user_id=1,
            amount="46.5",
            description="  Lunch  ",
            category=" meals ",
            expense_date="2026-07-05",
        )
    
        # Assert: Verify created expense has the expected values
        assert result.id == 99
        created_expense = repo.create_expense.call_args.args[0]
        assert created_expense.amount == 46.5
        assert created_expense.description == "Lunch"
        assert created_expense.category == "MEALS"
    
    
    @allure.issue("KAN-17", "KAN-17")
    @allure.story("Submit New Expense")
    @allure.title("Expense creation rejects invalid inputs")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.parametrize("amount, description, category, expense_date, expected_error", [
        # Amount Errors: Format, Non-numeric, Zero, Negative
        ("46.555", "Lunch", "MEALS", "2026-07-05", "at most 2 decimal places"),
        ("abc", "Lunch", "MEALS", "2026-07-05", "valid number"),
        ("0", "Lunch", "MEALS", "2026-07-05", "greater than zero"),
        ("-10.50", "Lunch", "MEALS", "2026-07-05", "greater than zero"),
        
        # Description Errors
        ("46.50", "", "MEALS", "2026-07-05", "A description is required"), 
        ("46.50", "   ", "MEALS", "2026-07-05", "A description is required"),
        
        # Category Errors
        ("46.50", "Lunch", "", "2026-07-05", "A category is required"),
        ("46.50", "Lunch", "   ", "2026-07-05", "A category is required"),
        ("46.50", "Lunch", "SNACKS", "2026-07-05", "Invalid category"),
        
        # Date Errors
        ("46.50", "Lunch", "MEALS", "07-05-2026", "YYYY-MM-DD format"), 
        ("46.50", "Lunch", "MEALS", "today", "YYYY-MM-DD format"),
    ])
    def test_create_expense_rejects_invalid_inputs(self, mocker, amount, description, category, expense_date, expected_error):
        # Arrange: Mock setup
        service = ExpenseService(mocker.Mock())
    
        # Act & Assert: Attempt to create an expense with bad inputs and expect specific ValueErrors
        with pytest.raises(ValueError, match=expected_error):
            service.create_expense(
                user_id=1,
                amount=amount,
                description=description,
                category=category,
                expense_date=expense_date,
            )
    
    
    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Ledger split returns pending and history sections")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_user_ledger_splits_pending_and_history(self, mocker):
        # Arrange: Mock setup, service instance, and stubbed repo return values
        repo = mocker.Mock()
        service = ExpenseService(repo)
    
        repo.get_expenses_by_user.return_value = [
            _entry(expense_id=3, user_id=1, status="Approved", amount=50),
            _entry(expense_id=2, user_id=1, status="pending", amount=10),
            _entry(expense_id=1, user_id=1, status="Denied", amount=25),
        ]
    
        # Act: Call the method under test
        ledger = service.get_user_ledger(user_id=1)
    
        # Assert: Verify the ledger splits pending and history correctly
        assert len(ledger["pending_expenses"]) == 1
        assert len(ledger["expense_history"]) == 2
        assert ledger["pending_expenses"][0]["expense_id"] == 2
    
    
    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Ledger split returns empty lists when no expenses")
    @allure.severity(allure.severity_level.MINOR)
    @pytest.mark.edge_case
    def test_get_user_ledger_returns_empty_lists_for_no_expenses(self, mocker):
        # Arrange: Mock setup, service instance, and stubbed repo return value
        repo = mocker.Mock()
        service = ExpenseService(repo)
    
        repo.get_expenses_by_user.return_value = []
    
        # Act: Call the method under test
        ledger = service.get_user_ledger(user_id=1)
    
        # Assert: Verify the ledger returns empty lists for both pending and history
        assert ledger["pending_expenses"] == []
        assert ledger["expense_history"] == []
    
    
    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Get pending expenses returns only pending items for user")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_get_pending_expenses_filters_non_pending_items(self, mocker):
        # Arrange: Mock setup and service instance
        repo = mocker.Mock()
        service = ExpenseService(repo)
    
        # Mock database returning a mix of pending and final statuses
        repo.get_expenses_by_user.return_value = [
            _entry(expense_id=1, user_id=1, status="PENDING", amount=15.0),
            _entry(expense_id=2, user_id=1, status="Approved", amount=50.0),
            _entry(expense_id=3, user_id=1, status="pending", amount=25.0),
            _entry(expense_id=4, user_id=1, status="Denied", amount=10.0),
        ]
    
        # Act: Call the method under test
        pending_list = service.get_pending_expenses(user_id=1)
    
        # Assert: Verify only the two pending expenses were extracted and serialized
        assert len(pending_list) == 2
        assert pending_list[0]["expense_id"] == 1
        assert pending_list[1]["expense_id"] == 3
        
        # Ensure repository was called with the exact user ID
        repo.get_expenses_by_user.assert_called_once_with(1)
    
    
    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Pending expenses returns empty list when none exist")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.edge_case
    def test_get_pending_expenses_returns_empty_list(self, mocker):
        repo = mocker.Mock()
        service = ExpenseService(repo)
    
        repo.get_expenses_by_user.return_value = []
    
        result = service.get_pending_expenses(user_id=1)
    
        assert result == []
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Update allows owned pending expense")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_update_pending_expense_updates_owned_pending(self, mocker):
        # Arrange: Mock setup, service instance and stubbed repo return values
        repo = mocker.Mock()
        service = ExpenseService(repo)
        
        repo.find_expense_with_status.return_value = _entry(
            expense_id=10,
            user_id=1,
            status="Pending",
            amount=10,
        )
        repo.update_expense.return_value = True
    
        # Act: Update the pending expense owned by the user
        service.update_pending_expense(
            user_id=1,
            expense_id=10,
            amount="12.50",
            description="Updated",
        )
    
        # Assert: Behavior Verification - ensure the update_expense method was called with the correct parameters
        repo.update_expense.assert_called_once_with(
            expense_id=10,
            amount=12.5,
            description="Updated",
        )
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Update rejects invalid inputs, ownership, and states")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.parametrize("amount, description, mock_expense, mock_update_result, expected_error", [
        # 1. Field Validation Errors (Fails before hitting the database)
        ("abc", "Updated", _entry(10, 1, "Pending", 10), True, "valid number"),
        (-5, "Updated", _entry(10, 1, "Pending", 10), True, "greater than zero"),
        ("12.555", "Updated", _entry(10, 1, "Pending", 10), True, "at most 2 decimal places"),
        ("12.50", "", _entry(10, 1, "Pending", 10), True, "A description is required"),
        ("12.50", "   ", _entry(10, 1, "Pending", 10), True, "A description is required"),
        
        # 2. State & Permission Errors (Fails based on current database state)
        ("12.50", "Updated", None, True, "Expense not found"),
        ("12.50", "Updated", _entry(10, 2, "Pending", 10), True, "only edit your own"),
        ("12.50", "Updated", _entry(10, 1, "Approved", 10), True, "Only pending expenses"),
        
        # 3. Database Failure (Fails during the actual update execution)
        ("12.50", "Updated", _entry(10, 1, "Pending", 10), False, "update failed"),
    ])
    def test_update_pending_expense_rejects_invalid_states(self, mocker, amount, description, mock_expense, mock_update_result, expected_error):
        # Arrange: Mock setup
        repo = mocker.Mock()
        service = ExpenseService(repo)
        
        # Stub the repository returns based on the specific parameterized scenario
        repo.find_expense_with_status.return_value = mock_expense
        repo.update_expense.return_value = mock_update_result
    
        # Act & Assert: Attempt to update and expect the specific ValueError
        with pytest.raises(ValueError, match=expected_error):
            service.update_pending_expense(
                user_id=1,
                expense_id=10,
                amount=amount,
                description=description,
            )
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Delete rejects invalid states and permissions")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.parametrize("mock_expense, mock_delete_result, expected_error", [
        # Scenario 1: The expense does not exist in the database
        (None, True, "Expense not found."),
        # Scenario 2: Security check - User 1 is trying to delete an expense owned by User 2
        (_entry(expense_id=10, user_id=2, status="Pending", amount=10), True, "delete your own expenses"),
        # Scenario 3: Status check - The expense is owned by User 1, but is already Approved
        (_entry(expense_id=10, user_id=1, status="Approved", amount=10), True, "Only pending expenses"),
        # Scenario 4: Database failure - The repository fails to delete the record
        (_entry(expense_id=10, user_id=1, status="Pending", amount=10), False, "delete failed"),
    ])
    def test_delete_pending_expense_rejects_invalid_states(self, mocker, mock_expense, mock_delete_result, expected_error):
        # Arrange: Mock setup and service instance
        repo = mocker.Mock()
        service = ExpenseService(repo)
        
        # Stub the repository returns based on the parameterized scenario
        repo.find_expense_with_status.return_value = mock_expense
        repo.delete_expense.return_value = mock_delete_result
    
        # Act & Assert: User 1 attempts to delete expense 10 and expects a specific ValueError
        with pytest.raises(ValueError, match=expected_error):
            service.delete_pending_expense(user_id=1, expense_id=10)
