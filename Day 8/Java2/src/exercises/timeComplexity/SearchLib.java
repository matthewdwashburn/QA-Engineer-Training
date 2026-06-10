package exercises.timeComplexity;

/**
 * Pair exercise — implement linearSearch and binarySearch.
 * Precondition: sorted ascending, may contain duplicates; return any matching index.
 */
public class SearchLib {

    public static int linearSearch(int[] sorted, int target) {
        for(int i = 0; i < sorted.length; i++) {
            if(sorted[i] == target) {
                return i;
            }
        }
        return -1;
    }

    public static int binarySearch(int[] sorted, int target) {
        int low = 0;
        int high = sorted.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if(sorted[mid] == target) {
                return mid;
            }
            // Target is greater, farther down the list, snip the low half
            if(sorted[mid] < target) {
                low = mid + 1;
            }
            // Target is less, farther up the list, snip the high half
            if(sorted[mid] > target) {
                high = mid - 1;
            }
        }
        return -1;
    }
}