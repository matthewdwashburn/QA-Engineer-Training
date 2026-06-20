package com.revature.JavaIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class setup {
    static void setupSampleData() throws IOException {
        Path dataDir = Paths.get("data");
        Files.createDirectories(dataDir);

        Path csv = dataDir.resolve("scores.csv");
        if(!Files.exists(csv)) {
            List<String> rows = List.of(
                "Name, Score",
                "Alic, 82",
                "Bob, 45",
                "Carol, 91",
                "Dave, 38",
                "Eve, 77",
                "Frank, 29"
    );
    Files.write(csv,rows);
        }
    }
}
