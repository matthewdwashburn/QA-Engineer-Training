package com.revautre;

public class StringDemo {
    public static void main(String[] args) {
        String s1 = "Hello";
        String s2 = new String("Hello");

        // Need to assign to a new variable to change the string because strings are immutable
        String s11 = s1.concat("World");
        System.out.println(s1);

        System.out.println(s11.length());

        System.out.println(s11.charAt(5));

        // compares s1 and s2 ignoring case, returns boolean (result unused here)
        s1.equalsIgnoreCase(s2);

        s1.toUpperCase();

        // returns a new string with leading/trailing whitespace removed (result unused here)
        System.err.println(s1.trim());

        s1.replace("e", "a");
        s1.contains("11");

        // Nothing changed, immutable
        System.out.println(s1);

        // String buffer can change, mutable
        StringBuffer sBuffer = new StringBuffer("Hello");
        sBuffer.append("World");

        sBuffer.insert(2, "abcd");

        System.err.println(sBuffer);
        
        sBuffer.replace(0, 0, "abc");

        System.out.println();
    }
}
