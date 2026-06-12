import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Week 2 Exercise — String analysis (implement TODO methods).
 *
 * Compile: javac TextAnalyzer.java
 * Run: java TextAnalyzer
 */
public class TextAnalyzer {

    // Count words
    public static int wordCount(String text) {
        if(text == null) {
            return 0;
        }
        String[] stringArray = text.split("\\s+");
        return stringArray.length;

    }

    // Recursive palindrome solution
    public static boolean isPalindrome(String token) {
        if(token.length() < 2) {
            return true;
        }

        // Can 
        boolean firstEqualLast = token.toLowerCase().charAt(0) == token.toLowerCase().charAt(token.length() - 1);


        return firstEqualLast && isPalindrome(token.substring(1, token.length() - 1));
    }

    // Count occurences in linear time, one pass through
    public static int countOccurrences(String haystack, String needle) {
        if (haystack == null) {
            return 0;
        }
        int needleOccurences = 0;
        String[] stringArray = haystack.split("\\s+");

        for(int i = 0; i < stringArray.length; i++) {
            if(stringArray[i].equals(needle)) {
                needleOccurences++;
            }
        }

        return needleOccurences;

    }

    public static void main(String[] args) throws IOException {
        Path p = Path.of("sample.txt");
        String body = Files.readString(p);
        System.out.println("words = " + wordCount(body));
        System.out.println("palindrome(Radar) = " + isPalindrome("Radar"));
        System.out.println("occurrences of 'QA' = " + countOccurrences(body, "QA"));
    }
}