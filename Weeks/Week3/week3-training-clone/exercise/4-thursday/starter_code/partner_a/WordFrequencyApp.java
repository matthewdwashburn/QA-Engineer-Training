import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Partner A — word counts + sorted unique words.
 * See ../../README.md
 */
public class WordFrequencyApp {

    static final String SAMPLE = """
            Java collections maps sets queues lambdas
            Java maps and sets and more Java
            """;

    public static void main(String[] args) {
        Map<String, Integer> counts = new HashMap<>();
        // TODO: tokenize SAMPLE, populate counts (lower-case tokens)

        TreeSet<String> vocabulary = new TreeSet<>();
        // TODO: add all distinct words to vocabulary

        System.out.println("TODO: print counts and top N");
        System.out.println("TODO: print first/last of vocabulary");
    }
}
