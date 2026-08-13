import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Log File Analyzer — INSTRUCTOR SOLUTION
 * =========================================
 * Do not distribute to trainees before they attempt the exercise.
 */
public class LogAnalyzer {

    private static final String LOG_FILE    = "data/test-run.log";
    private static final String REPORT_FILE = "output/summary-report.txt";
    private static final String AUDIT_FILE  = "output/audit.log";

    private List<String> passList = new ArrayList<>();
    private List<String> failList = new ArrayList<>();
    private List<String> skipList = new ArrayList<>();

    // =========================================================================
    // TASK 1 — Validate
    // =========================================================================
    void validateInput() throws IOException {
        Path path = Paths.get(LOG_FILE);

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalStateException("Log file not found: " + path.toAbsolutePath());
        }

        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        System.out.println("[OK] Log file found: " + path);
        System.out.printf("     Size: %d bytes | Last modified: %s%n",
                Files.size(path), attrs.lastModifiedTime().toInstant());
    }

    // =========================================================================
    // TASK 2 — Parse
    // =========================================================================
    void parseLog() throws IOException {
        Path path = Paths.get(LOG_FILE);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[PASS]")) {
                    passList.add(line);
                } else if (line.startsWith("[FAIL]")) {
                    failList.add(line);
                } else if (line.startsWith("[SKIP]")) {
                    skipList.add(line);
                }
            }
        }
        System.out.printf("Parsed: PASS=%d  FAIL=%d  SKIP=%d%n",
                passList.size(), failList.size(), skipList.size());
    }

    // =========================================================================
    // TASK 3 — Write report
    // =========================================================================
    void writeSummaryReport() throws IOException {
        Path report = Paths.get(REPORT_FILE);
        Files.createDirectories(report.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(report)) {
            writer.write("=== Log Analyzer Report ===");
            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now());
            writer.newLine();
            writer.newLine();

            int total = passList.size() + failList.size() + skipList.size();
            writer.write("Total Tests : " + total);       writer.newLine();
            writer.write("  PASS      : " + passList.size()); writer.newLine();
            writer.write("  FAIL      : " + failList.size()); writer.newLine();
            writer.write("  SKIP      : " + skipList.size()); writer.newLine();
            writer.newLine();

            writer.write("## FAILURES");
            writer.newLine();
            for (String entry : failList) {
                writer.write("  " + entry);
                writer.newLine();
            }
            writer.newLine();

            writer.write("## SKIPPED");
            writer.newLine();
            for (String entry : skipList) {
                writer.write("  " + entry);
                writer.newLine();
            }
        }
        System.out.println("Report written: " + Paths.get(REPORT_FILE).toAbsolutePath());
    }

    // =========================================================================
    // TASK 4 — Append audit
    // =========================================================================
    void appendAuditEntry() throws IOException {
        Path audit = Paths.get(AUDIT_FILE);
        Files.createDirectories(audit.getParent());

        String entry = String.format("[%s] Run complete | PASS=%d FAIL=%d SKIP=%d",
                LocalDateTime.now(), passList.size(), failList.size(), skipList.size());

        try (BufferedWriter writer = Files.newBufferedWriter(
                audit, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            writer.write(entry);
            writer.newLine();
        }
        System.out.println("Audit appended: " + entry);
    }

    // =========================================================================
    // Main
    // =========================================================================
    public static void main(String[] args) {
        LogAnalyzer analyzer = new LogAnalyzer();
        try {
            System.out.println("=== Log Analyzer Starting ===\n");
            analyzer.validateInput();
            analyzer.parseLog();
            analyzer.writeSummaryReport();
            analyzer.appendAuditEntry();
            System.out.println("\n=== Analysis Complete ===");
            System.out.println("Report : " + Paths.get(REPORT_FILE).toAbsolutePath());
            System.out.println("Audit  : " + Paths.get(AUDIT_FILE).toAbsolutePath());
        } catch (IllegalStateException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[ERROR] File operation failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
