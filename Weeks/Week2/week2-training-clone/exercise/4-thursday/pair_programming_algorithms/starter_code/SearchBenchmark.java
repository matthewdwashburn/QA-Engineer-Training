/**
 * Pair exercise — build sorted array, pick target, time both searches.
 * TODO: complete main after SearchLib is implemented.
 */
public class SearchBenchmark {

    public static void main(String[] args) {
        // TODO: size N, fill sorted even integers, pick target, time SearchLib.linearSearch vs binarySearch
        System.out.println("Implement benchmark");
    }

    static int[] buildSortedEvens(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i * 2;
        }
        return arr;
    }
}
