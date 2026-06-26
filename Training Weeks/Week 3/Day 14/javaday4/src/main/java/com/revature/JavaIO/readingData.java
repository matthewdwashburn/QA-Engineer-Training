package com.revature.JavaIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class readingData {
    //reading line-by-line (classic BufferedReader)
    //Use this for large files - it never loads the entire file into memory

    static void readWithBufferedReader() {
        Path path = Paths.get("data/scores.csv");

        //try-with-resources guarentees the reader is always cloased
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNum = 0;
            while((line = reader.readLine()) != null) {
                lineNum++;
                System.out.printf("Line %2d: %s %n", lineNum, line);
            }
        } catch(IOException e) {
            System.err.println("Cound not read file");
        }
    }
    
    //reading with files convenience methods in modern java
    // for small files this is all you need
    static void readWithFilesAPI() throws IOException {
            Path path = Paths.get("data/scores.csv");

            //Option A: All the lines as List<String>
            System.out.println("readAllLines(path)");
            List<String> lines = Files.readAllLines(path);
            lines.forEach(l -> System.out.println(" " + l));
        }
}
