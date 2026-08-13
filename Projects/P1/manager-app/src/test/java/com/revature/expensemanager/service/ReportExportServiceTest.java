package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.allure.ParentSuite;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;

@Epic("Manager Portal Backend")
@Feature("Reporting")
@ParentSuite("Manager - Service Layer")
@Issue("KAN-23")
@Story("Manager Reports")
@Tag("unit")
@DisplayName("CSV Export")
class ReportExportServiceTest {

    /*
     * OBSERVED behavior of ReportExportService.exportExpensesToCsv(list, reportName).
     * (Captured from a probe run — assertions below are written against these facts.)
     *
     *   Returns    : the file path, e.g.  reports\<reportName>_2026-07-24_11-54-40.csv
     *   File name  : <reportName> + "_" + yyyy-MM-dd_HH-mm-ss + ".csv"
     *   Header     : amount,category,date,description,id,userId   <-- Jackson sorts columns ALPHABETICALLY
     *   Data row   : <amount>,<category>,<date>,<description>,<id>,<userId>   e.g.  50.0,Travel,2026-07-01,Taxi,1,101
     *   Empty list : writes a file with exactly 1 line (header only), no exception
     *   Null list  : throws NullPointerException (service does not guard null)
     *   Blank name : writes a file named  _<timestamp>.csv  (no validation)
     */

    private static final String HEADER = "amount,category,date,description,id,userId";

    private final ReportExportService service = new ReportExportService();

    // The service always writes here (hardcoded relative dir).
    private final Path reportsDir = Path.of("reports");

    // Files that already existed before a test ran — so teardown only removes what THIS test created.
    private Set<Path> preexisting;

    // ---------- setup / teardown ----------

    @BeforeEach
    void snapshotReportsDir() throws IOException {
        preexisting = listReportFiles();
    }

    @AfterEach
    void deleteFilesCreatedByTest() throws IOException {
        for (Path file : listReportFiles()) {
            if (!preexisting.contains(file)) {
                Files.deleteIfExists(file);
            }
        }
    }

    private Set<Path> listReportFiles() throws IOException {
        if (!Files.isDirectory(reportsDir)) {
            return new HashSet<>();
        }
        try (Stream<Path> files = Files.list(reportsDir)) {
            return files.collect(Collectors.toCollection(HashSet::new));
        }
    }

    // ---------- shared sample data ----------

    private List<Expense> sampleExpenses() {
        return List.of(
                new Expense(1, 101, 50.0, "Taxi", "Travel", "2026-07-01"),
                new Expense(2, 102, 20.5, "Coffee", "Meals", "2026-07-02"));
    }

    // ============================================================
    //  Tests
    // ============================================================

    @Test
    @Tag("smoke")
    @DisplayName("A nonempty expense list is exported successfully.")
    void export_nonEmptyList_writesFile() {
        // Act
        String path = service.exportExpensesToCsv(sampleExpenses(), "test-export");

        // Assert: the service reported a path, and a real file exists there
        assertTrue(Files.exists(Path.of(path)), "expected a file at the returned path: " + path);
    }

    @Test
    @Tag("edge_case")
    @DisplayName("The exported filename includes the expected report name.")
    void export_fileName_includesReportName() {
        String path = service.exportExpensesToCsv(sampleExpenses(), "test-export");

        String fileName = Path.of(path).getFileName().toString();
        assertTrue(fileName.startsWith("test-export_"), "unexpected file name: " + fileName);
    }

    @Test
    @Tag("edge_case")
    @DisplayName("The filename follows the <name>_<timestamp>.csv naming convention.")
    void export_fileName_followsNamingConvention() {
        String path = service.exportExpensesToCsv(sampleExpenses(), "test-export");

        String fileName = Path.of(path).getFileName().toString();
        assertTrue(fileName.matches("test-export_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.csv"),
                "file name did not match naming convention: " + fileName);
    }

    @Test
    @Tag("edge_case")
    @DisplayName("The reports directory exists after an export.")
    void export_createsReportsDirectory() {
        service.exportExpensesToCsv(sampleExpenses(), "test-export");

        assertTrue(Files.isDirectory(reportsDir), "reports directory should exist after export");
    }

    @Test
    @Tag("edge_case")
    @DisplayName("The CSV contains the expected headers.")
    void export_writesExpectedHeaders() throws IOException {
        String path = service.exportExpensesToCsv(sampleExpenses(), "test-export");

        List<String> lines = Files.readAllLines(Path.of(path));
        assertEquals(HEADER, lines.get(0));
    }

    @Test
    @Tag("edge_case")
    @DisplayName("Expense fields are written correctly.")
    void export_writesFieldValues() throws IOException {
        String path = service.exportExpensesToCsv(sampleExpenses(), "test-export");

        List<String> lines = Files.readAllLines(Path.of(path));
        // column order matches HEADER: amount,category,date,description,id,userId
        assertEquals("50.0,Travel,2026-07-01,Taxi,1,101", lines.get(1));
    }

    @Test
    @Tag("edge_case")
    @DisplayName("Multiple expenses produce multiple data rows.")
    void export_writesRowPerExpense() throws IOException {
        List<Expense> expenses = sampleExpenses();
        String path = service.exportExpensesToCsv(expenses, "test-export");

        List<String> lines = Files.readAllLines(Path.of(path));
        assertEquals(expenses.size() + 1, lines.size(), "expected 1 header row + 1 row per expense");
    }

    @Test
    @Tag("edge_case")
    @DisplayName("An empty expense list writes a header-only file.")
    void export_emptyList_writesHeaderOnly() throws IOException {
        String path = service.exportExpensesToCsv(List.of(), "test-export");

        List<String> lines = Files.readAllLines(Path.of(path));
        assertTrue(Files.exists(Path.of(path)), "a file should still be created for an empty list");
        assertEquals(1, lines.size(), "empty list should produce a header row and no data rows");
    }

    @Test
    @Tag("negative")
    @DisplayName("A null expense list throws (current, unguarded behavior).")
    void export_nullList_throwsNpe() {
        
        assertThrows(NullPointerException.class,
                () -> service.exportExpensesToCsv(null, "test-export"));
    }

    @Test
    @Tag("edge_case")
    @DisplayName("A blank report name still produces a file (no validation).")
    void export_blankReportName_isNotValidated() {
        String path = service.exportExpensesToCsv(sampleExpenses(), "");

        // Documents that blank names aren't rejected: file name becomes "_<timestamp>.csv".
        String fileName = Path.of(path).getFileName().toString();
        assertTrue(fileName.startsWith("_"), "blank name should yield _<timestamp>.csv, got: " + fileName);
    }

    @Test
    @Tag("negative")
    @DisplayName("A file-writing failure is wrapped in a RuntimeException.")
    void export_wrapsWriteFailure_inRuntimeException() {
        
        // A report name containing a path separator makes the target land in a non-existent
        // subdirectory (reports/sub/dir_<ts>.csv), so the write fails with an IOException that
        // the service catches and rethrows. Lets us exercise the failure path with no prod change.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.exportExpensesToCsv(sampleExpenses(), "sub/dir"));
        assertEquals("Error exporting report to CSV file.", ex.getMessage());
    }

    @Test
    @Tag("edge_case")
    @DisplayName("The returned path points at the file that was created.")
    void export_returnedPathMatchesCreatedFile() {
        String path = service.exportExpensesToCsv(sampleExpenses(), "test-export");

        Path created = Path.of(path);
        assertTrue(Files.exists(created), "returned path should point to a real file: " + path);
        assertTrue(created.startsWith(reportsDir), "file should live under the reports directory: " + path);
    }
}
