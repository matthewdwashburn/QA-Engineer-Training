package com.revature;
import java.util.Arrays;

/**
 * Lab 1 — Arrays & loops. Implement the bodies.
 * See ../README.md
 */
public class ArrayLoopsLab {

    /** Reverse array in place. */
    public static void reverse(int[] data) {
        int end = data.length - 1;
        int start = 0;
        
        while(start != end && start < end) { // account for odd and even list sizes
            int temp = data[start];
            data[start] = data[end];
            data[end] = temp;
            start++;
            end--;
        }
    }

    /** Smallest element; illegal if null or empty. */
    public static int min(int[] data) {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < data.length; i++) {
            if(data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    /** Largest element; illegal if null or empty. */
    public static int max(int[] data) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    /** In-place ascending sort using nested loops only (no Arrays.sort). */
    public static void sortAscending(int[] data) {
        for(int i = 0; i < data.length; i++) {
            for(int j = i; j < data.length; j++) {
                if(data[j] < data[i]) {
                    int temp = data[i];
                    data[i] = data[j];
                    data[j] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        // reverse
        int[] toReverse = {1, 2, 3, 4, 5};
        System.out.println("reverse before: " + Arrays.toString(toReverse));
        reverse(toReverse);
        System.out.println("reverse after:  " + Arrays.toString(toReverse));

        int[] toReverseEven = {1, 2, 3, 4};
        System.out.println("reverse before: " + Arrays.toString(toReverseEven));
        reverse(toReverseEven);
        System.out.println("reverse after:  " + Arrays.toString(toReverseEven));

        System.out.println();

        // min
        int[] nums = {7, 3, 9, -2, 5};
        System.out.println("array: " + Arrays.toString(nums));
        System.out.println("min:   " + min(nums));

        // max
        System.out.println("max:   " + max(nums));

        System.out.println();

        // sortAscending
        int[] toSort = {5, 1, 4, 2, 8, -3};
        System.out.println("sort before: " + Arrays.toString(toSort));
        sortAscending(toSort);
        System.out.println("sort after:  " + Arrays.toString(toSort));
    }
}
