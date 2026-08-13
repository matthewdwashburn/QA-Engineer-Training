package com.revature.expensemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.revature.expensemanager.allure.ParentSuite;
import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.service.ExpenseService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

/**
 * Unit coverage for {@link ExpenseController} against a mocked Javalin
 * {@link Context}. The service is mocked so these tests cover only request
 * validation, response mapping, and controller-to-service delegation.
 */
@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Expense Review")
@ParentSuite("Manager - Controller Layer")
@Story("Manager expense review")
@Tag("api")
@DisplayName("Expense Controller")
class ExpenseControllerTest {

    private static final String INVALID_REVIEW_BODY_MESSAGE = "Invalid review request body.";

    @Mock
    private ExpenseService expenseService;

    private ExpenseController controller;
    private Context ctx;

    @BeforeEach
    void setUp() {
        controller = new ExpenseController(expenseService);
        // Supports production's status(...).json(...) response chains.
        ctx = mock(Context.class, Answers.RETURNS_SELF);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Pending expenses return 200 and the service results")
    void getPendingExpenses_returnsOkAndServiceResults() {
        List<Expense> expenses = List.of(
                new Expense(1, 101, 42.50, "Taxi", "TRAVEL", "2026-07-01"));
        when(ctx.attribute("userId")).thenReturn(900);
        when(expenseService.getPendingExpenses()).thenReturn(expenses);

        controller.getPendingExpenses(ctx);

        verify(expenseService).getPendingExpenses();
        verify(ctx).status(HttpStatus.OK);
        verify(ctx).json(expenses);
    }

    @Test
    @Tag("negative")
    @DisplayName("A non-numeric expense id is rejected before the service is called")
    void reviewExpense_returnsBadRequest_whenExpenseIdIsNotNumeric() {
        when(ctx.pathParam("id")).thenReturn("abc");

        controller.reviewExpense(ctx);

        assertErrorResponse(HttpStatus.BAD_REQUEST, "Invalid expense id.");
        verifyNoInteractions(expenseService);
    }

    @Test
    @Tag("negative")
    @DisplayName("A blank review body is rejected before parsing or service delegation")
    void reviewExpense_returnsBadRequest_whenRequestBodyIsBlank() {
        when(ctx.pathParam("id")).thenReturn("42");
        when(ctx.body()).thenReturn(" ");

        controller.reviewExpense(ctx);

        assertErrorResponse(HttpStatus.BAD_REQUEST, INVALID_REVIEW_BODY_MESSAGE);
        verify(ctx, never()).bodyAsClass(ApprovalRequest.class);
        verifyNoInteractions(expenseService);
    }

    @Test
    @Tag("negative")
    @DisplayName("An unreadable review body is rejected before service delegation")
    void reviewExpense_returnsBadRequest_whenBodyCannotBeDeserialized() {
        when(ctx.pathParam("id")).thenReturn("42");
        when(ctx.body()).thenReturn("{invalid json}");
        when(ctx.bodyAsClass(ApprovalRequest.class)).thenReturn(null);

        controller.reviewExpense(ctx);

        assertErrorResponse(HttpStatus.BAD_REQUEST, INVALID_REVIEW_BODY_MESSAGE);
        verifyNoInteractions(expenseService);
    }

    @Test
    @Tag("negative")
    @DisplayName("Malformed JSON is rejected before service delegation")
    void reviewExpense_returnsBadRequest_whenBodyParsingThrowsJacksonException() {
        when(ctx.pathParam("id")).thenReturn("42");
        when(ctx.body()).thenReturn("{invalid json}");
        when(ctx.bodyAsClass(ApprovalRequest.class)).thenAnswer(
                invocation -> { throw JsonMappingException.from((JsonParser) null, "Malformed review JSON"); });

        controller.reviewExpense(ctx);

        assertErrorResponse(HttpStatus.BAD_REQUEST, INVALID_REVIEW_BODY_MESSAGE);
        verifyNoInteractions(expenseService);
    }

    @Test
    @Tag("smoke")
    @DisplayName("A successful review returns 200 and delegates the parsed request and manager id")
    void reviewExpense_returnsOk_whenServiceReviewsExpense() {
        ApprovalRequest request = new ApprovalRequest("approved", "Receipt verified");
        when(ctx.pathParam("id")).thenReturn("42");
        when(ctx.body()).thenReturn("{\"status\":\"approved\"}");
        when(ctx.bodyAsClass(ApprovalRequest.class)).thenReturn(request);
        when(ctx.attribute("userId")).thenReturn(900);
        when(expenseService.reviewExpense(42, 900, request)).thenReturn(true);

        controller.reviewExpense(ctx);

        verify(expenseService).reviewExpense(42, 900, request);
        verify(ctx).status(HttpStatus.OK);
        verify(ctx).json(Map.of("message", "Expense reviewed successfully"));
    }

    @Test
    @Tag("negative")
    @DisplayName("A review rejected by the service returns 400")
    void reviewExpense_returnsBadRequest_whenServiceCannotReviewExpense() {
        ApprovalRequest request = new ApprovalRequest("denied", "Receipt missing");
        when(ctx.pathParam("id")).thenReturn("42");
        when(ctx.body()).thenReturn("{\"status\":\"denied\"}");
        when(ctx.bodyAsClass(ApprovalRequest.class)).thenReturn(request);
        when(ctx.attribute("userId")).thenReturn(900);
        when(expenseService.reviewExpense(42, 900, request)).thenReturn(false);

        controller.reviewExpense(ctx);

        verify(expenseService).reviewExpense(42, 900, request);
        assertErrorResponse(HttpStatus.BAD_REQUEST, "Unable to review expense");
    }

    private void assertErrorResponse(HttpStatus status, String expectedMessage) {
        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(ctx).status(status);
        verify(ctx).json(errorCaptor.capture());
        assertEquals(expectedMessage, errorCaptor.getValue().getMessage());
    }
}
