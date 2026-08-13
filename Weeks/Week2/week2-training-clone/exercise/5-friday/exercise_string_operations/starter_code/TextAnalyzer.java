import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Week 2 Exercise — String analysis (implement TODO methods).
 *
 * Compile: javac TextAnalyzer.java
 * Run:     java TextAnalyzer
 */
public class TextAnalyzer {

    public static int wordCount(String text) {
        throw new UnsupportedOperationException("TODO");
    }

    public static boolean isPalindrome(String token) {
        throw new UnsupportedOperationException("TODO");
    }

    public static int countOccurrences(String haystack, String needle) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) throws IOException {
        Path p = Path.of("sample.txt");
        String body = Files.readString(p);
        System.out.println("words=" + wordCount(body));
        System.out.println("palindrome(Radar)=" + isPalindrome("Radar"));
        System.out.println("occurrences of 'QA'=" + countOccurrences(body, "QA"));
    }
}
