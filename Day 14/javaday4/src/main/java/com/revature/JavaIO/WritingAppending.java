package com.revature.JavaIO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

public class WritingAppending {
    static void writeAndCreate() throws IOException{
        // Writing and Appending
        // Three flavors: BufferedWriter, writeString, write(List)
        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);

        //Write with BufferedWriter
        Path report = outputDir.resolve("report.txt");
        try(BufferedWriter writer = Files.newBufferedWriter(report)) {
            writer.write(" === Test Run Report === ");
            writer.newLine();
            writer.write("Generated: " + LocalDateTime.now());
            writer.newLine();
        }
        System.out.println("Wrote: " + report.toAbsolutePath());
        //Append
        try(BufferedWriter writer = Files.newBufferedWriter(report, StandardOpenOption.APPEND)){
            writer.write("PASS: LoginTest");
            writer.newLine();
            writer.write("FAIL: CheckoutTest");
            writer.newLine();
        }
        System.out.println("Append To: " + report.toAbsolutePath());

        //Files.writeString() - most concise ---
        Path summary = outputDir.resolve("summary.txt");
        Files.writeString(summary, "Total: 50 | Pass: 48 | Fail: 2 \n");
        System.out.println("Summary written: " + Files.readString(summary));

        //Files.write(List)
        Path results = outputDir.resolve("results.txt");
        List<String> entries = List.of("PASS LoginTest", "PASS SearchTest", "FAIL CheckoutTest");
        Files.write(results, entries);
        System.out.println("Results list written: " + Files.readAllLines(results));
    }

    static void endToEndScenario() throws IOException {
        Path input = Paths.get("data/scores.csv");
        Path output = Paths.get("output/failures.txt");

        //Read all Lines (skip the header)
        List<String> failures = Files.readAllLines(input)
            .stream()
            .skip(1) //Skip name,score header
            .filter(line -> {
                String [] parts = line.split(",");
                int score = Integer.parseInt(parts[1].trim());
                return score < 50;
            })
            .toList();
        
        //write the filtered report
        Files.createDirectories(output.getParent());
        Files.write(output, failures);
        System.out.println("Failures Found: " + failures.size());
        System.out.println("Report Saved: " + output.toAbsolutePath());
        failures.forEach(f -> System.out.println(" >> " + f));
    }

}
