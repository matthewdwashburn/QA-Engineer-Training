package com.revature;

public class EvenIndex {
    public static int findEvenIndex(int[] arr) {
        // Track total left and right
        int totalRight = 0;
        int totalLeft = 0;
        // Get total right at the start
        for(int i = 0; i < arr.length; i++) { 
            totalRight += arr[i];
        }

        // Find index with even sum on both sides
        for(int i = 0; i < arr.length; i++) {
            int curr = arr[i];
            totalRight -= curr;
            if(totalRight == totalLeft) {
                return i;
            } else { // Not matching, add current index value to left sum
                totalLeft += curr;
            }

        }
        return -1;
    }
}
