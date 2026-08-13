package com.revature;

import java.util.*;

public class LandPerimiter {

    public static String landPerimeter(String[] arr) {
        int y_length = arr.length;
        int x_length = arr[0].length();
        for (String currStr : arr) {
            System.out.println(currStr);
        }
        // Add extra space to remove border edge cases
        int[][] landArr = new int[y_length + 2][x_length + 2];
        int totalLandPerimeter = 0;
        // Convert string 2d array to int 2d array
        for (int i = 0; i < y_length; i++) {
            for (int j = 0; j < x_length; j++) {
                if (arr[i].charAt(j) == 'X') {
                    landArr[i + 1][j + 1] = 1;
                }
            }
        }
        for (int[] currArr : landArr) {
            System.out.println(Arrays.toString(currArr));
        }
        // For each land point, count adjacent land points to see how much is added to
        // the perimeter
        for (int i = 1; i < y_length + 1; i++) {
            for (int j = 1; j < x_length + 1; j++) {
                if (landArr[i][j] == 1) { // Found land point, count surrounding points
                    int adjacentLands = 0;
                    System.out.println("Found Land");
                    for (int k = -1; k < 2; k++) {
                        for (int l = -1; l < 2; l++) {
                            System.out.print(landArr[i + k][j + l]);
                            if (k == l)
                                continue; // Skip center, top right and bottom left corners
                            if (k == 1 && l == -1)
                                continue; // Skip bottom right corner
                            if (k == -1 && l == 1)
                                continue; // Skip top left corner
                            if (landArr[i + k][j + l] == 1) { // Adjacent land found, increment
                                adjacentLands++;
                            }
                        }
                        System.out.println();
                    }
                    int addedPerimeter = 4 - adjacentLands;
                    System.out.println("Added Perimeter: " + addedPerimeter);
                    totalLandPerimeter += addedPerimeter;

                }
            }
        }
        return "Total land perimeter: " + totalLandPerimeter;
    }
}