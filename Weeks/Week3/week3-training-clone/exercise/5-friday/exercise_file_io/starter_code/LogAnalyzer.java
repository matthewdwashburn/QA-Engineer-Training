import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Log File Analyzer — Starter Code
 * ==================================
 * Complete each TODO method. Do NOT change method signatures.
 *
 * Run: javac LogAnalyzer.java && java LogAnalyzer
 */
public class LogAnalyzer {

    // --- Configuration ---
    private static final String LOG_FILE    = "data/test-run.log";
    private static final String REPORT_FILE = "output/summary-report.txt";
    private static final String AUDIT_FILE  = "output/audit.log";

    // --- Result lists — populated by parseLog() ---
    private List<String> passList = new ArrayList<>();
    private List<String> failList = new ArrayList<>();
    private List<String> skipList = new ArrayList<>();

    // =========================================================================
    // TASK 1 — Validate the input file exists and print its metadata.
    // =========================================================================
    void validateInput() throws IOException {
        Path path = Paths.get(LOG_FILE);

        // TODO 1a: Check Files.exists() and Files.isRegularFile().
        //          If the file is missing, throw an IllegalStateException
        //          with the message: "Log file not found: " + path.toAbsolutePath()


        // TODO 1b: Read BasicFileAttributes and print:
        //          "[OK] Log file found: <path>"
        //          "     Size: <bytes> bytes | Last modified: <timestamp>"


    }

    // =========================================================================
    // TASK 2 — Read the log file and populate passList, failList, skipList.
    // =========================================================================
    void parseLog() throws IOException {
        Path path = Paths.get(LOG_FILE);

        // TODO 2: Use Files.newBufferedReader() inside a try-with-resources.
        //         For each line, check startsWith("[PASS]"), "[FAIL]", "[SKIP]"
        //         and add to the appropriate list.


    }

    // =========================================================================
    // TASK 3 — Write a formatted summary report to output/summary-report.txt.
    // =========================================================================
    void writeSummaryReport() throws IOException {
        Path report = Paths.get(REPORT_FILE);

        // TODO 3a: Create the output/ directory if it does not exist.
        //          Hint: Files.createDirectories(report.getParent())


        // TODO 3b: Use Files.newBufferedWriter() inside a try-with-resources.
        //          Write the header, totals, and failure/skip sections.
        //          Use writer.newLine() for line separators.


    }

    // =========================================================================
    // TASK 4 — Append a timestamped one-liner to output/audit.log.
    // =========================================================================
    void appendAuditEntry() throws IOException {
        Path audit = Paths.get(AUDIT_FILE);

        // TODO 4: Open with StandardOpenOption.APPEND and CREATE so that
        //         each program run adds a new line rather than overwriting.
        //         Format: "[<timestamp>] Run complete | PASS=<n> FAIL=<n> SKIP=<n>"


    }

    // =========================================================================
    // Main — orchestrates the four tasks in order.
    // =========================================================================
    public static void main(String[] args) {
        LogAnalyzer analyzer = new LogAnalyzer();

        try {
            System.out.println("=== Log Analyzer Starting ===\n");

            analyzer.validateInput();    // Task 1
            analyzer.parseLog();         // Task 2
            analyzer.writeSummaryReport(); // Task 3
            analyzer.appendAuditEntry(); // Task 4

            System.out.println("\n=== Analysis Complete ===");
            System.out.println("Report : " + Paths.get(analyzer.REPORT_FILE).toAbsolutePath());
            System.out.println("Audit  : " + Paths.get(analyzer.AUDIT_FILE).toAbsolutePath());

        } catch (IllegalStateException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[ERROR] File operation failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
