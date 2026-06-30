package com.revature.JavaIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class inspection {
    static void inspectPath() throws IOException {
        Path path = Paths.get("data/scores.csv");

        System.out.println("Path: " + path);
        System.out.println("Absolute Path: " + path.toAbsolutePath());
        System.out.println("File name: " + path.getFileName());
        System.out.println("Parent dir: " + path.getParent());
        System.out.println("Exists: " + Files.exists(path));
        System.out.println("Is regular file: " + Files.isRegularFile(path));
        System.out.println("Size (bytes): " + Files.size(path));

        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        System.out.println("Last modified:   " + attrs.lastModifiedTime());
    }
}
