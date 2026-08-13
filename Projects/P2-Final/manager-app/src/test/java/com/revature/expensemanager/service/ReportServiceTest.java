// Report service tests, organized by report feature (Employee / Category / Date)
package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.expensemanager.allure.ParentSuite;
import com.revature.expensemanager.dao.ReportDAO;
import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.model.User;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

/**
 * Unit coverage for {@link ReportService} against mocked DAOs.
 *
 * <p>Scope is delegation and pass-through only: does the service hand the DAO
 * what it was given, return what the DAO returned, and let a DAO failure
 * propagate rather than swallowing it. Request parsing and status mapping moved
 * to {@code ReportControllerTest}; the SQL itself is {@code JdbcReportDAOTest}'s.
 */
@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Reporting")
@ParentSuite("Manager - Service Layer")
@Tag("unit")
@DisplayName("Reporting Service")
class ReportServiceTest {

    @Mock
    private ReportDAO reportDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private ReportService reportService;

    @Nested
    @Issue("KAN-23")
    @Story("Manager Reports")
    @DisplayName("Report by Employee")
    class ReportByEmployee {

        @Test
        @Tag("smoke")
        @DisplayName("A valid employee ID returns the employee's expenses.")
        void getExpensesByEmployee_returnsListFromDao() {

            // Arrange: Initialize employee, create employee-owned expenses, and stub DAO behavior
            int employeeId = 101;
            List<Expense> expenses = List.of(
                    new Expense(1, employeeId, 50.00, "Taxi", "Travel", "2026-07-01"),
                    new Expense(2, employeeId, 20.00, "Coffee", "Meals", "2026-07-02"));
            when(reportDAO.findExpensesByEmployee(employeeId)).thenReturn(expenses);

            // Act: Invoke getExpensesByEmployee
            List<Expense> result = reportService.getExpensesByEmployee(employeeId);

            // Assert: Verify all expenses exist, data is integral, and the reportDAO was executed
            assertEquals(2, result.size());
            assertEquals("Taxi", result.get(0).getDescription());
            verify(reportDAO).findExpensesByEmployee(employeeId);
        }

        @Test
        @Tag("edge_case")
        @DisplayName("An existing employee with no expenses returns an empty result.")
        void getExpensesByEmployee_returnsEmpty_whenNoExpenses() {

            // Arrange: initialize employee with valid ID and no expense records.
            int employeeId = 101;
            when(reportDAO.findExpensesByEmployee(employeeId)).thenReturn(List.of());

            // Act: get empty list from stub
            List<Expense> result = reportService.getExpensesByEmployee(employeeId);

            // Assert: ensure list isn't null; and that it's empty
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reportDAO).findExpensesByEmployee(employeeId);
        }

        @Test
        @Tag("edge_case")
        @DisplayName("The employee id is passed unchanged to the report DAO")
        void getExpensesByEmployee_passesIdToDao() {

            // Arrange: valid Id; stub returns anything
            int employeeId = 101;
            when(reportDAO.findExpensesByEmployee(employeeId)).thenReturn(List.of());

            // Act:  propagate ID to service
            reportService.getExpensesByEmployee(employeeId);

            // Assert: verify delegation, capture user id to verify
            ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(reportDAO).findExpensesByEmployee(idCaptor.capture());
            assertEquals(employeeId, idCaptor.getValue());
        }

        @Test
        @Tag("negative")
        @DisplayName("A nonexistent employee is rejected.")
        void employeeExists_returnsFalse_whenUserNotFound() {

            // Arrange: non-existent id returns empty list
            int userId = 999;
            when(userDAO.findById(userId)).thenReturn(Optional.empty());

            // Act: false when non-existent employee is rejected
            boolean result = reportService.employeeExists(userId);

            // Assert: ensure result is false and confirm delegation
            assertFalse(result);
            verify(userDAO).findById(userId);
        }

        @Test
        @Tag("negative")
        @DisplayName("A nonexistent employee is rejected. (user role is not employee)")
        void employeeExists_returnsFalse_whenRoleNotEmployee() {

            // Arrange: user with manager role
            int employeeId = 101;
            User employee = new User(employeeId, "manager1", "hashed", "manager");
            when(userDAO.findById(employeeId)).thenReturn(Optional.of(employee));

            // Act: Invoke employeeExists()
            boolean result = reportService.employeeExists(employeeId);

            assertFalse(result);
            verify(userDAO).findById(employeeId);
        }

        @Test
        @Tag("smoke")
        @DisplayName("Employee existence is checked before retrieving the report")
        void employeeExists_returnsTrue_whenUserRoleIsEmployee() {

            // Arrange: Initialize user for employee and stub DAO behavior
            int employeeId = 101;
            User employee = new User(employeeId, "employee1", "hashed", "employee");
            when(userDAO.findById(employeeId)).thenReturn(Optional.of(employee));

            // Act: Invoke employeeExists on created employee
            boolean result = reportService.employeeExists(employeeId);

            // Assert: Verify returned True
            assertTrue(result);
        }

        @Test
        @Tag("negative")
        @DisplayName("DAO failure is handled or propagated.")
        void getExpensesByEmployee_propagatesException_whenDaoFails() {

            // Arrange: exception thrown when Dao is queried
            int id = 101;
            when(reportDAO.findExpensesByEmployee(id)).thenThrow(new RuntimeException("database unavailable"));

            // Act + Assert: service lets the exception propagate
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> reportService.getExpensesByEmployee(id));

            // Assert: verify exception propagation and method delegation
            assertEquals("database unavailable", thrown.getMessage());
            verify(reportDAO).findExpensesByEmployee(id);
        }
    }

    @Nested
    @Issue("KAN-23")
    @Story("Manager Reports")
    @DisplayName("Report by Category")
    class ReportByCategory {

        @Test
        @Tag("smoke")
        @DisplayName("A valid category returns matching expenses.")
        void getExpensesByCategory_returnsMatching() {

            // Arrange: valid category, stub returns list with expenses
            String category = "Travel";
            List<Expense> expenses = List.of(new Expense(101, 10, "bus ticket", "Travel", "2026-05-01"),
                    new Expense(101, 50, "weekend fare", "Travel", "2026-05-10"));
            when(reportDAO.findExpensesByCategory(category)).thenReturn(expenses);

            // Act: retrieve expenses with category
            List<Expense> result = reportService.getExpensesByCategory(category);

            // Assert: result is not null, is the right size, and the correct expenses are returned.
            assertEquals(2, result.size());
            assertEquals("bus ticket", result.get(0).getDescription());
            assertEquals("weekend fare", result.get(1).getDescription());

            verify(reportDAO).findExpensesByCategory(category);
        }

        @Test
        @Tag("edge_case")
        @DisplayName("A valid category with no matching expenses returns an empty result.")
        void getExpensesByCategory_returnsEmpty_whenNoMatches() {

            // Arrange: empty list when no category match is made
            String category = "rowboating";
            when(reportDAO.findExpensesByCategory(category)).thenReturn(List.of());

            // Act: get empty list
            List<Expense> result = reportService.getExpensesByCategory(category);

            // Assert: ensure result isn't null and is empty after no match is made
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(reportDAO).findExpensesByCategory(category);
        }

        @Test
        @Tag("negative")
        @DisplayName("DAO failure is handled or propagated.")
        void getExpensesByCategory_propagatesException_whenDaoFails() {

            // Arrange: stub throws runtime exception
            String category = "Travel";
            when(reportDAO.findExpensesByCategory(category)).thenThrow(new RuntimeException("database unavailable"));

            // Act + Assert: propagate runtime exception from dao -> service
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> reportService.getExpensesByCategory(category));

            assertEquals("database unavailable", thrown.getMessage());

            verify(reportDAO).findExpensesByCategory(category);
        }
    }

    @Nested
    @Issue("KAN-23")
    @Story("Manager Reports")
    @DisplayName("Report by Date")
    class ReportByDate {

        @Test
        @Tag("smoke")
        @DisplayName("A valid start and end date return matching expenses.")
        void getExpensesByDateRange_returnsMatching() {

            // Arrange: valid start and end date
            String start = "2026-07-01";
            String end = "2026-07-10";

            List<Expense> expenses = List.of(
                    new Expense(1, 100, "plane ticket", "Travel", "2026-07-05"),
                    new Expense(1, 10, "McDonalds", "Food", "2026-07-08"));

            when(reportDAO.findExpensesByDateRange(start, end)).thenReturn(expenses);

            // Act: retrieve dates from range start - end
            List<Expense> result = reportService.getExpensesByDateRange(start, end);

            // Assert: not null, size of 2, and fields unaltered
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Travel", result.get(0).getCategory());
            assertEquals("Food", result.get(1).getCategory());

            verify(reportDAO).findExpensesByDateRange(start, end);
        }

        @Test
        @Tag("edge_case")
        @DisplayName("A valid range with no matching expenses returns an empty result.")
        void getExpensesByDateRange_returnsEmpty_whenNoMatches() {

            // Arrange: valid range that matches no expenses
            String start = "2026-07-01";
            String end = "2026-07-10";
            when(reportDAO.findExpensesByDateRange(start, end)).thenReturn(List.of());

            // Act: retrieve expenses for the range
            List<Expense> result = reportService.getExpensesByDateRange(start, end);

            // Assert: not null and empty; confirm delegation
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reportDAO).findExpensesByDateRange(start, end);
        }

        @Test
        @Tag("negative")
        @DisplayName("DAO failure is handled or propagated.")
        void getExpensesByDateRange_propagatesException_whenDaoFails() {

            // Arrange: stub throws runtime exception
            String start = "2026-07-01";
            String end = "2026-07-10";
            when(reportDAO.findExpensesByDateRange(start, end))
                    .thenThrow(new RuntimeException("database unavailable"));

            // Act + Assert: propagate runtime exception from dao -> service
            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> reportService.getExpensesByDateRange(start, end));

            assertEquals("database unavailable", thrown.getMessage());
            verify(reportDAO).findExpensesByDateRange(start, end);
        }
    }
}
