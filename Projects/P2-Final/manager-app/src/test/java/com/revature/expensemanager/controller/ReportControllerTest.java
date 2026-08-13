package com.revature.expensemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.expensemanager.allure.ParentSuite;
import com.revature.expensemanager.dto.EmployeeSummary;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.service.ReportExportService;
import com.revature.expensemanager.service.ReportService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

/**
 * Unit coverage for {@link ReportController} against a mocked {@link Context}.
 *
 * <p>These cases previously lived in {@code ReportServiceTest}, where they
 * reached through a <em>real</em> {@code ReportService} to a mocked DAO in order
 * to assert controller behaviour. That made a file named {@code ...ServiceTest}
 * the home of the request-parsing rules, and it meant a controller assertion
 * could fail because of something the service did. Here the service is mocked
 * outright, so each test states one thing: given this request, the controller
 * chose this status and delegated exactly this much.
 *
 * <p>Query-parameter parsing, validation ordering, status mapping <em>and the
 * exact contract message</em> are the subject. What the SQL returns is
 * {@code JdbcReportDAOTest}'s job; what the running service answers over HTTP is
 * {@code ReportsIT}'s.
 *
 * <p>The message assertions matter to the shape of the suite. Three of this
 * controller's rejections are all {@code 400}, so a status-only assertion cannot
 * tell them apart - which is why {@code ReportsIT} previously re-drove every
 * validation branch over real HTTP purely to pin the text. Owning the messages
 * here lets the HTTP layer keep one representative case per branch.
 */
@ExtendWith(MockitoExtension.class)
@Epic("Manager Portal Backend")
@Feature("Reporting")
@ParentSuite("Manager - Controller Layer")
@Issue("KAN-23")
@Story("Manager Reports")
@Tag("api")
@DisplayName("Reporting Controller")
class ReportControllerTest {

    // The exact ErrorResponse text each branch promises. Transcribed rather than
    // shared with the production constant on purpose: a test that reads the same
    // string the controller writes would follow an unintended message change
    // instead of catching it.
    private static final String USER_ID_NOT_A_NUMBER = "userId must be a valid number.";
    private static final String USER_ID_NOT_POSITIVE = "userId must be a positive number.";
    private static final String EMPLOYEE_NOT_FOUND = "Employee not found.";
    private static final String INVALID_CATEGORY = "Invalid category. Valid categories: "
            + "TRAVEL, MEALS, LODGING, OFFICE_SUPPLIES, EQUIPMENT, SOFTWARE, TRAINING, OTHER.";
    private static final String DATES_REQUIRED = "startDate and endDate query parameters are required.";
    private static final String DATE_FORMAT_REQUIRED = "startDate and endDate must use YYYY-MM-DD format.";
    private static final String DATE_ORDER_REQUIRED = "startDate must be on or before endDate.";

    @Mock
    private ReportService reportService;

    @Mock
    private ReportExportService reportExportService;

    private ReportController controller;
    private Context ctx;

    @BeforeEach
    void setUp() {
        controller = new ReportController(reportService, reportExportService);
        // RETURNS_SELF so the production `status(...).json(...)` chain resolves.
        ctx = mock(Context.class, Answers.RETURNS_SELF);
    }

    @Nested
    @DisplayName("Report by Employee")
    class ReportByEmployee {

        @ParameterizedTest(name = "userId=[{0}] -> 400 \"{1}\"")
        @CsvSource(nullValues = "NULL", value = {
                "NULL,        userId query parameter is required.",
                "'',          userId query parameter is required.",
                "abc,         userId must be a valid number.",
                "1.5,         userId must be a valid number.",
                "9999999999,  userId must be a valid number.",
                "0,           userId must be a positive number.",
                "-1,          userId must be a positive number.",
                "-2147483648, userId must be a positive number."
        })
        @Tag("negative")
        @DisplayName("Missing, unparseable, zero and negative ids are each rejected with their own message")
        void rejectsInvalidUserId(String userIdParam, String expectedMessage) {

            // Arrange: the three rejections share a status but not a message, so
            // pairing each input with its text is what keeps them distinguishable.
            // `9999999999` parses as a long but overflows an int, which is why it
            // lands on the unparseable branch rather than the non-positive one.
            when(ctx.queryParam("userId")).thenReturn(userIdParam);

            // Act
            controller.getExpensesByEmployee(ctx);

            // Assert: rejected outright, and no report was ever requested
            assertErrorResponse(HttpStatus.BAD_REQUEST, expectedMessage);
            verifyNoInteractions(reportService);
        }

        @Test
        @Tag("negative")
        @DisplayName("A well-formed id for an employee who does not exist returns 404, not 400")
        void unknownEmployeeReturnsNotFound() {

            // Arrange
            when(ctx.queryParam("userId")).thenReturn("999");
            when(reportService.employeeExists(999)).thenReturn(false);

            // Act
            controller.getExpensesByEmployee(ctx);

            // Assert: the existence check gates the report
            assertErrorResponse(HttpStatus.NOT_FOUND, EMPLOYEE_NOT_FOUND);
            verify(reportService, never()).getExpensesByEmployee(anyInt());
        }

        @Test
        @Tag("smoke")
        @DisplayName("A valid employee id returns 200 and the service's rows")
        void validUserIdReturnsReport() {

            // Arrange
            List<Expense> expenses = List.of(
                    new Expense(1, 101, 50.00, "Taxi", "TRAVEL", "2026-07-01"));
            when(ctx.queryParam("userId")).thenReturn("101");
            when(reportService.employeeExists(101)).thenReturn(true);
            when(reportService.getExpensesByEmployee(101)).thenReturn(expenses);

            // Act
            controller.getExpensesByEmployee(ctx);

            // Assert
            verify(ctx).status(HttpStatus.OK);
            verify(ctx).json(expenses);
        }
    }

    @Nested
    @DisplayName("Report by Category")
    class ReportByCategory {

        @ParameterizedTest(name = "category=[{0}] is rejected with 400")
        @NullSource
        @ValueSource(strings = { "", " ", "Video Games" })
        @Tag("negative")
        @DisplayName("A null, blank, or unrecognised category is rejected")
        void rejectsInvalidCategory(String categoryParam) {

            // Arrange
            when(ctx.queryParam("category")).thenReturn(categoryParam);

            // Act
            controller.getExpensesByCategory(ctx);

            // Assert: ExpenseCategory.fromInput raises "Category is required." for
            // null/blank and "Invalid category." for an unknown name, but the
            // handler discards both and substitutes one message - so all four
            // inputs are genuinely indistinguishable at the response boundary.
            assertErrorResponse(HttpStatus.BAD_REQUEST, INVALID_CATEGORY);
            verify(reportService, never()).getExpensesByCategory(anyString());
        }

        @ParameterizedTest(name = "input [{0}] is queried as TRAVEL")
        @ValueSource(strings = { "travel", "TRAVEL", "Travel", " tRaVeL " })
        @Tag("edge_case")
        @DisplayName("Casing and surrounding spaces are normalised to the canonical category name")
        void normalisesCategoryBeforeQuerying(String categoryParam) {

            // Arrange
            when(ctx.queryParam("category")).thenReturn(categoryParam);

            // Act
            controller.getExpensesByCategory(ctx);

            // Assert: normalisation happens here, which is why JdbcReportDAO can
            // afford an exact-match WHERE clause.
            verify(reportService).getExpensesByCategory("TRAVEL");
        }

        @Test
        @Tag("smoke")
        @DisplayName("A valid category returns 200 and the service's rows")
        void validCategoryReturnsReport() {

            // Arrange
            List<Expense> expenses = List.of(
                    new Expense(7, 2, 12.80, "Coffee meeting with client", "MEALS", "2026-07-04"));
            when(ctx.queryParam("category")).thenReturn("MEALS");
            when(reportService.getExpensesByCategory("MEALS")).thenReturn(expenses);

            // Act
            controller.getExpensesByCategory(ctx);

            // Assert
            verify(ctx).status(HttpStatus.OK);
            verify(ctx).json(expenses);
        }
    }

    @Nested
    @DisplayName("Report by Date")
    class ReportByDate {

        @ParameterizedTest(name = "start=[{0}], end=[{1}] -> 400")
        @CsvSource(nullValues = "NULL", value = {
                "NULL,        2026-07-10",
                "2026-07-01,  NULL",
                "'',          2026-07-10",
                "2026-07-01,  ''"
        })
        @Tag("negative")
        @DisplayName("A null or blank start/end date is rejected")
        void rejectsMissingDates(String start, String end) {

            // Arrange
            when(ctx.queryParam("startDate")).thenReturn(start);
            when(ctx.queryParam("endDate")).thenReturn(end);

            // Act
            controller.getExpensesByDateRange(ctx);

            // Assert: all four permutations fall through one guard onto one
            // message, so this is the only place they need enumerating.
            assertErrorResponse(HttpStatus.BAD_REQUEST, DATES_REQUIRED);
            verify(reportService, never()).getExpensesByDateRange(anyString(), anyString());
        }

        @ParameterizedTest(name = "start=[{0}], end=[{1}] -> 400")
        @CsvSource({
                "07/01/2026,  2026-07-10",
                "2026-07-01,  2026-13-40",
                "not-a-date,  also-bad"
        })
        @Tag("negative")
        @DisplayName("A date that is not YYYY-MM-DD, or not a real calendar date, is rejected")
        void rejectsInvalidDateFormat(String start, String end) {

            // Arrange
            when(ctx.queryParam("startDate")).thenReturn(start);
            when(ctx.queryParam("endDate")).thenReturn(end);

            // Act
            controller.getExpensesByDateRange(ctx);

            // Assert: `2026-13-40` is correctly shaped but not a real date, so it
            // must land here rather than reaching the DAO, where SQLite's string
            // comparison would happily accept it.
            assertErrorResponse(HttpStatus.BAD_REQUEST, DATE_FORMAT_REQUIRED);
            verify(reportService, never()).getExpensesByDateRange(anyString(), anyString());
        }

        @Test
        @Tag("negative")
        @DisplayName("A start date after the end date is rejected")
        void rejectsStartAfterEnd() {

            // Arrange
            when(ctx.queryParam("startDate")).thenReturn("2026-07-10");
            when(ctx.queryParam("endDate")).thenReturn("2026-07-01");

            // Act
            controller.getExpensesByDateRange(ctx);

            // Assert
            assertErrorResponse(HttpStatus.BAD_REQUEST, DATE_ORDER_REQUIRED);
            verify(reportService, never()).getExpensesByDateRange(anyString(), anyString());
        }

        @Test
        @Tag("edge_case")
        @DisplayName("An identical start and end date is accepted as a single-day range")
        void allowsSameStartAndEnd() {

            // Arrange
            when(ctx.queryParam("startDate")).thenReturn("2026-07-01");
            when(ctx.queryParam("endDate")).thenReturn("2026-07-01");

            // Act
            controller.getExpensesByDateRange(ctx);

            // Assert: not rejected - it reaches the service and answers 200
            verify(reportService).getExpensesByDateRange("2026-07-01", "2026-07-01");
            verify(ctx).status(HttpStatus.OK);
        }

        @Test
        @Tag("edge_case")
        @DisplayName("Validated dates are handed to the service as the exact strings received")
        void passesRawDatesThrough() {

            // Arrange
            when(ctx.queryParam("startDate")).thenReturn("2026-07-01");
            when(ctx.queryParam("endDate")).thenReturn("2026-07-31");

            // Act
            controller.getExpensesByDateRange(ctx);

            // Assert: parsed for validation, but never reformatted - the DAO
            // compares them as text, so any rewriting here would change results.
            verify(reportService).getExpensesByDateRange("2026-07-01", "2026-07-31");
        }
    }

    @Nested
    @DisplayName("CSV export")
    class CsvExport {

        @Test
        @Tag("smoke")
        @DisplayName("export=true writes a file and returns its path in the Report-File header")
        void exportTrueSetsReportFileHeader() {

            // Arrange
            when(ctx.queryParam("category")).thenReturn("MEALS");
            when(ctx.queryParam("export")).thenReturn("true");
            when(reportExportService.exportExpensesToCsv(anyList(), eq("category_report")))
                    .thenReturn("reports/category_report_20260728_101500.csv");

            // Act
            controller.getExpensesByCategory(ctx);

            // Assert
            verify(ctx).header("Report-File", "reports/category_report_20260728_101500.csv");
            verify(ctx).status(HttpStatus.OK);
        }

        @ParameterizedTest(name = "export=[{0}] does not export")
        @NullSource
        @ValueSource(strings = { "", "false", "yes", "1" })
        @Tag("negative")
        @DisplayName("Any value other than \"true\" leaves the response without an export")
        void nonTrueExportParamDoesNotExport(String exportParam) {

            // Arrange
            when(ctx.queryParam("category")).thenReturn("MEALS");
            when(ctx.queryParam("export")).thenReturn(exportParam);

            // Act
            controller.getExpensesByCategory(ctx);

            // Assert
            verifyNoInteractions(reportExportService);
            verify(ctx, never()).header(eq("Report-File"), anyString());
        }
    }

    /**
     * Nested rather than a bare top-level test: when a class mixes {@code @Nested}
     * classes with its own tests, Surefire files the loose ones into whichever
     * nested report it wrote last - this test was landing under "CSV export".
     */
    @Nested
    @DisplayName("Employee lookup")
    class EmployeeLookup {

        @Test
        @Tag("smoke")
        @DisplayName("Returns the service's list as JSON with no status override")
        void getEmployeesReturnsServiceList() {

            // Arrange
            List<EmployeeSummary> employees = List.of(
                    new EmployeeSummary(1, "brian"),
                    new EmployeeSummary(2, "landon"));
            when(reportService.getEmployees()).thenReturn(employees);

            // Act
            controller.getEmployees(ctx);

            // Assert: this handler deliberately never calls status() - Javalin's
            // 200 default stands, and EmployeesIT proves that over real HTTP
            verify(ctx).json(employees);
            assertEquals(2, employees.size());
        }
    }

    /**
     * Asserts the status and the exact {@link ErrorResponse} message the handler
     * serialised.
     *
     * <p>Captures the response body rather than trusting the status alone: this
     * controller answers {@code 400} from three different branches, and a
     * status-only assertion would stay green if one branch's message were
     * repurposed - or if two branches were accidentally collapsed into one.
     */
    private void assertErrorResponse(HttpStatus expectedStatus, String expectedMessage) {
        ArgumentCaptor<ErrorResponse> body = ArgumentCaptor.forClass(ErrorResponse.class);

        verify(ctx).status(expectedStatus);
        verify(ctx).json(body.capture());
        assertEquals(expectedMessage, body.getValue().getMessage());
    }
}
