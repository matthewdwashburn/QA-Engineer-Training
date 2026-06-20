package com.revature.JavaIO;

import java.io.IOException;

public class DemoIO {
    public static void main(String[] args) throws IOException {
        try {
            setup.setupSampleData();
            inspection.inspectPath();
            readingData.readWithBufferedReader();
            System.out.println("--------------------");
            readingData.readWithFilesAPI();
            WritingAppending.writeAndCreate();
            WritingAppending.endToEndScenario();
        } catch (IOException e) {
            System.out.println("File Error occurred: " + e.getStackTrace());
        }
    }
}
