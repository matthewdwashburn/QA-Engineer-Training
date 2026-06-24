package com.revature;

public class StringSplit {
    public static String[] stringSplit(String s) {
        boolean odd = false;
        int arrLength = 0;
        if ((s.length() % 2) == 0) {
            arrLength = s.length() / 2;
        } else {
            arrLength = (s.length() / 2) + 1;
            odd = true;
        }
        String[] stringArr = new String[arrLength];
        for (int i = 0; i < arrLength; i++) {
            if (!odd) { // even
                String firstLetter = String.valueOf(s.charAt(i * 2));
                String secondLetter = String.valueOf(s.charAt((i * 2) + 1));
                stringArr[i] = firstLetter + secondLetter;
            } else { // odd
                if ((i * 2) + 1 < s.length()) { // not last index
                    String firstLetter = String.valueOf(s.charAt(i * 2));
                    String secondLetter = String.valueOf(s.charAt((i * 2) + 1));
                    stringArr[i] = firstLetter + secondLetter;
                } else { // last index
                    stringArr[i] = String.valueOf(s.charAt(i * 2)) + "_";
                }
            }
        }
        return stringArr;
    }
    // Test string split
    public static void main(String[] args) {
        String oddString = "abcdef";
        String evenString = "abcde";
        String[] splitOdd = stringSplit(oddString);
        String[] splitEven = stringSplit(evenString);
        
        // Print odd split
        for(int i = 0; i < splitOdd.length; i++) {
            System.out.println("Odd index " + i + ": " + splitOdd[i]);
        }

        // Print even split
        for (int i = 0; i < splitEven.length; i++) {
            System.out.println("Even index " + i + ": " + splitEven[i]);
        }

    }
}
