package com.revature;
import java.io.*;
import java.util.*;

class Result2 {

    /*
     * Complete the 'isValid' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts STRING s as parameter.
     */

    public static String isValid(String s) {
        HashMap<Character, Integer> charMap = new HashMap<>();
        // Store all the char counts
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (charMap.containsKey(currChar)) {
                charMap.put(currChar, charMap.get(currChar) + 1);
            } else {
                charMap.put(currChar, 1);
            }
        }
        // Track how many char counts there are
        HashSet<Integer> charValSet = new HashSet<>(charMap.values());
        if (charValSet.size() > 2) {
            return "NO";
        } else if (charValSet.size() == 2) {
            List<Integer> charCountSetList = new ArrayList<>(charValSet);
            // 2 counts, multiple counts with more than 1 char
            int firstCount = 0;
            int secondCount = 0;
            for (Map.Entry<Character, Integer> entry : charMap.entrySet()) {
                int value = entry.getValue();
                if (value == charCountSetList.get(0))
                    firstCount++;
                if (value == charCountSetList.get(1))
                    secondCount++;
            }
            // Check how many of each count there is
            if (firstCount > 1 && secondCount > 1) {
                return "NO";
            }
            // Check the difference in counts
            int difference = Math.abs(charCountSetList.get(0) - charCountSetList.get(1));

            if (difference > 1 && (charCountSetList.get(0) > 1 || firstCount > 1) &&
                    (charCountSetList.get(1) > 1 || secondCount > 1)) {
                return "NO";
            }
        }
        return "YES";
    }

}

public class validString {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String s = bufferedReader.readLine();

        String result = Result2.isValid(s);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}