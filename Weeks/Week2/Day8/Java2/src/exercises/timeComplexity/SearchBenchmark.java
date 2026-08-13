package exercises.timeComplexity;
import java.util.Random;

/**
 * Pair exercise — build sorted array, pick target, time both searches.
 */
public class SearchBenchmark {

    public static void main(String[] args) {
        // Create sorted evens
        int n = 1_000_000_000;
        int[] sortedEvens = buildSortedEvens(n);

        // Get random target
        Random rand = new Random();
        int randomTarget = rand.nextInt(n);

        // Calculate execution time linear
        long startTimeLinear = System.nanoTime();
        SearchLib.linearSearch(sortedEvens, randomTarget);
        long endTimeLinear = System.nanoTime();
        long linearDurationInNanoseconds = endTimeLinear - startTimeLinear;
        double linearDurationInMilliseconds = linearDurationInNanoseconds / 1_000_000.0;

        // Calculate execution time binary
        long startTimeBinary = System.nanoTime();
        SearchLib.binarySearch(sortedEvens, randomTarget);
        long endTimeBinary = System.nanoTime();
        long binaryDurationInNanoseconds = endTimeBinary - startTimeBinary;
        double binaryDurationInMilliseconds = binaryDurationInNanoseconds / 1_000_000.0;

        // Display execuation duration for linear and binary
        System.out.println("Linear Search Duration in ms for " + n + " numbers: " + linearDurationInMilliseconds);
        System.out.println("Binary Search Duration in ms for " + n + " numbers: " + binaryDurationInMilliseconds);

    }

    static int[] buildSortedEvens(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i * 2;
        }
        return arr;
    }
}