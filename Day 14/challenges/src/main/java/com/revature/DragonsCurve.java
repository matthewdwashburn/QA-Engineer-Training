package com.revature;

import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class DragonsCurve {

    // Make the function; map the chars to Strings
    // a -> aRbFR, b -> LFaLb, otherwise -> itself
    public IntFunction<String> mapFunction = s -> { 
        if(s == 'a') return "aRbFR";
        if(s == 'b') return "LFaLb";
        return String.valueOf((char) s);
    }; 

    /**
     * Make the curve; stream the chars repeatedly (starting with Fa) through the
     * mapFunction n times
     * Then remove the a and b (createFilter function is useful for that)
     */
    public String createCurve(int n) {
        StringBuilder curve = new StringBuilder("Fa");
        for (int i = 0; i < n; i++) {
            curve = new StringBuilder(curve.chars().mapToObj(mapFunction).collect(Collectors.joining()));
        }
        // Remove remaining a and b chars
        String cleanCurve = curve.chars()
        .filter(createFilter('a', false))
        .filter(createFilter('b', false))
        .mapToObj(c -> String.valueOf((char) c))
        .collect(Collectors.joining());
        return cleanCurve;
    }

    /**
     * How many of the specified char are in the given curve?
     * Hint: createFilter could be useful for this
     */
    public long howMany(char c, String curve) {
        long count = curve.chars().filter(createFilter(c, true)).toArray().length;
        return count; // Determined by die roll; guaranteed to be random
    }

    /**
     * Create a predicate to filter the specified char; keep or remove based on keep
     * variable
     */
    public IntPredicate createFilter(char filterWhat, boolean keep) {
    
        IntPredicate f = c -> {if(c == filterWhat) {
            return keep;
        } else {
            return !keep;
        }
        };
        return f;
    }
    
}
