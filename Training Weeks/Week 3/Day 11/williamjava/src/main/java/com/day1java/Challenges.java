package com.day1java;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Challenges {

    public static void main(String[] args) {
        int[] values = {1, 2, 3, 4, 5};
        System.out.println(Arrays.toString(beggars(values, 1)));
    }

    public static int setReducer(int[] inputArray) {

        int[] currentArray = inputArray;

        while(currentArray.length > 1) {
            currentArray = reduceOnce(currentArray);
        }

        return currentArray[0];
        
    }

    public static int[] reduceOnce(int[] arr) {
        ArrayList<Integer> arrList = new ArrayList<>();

        int repeatCount = 1;
        for(int i = 1; i < arr.length; i++) {
            // Only look behind, start a new count if behind doesn't match
            if(arr[i] == arr[i - 1]) {
                repeatCount++;
            } else {
                arrList.add(repeatCount);
                repeatCount = 1;
            }
        }
        // Add final count
        arrList.add(repeatCount);

        // Convert array list to array, important
        return arrList.stream().mapToInt(Integer::intValue).toArray();  
    }

    public static int[] beggars(int[] values, int n) {
        // Array list for the sum 
        int[] beggarSumArr = new int[n];

        // Make an array list for each beggar
        for(int i = 0; i < n; i++) { // Start at the current beggar index
            // Sum up this beggar's share
            int currentBeggarSum = 0;

            // Add this beggar's share
            for(int j = i; j < values.length; j += n) {
                currentBeggarSum += values[j];
            }

            // Add beggar's sum to final sum array
            beggarSumArr[i] = currentBeggarSum;
        }
        return beggarSumArr;
    }

    public static List<String> maxMatch(String sentence) {
        // Create return list
        ArrayList<String> returnList = new ArrayList<>();

        // Make mutable copy of sentence
        StringBuilder currentSentence = new StringBuilder(sentence);

        while(currentSentence.length() > 0) {
            // Find the longest string
            String longest = maxMatchHelper(currentSentence.toString());
            // Add it
            returnList.add(longest);
            // Delete longest word
            currentSentence.delete(0, longest.length());
        }
        return returnList;

    }

    public static String maxMatchHelper(String sentence) {
        // Find the longest valid word
        int max = 0;
        StringBuilder maxString = new StringBuilder();

        // Find all the valid words in this sentence
        for (int i = 1; i <= sentence.length(); i++) {
            String currentSlice = sentence.substring(0, i);
            if (Preloaded.VALID_WORDS.contains(currentSlice)) {
                if(currentSlice.length() > max) {
                    max = currentSlice.length();
                    maxString.setLength(0);
                    maxString.append(currentSlice);
                }
            }
        }
        String longestString = maxString.toString();

        // If none with found, return the first element of the sentence
        if (longestString.isEmpty()) {
            return String.valueOf(sentence.charAt(0));
        }

        return longestString;
    }


}
