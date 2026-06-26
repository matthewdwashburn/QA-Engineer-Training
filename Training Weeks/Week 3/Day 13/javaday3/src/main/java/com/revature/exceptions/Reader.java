package com.revature.exceptions;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;

public class Reader {
    public static void main(String[] args) {
        try {
            readAwfulFile();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readAwfulFile() throws FileNotFoundException {
        File f = new File("/not/a/real/file/path");
        System.out.println(f.exists());
        FileReader read = new FileReader(f);
        System.out.println(read);
    }
}
