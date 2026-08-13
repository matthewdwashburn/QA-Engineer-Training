package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.model.Approval;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Expense Approvals")
@ParentSuite("Manager - Service Layer")
@DisplayName("Expense Service")
@Tag("unit")
class ExpenseServiceTest {

    @Mock
    private ExpenseDAO expenseDAO;

    @Mock
    private ApprovalDAO approvalDAO;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    @Issue("KAN-21")
    @Story("Manager View Queue")
    @Tag("smoke")
    void getPendingExpenses_returnsListFromDao() {
        // Arrange: Create 2 mock pending expenses stored as a list and stub expenseDAO
        List<Expense> pendingExpenses = List.of(
                new Expense(1, 101, 45.50, "Lunch", "Meals", "2026-07-01"),
                new Expense(2, 102, 75.00, "Hotel", "Travel", "2026-07-02"));
        when(expenseDAO.findPendingExpenses()).thenReturn(pendingExpenses);

        // Act: Invoke getPendingExpenses
        List<Expense> result = expenseService.getPendingExpenses();

        // Assert: Verify all elements passed, data is integral, and the DAO executed
        assertEquals(2, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
        verify(expenseDAO).findPendingExpenses();
    }

    @Test
    @Issue("KAN-21")
    @Story("Manager View Queue")
    @Tag("edge_case")
    void getPendingExpenses_returnsEmptyList_whenNoPendingExpensesExist() {
        // Arrange: The DAO reports that there are no pending expenses.
        List<Expense> noPendingExpenses = List.of();
        when(expenseDAO.findPendingExpenses()).thenReturn(noPendingExpenses);

        // Act
        List<Expense> result = expenseService.getPendingExpenses();

        // Assert: The service preserves the DAO result without adding data.
        assertSame(noPendingExpenses, result);
        assertTrue(result.isEmpty());
        verify(expenseDAO).findPendingExpenses();
    }

    @Test
    @Issue("KAN-21")
    @Story("Manager View Queue")
    @Tag("negative")
    void getPendingExpenses_propagatesDaoFailure() {
        // Arrange: getPendingExpenses has no error handling, so DAO failures are its
        // contract.
        RuntimeException daoFailure = new RuntimeException("Database unavailable");
        when(expenseDAO.findPendingExpenses()).thenThrow(daoFailure);

        // Act / Assert
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> expenseService.getPendingExpenses());

        assertSame(daoFailure, thrown);
        verify(expenseDAO).findPendingExpenses();
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("smoke")
    void reviewExpense_returnsTrue_whenStatusApprovedAndDaosAllowReview() {
        // Arrange: Create request data. mock objects, and stub DAO behaviors
        int expenseId = 1;
        int reviewerId = 999;
        ApprovalRequest request = new ApprovalRequest("approved", "Looks good");

        Expense expense = new Expense(expenseId, 101, 45.50, "Lunch", "Meals", "2026-07-01");
        Approval approval = new Approval(5, expenseId, "pending", null, null, null);

        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(expense));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(approval));
        when(approvalDAO.reviewExpense(expenseId, "approved", reviewerId, "Looks good")).thenReturn(true);

        // Act: Invoke reviewExpense to the service
        boolean result = expenseService.reviewExpense(expenseId, reviewerId, request);

        // Assert: Verify returns true and the approvalDAO was executed
        assertTrue(result);
        verify(approvalDAO).reviewExpense(expenseId, "approved", reviewerId, "Looks good");
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("smoke")
    void reviewExpense_returnsTrue_whenStatusDeniedAndDaosAllowReview() {
        // Arrange
        int expenseId = 2;
        int reviewerId = 999;
        ApprovalRequest request = new ApprovalRequest("denied", "Missing receipt");
        Expense expense = new Expense(expenseId, 101, 45.50, "Lunch", "Meals", "2026-07-01");
        Approval approval = new Approval(5, expenseId, "pending", null, null, null);

        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(expense));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(approval));
        when(approvalDAO.reviewExpense(expenseId, "denied", reviewerId, "Missing receipt")).thenReturn(true);

        // Act
        boolean result = expenseService.reviewExpense(expenseId, reviewerId, request);

        // Assert
        assertTrue(result);
        verify(approvalDAO).reviewExpense(expenseId, "denied", reviewerId, "Missing receipt");
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenExpenseDoesNotExist() {
        // Arrange
        int expenseId = 404;
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.empty());

        // Act
        boolean result = expenseService.reviewExpense(expenseId, 999, new ApprovalRequest("approved", "Looks good"));

        // Assert
        assertFalse(result);
        verify(expenseDAO).findById(expenseId);
        verify(approvalDAO, never()).findByExpenseId(expenseId);
        verify(approvalDAO, never()).reviewExpense(expenseId, "approved", 999, "Looks good");
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenApprovalRecordDoesNotExist() {
        // Arrange
        int expenseId = 1;
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(existingExpense(expenseId)));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.empty());

        // Act
        boolean result = expenseService.reviewExpense(expenseId, 999, new ApprovalRequest("approved", "Looks good"));

        // Assert
        assertFalse(result);
        verify(approvalDAO).findByExpenseId(expenseId);
        verify(approvalDAO, never()).reviewExpense(expenseId, "approved", 999, "Looks good");
    }

    @ParameterizedTest(name = "current status [{0}] returns false")
    @ValueSource(strings = { "approved", "denied" })
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenExpenseHasAlreadyBeenReviewed(String currentStatus) {
        // Arrange
        int expenseId = 1;
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(existingExpense(expenseId)));
        when(approvalDAO.findByExpenseId(expenseId))
                .thenReturn(Optional.of(new Approval(5, expenseId, currentStatus, 999, "Reviewed", "2026-07-03")));

        // Act
        boolean result = expenseService.reviewExpense(expenseId, 999, new ApprovalRequest("approved", "Looks good"));

        // Assert
        assertFalse(result);
        verify(approvalDAO).findByExpenseId(expenseId);
        verify(approvalDAO, never()).reviewExpense(expenseId, "approved", 999, "Looks good");
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenRequestIsNull() {
        // Act
        boolean result = expenseService.reviewExpense(1, 999, null);

        // Assert: Request validation occurs before either DAO is used.
        assertFalse(result);
        verifyNoInteractions(expenseDAO, approvalDAO);
    }

    @ParameterizedTest(name = "status [{0}] returns false")
    @NullSource
    @ValueSource(strings = { "", "   " })
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenStatusIsMissing(String status) {
        // Act
        boolean result = expenseService.reviewExpense(1, 999, new ApprovalRequest(status, "Looks good"));

        // Assert: Status validation occurs before either DAO is used.
        assertFalse(result);
        verifyNoInteractions(expenseDAO, approvalDAO);
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("edge_case")
    void reviewExpense_normalizesMixedCaseStatusBeforeSaving() {
        // Arrange
        int expenseId = 1;
        int reviewerId = 999;
        ApprovalRequest request = new ApprovalRequest("ApPrOvEd", "Looks good");
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(existingExpense(expenseId)));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(pendingApproval(expenseId)));
        when(approvalDAO.reviewExpense(expenseId, "approved", reviewerId, "Looks good")).thenReturn(true);

        // Act
        boolean result = expenseService.reviewExpense(expenseId, reviewerId, request);

        // Assert
        assertTrue(result);
        verify(approvalDAO).reviewExpense(expenseId, "approved", reviewerId, "Looks good");
    }

    @ParameterizedTest(name = "comment [{0}] passes unchanged")
    @NullSource
    @ValueSource(strings = { "", "  Needs receipt  " })
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("edge_case")
    void reviewExpense_passesOptionalCommentToDaoUnchanged(String comment) {
        // Arrange: Comments are optional and the service does not trim or normalize them.
        int expenseId = 1;
        int reviewerId = 999;
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(existingExpense(expenseId)));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(pendingApproval(expenseId)));
        when(approvalDAO.reviewExpense(expenseId, "approved", reviewerId, comment)).thenReturn(true);

        // Act
        boolean result = expenseService.reviewExpense(
                expenseId,
                reviewerId,
                new ApprovalRequest("approved", comment));

        // Assert
        assertTrue(result);
        verify(approvalDAO).reviewExpense(expenseId, "approved", reviewerId, comment);
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenDaoCannotSaveReview() {
        // Arrange
        int expenseId = 1;
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(existingExpense(expenseId)));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(pendingApproval(expenseId)));
        when(approvalDAO.reviewExpense(expenseId, "approved", 999, "Looks good")).thenReturn(false);

        // Act
        boolean result = expenseService.reviewExpense(expenseId, 999, new ApprovalRequest("approved", "Looks good"));

        // Assert
        assertFalse(result);
        verify(approvalDAO).reviewExpense(expenseId, "approved", 999, "Looks good");
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_propagatesDaoFailureWhenSavingReview() {
        // Arrange
        int expenseId = 1;
        RuntimeException daoFailure = new RuntimeException("Database unavailable");
        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(existingExpense(expenseId)));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(pendingApproval(expenseId)));
        when(approvalDAO.reviewExpense(expenseId, "approved", 999, "Looks good")).thenThrow(daoFailure);

        // Act / Assert: The service does not catch DAO runtime exceptions.
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> expenseService.reviewExpense(expenseId, 999, new ApprovalRequest("approved", "Looks good")));

        assertSame(daoFailure, thrown);
        verify(approvalDAO).reviewExpense(expenseId, "approved", 999, "Looks good");
    }

    @Test
    @Issue("KAN-22")
    @Story("Manager Approve Deny")
    @Tag("negative")
    void reviewExpense_returnsFalse_whenStatusIsInvalid() {
        // Arrange: Create bad ApprovalRequest object
        ApprovalRequest request = new ApprovalRequest("penguin", "Invalid status test");

        // Act: Invoke reviewExpense on bad request
        boolean result = expenseService.reviewExpense(1, 999, request);

        // Assert: Verify returns false and ensure the service aborted early without
        // calling the DAOs
        assertFalse(result);
        verify(expenseDAO, never()).findById(1);
        verify(approvalDAO, never()).reviewExpense(org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    }

    private Expense existingExpense(int expenseId) {
        return new Expense(expenseId, 101, 45.50, "Lunch", "Meals", "2026-07-01");
    }

    private Approval pendingApproval(int expenseId) {
        return new Approval(5, expenseId, "pending", null, null, null);
    }
}
