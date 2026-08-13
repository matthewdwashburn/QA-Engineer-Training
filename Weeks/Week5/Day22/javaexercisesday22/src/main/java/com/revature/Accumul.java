package com.revature;

public class Accumul {
    public static String accum(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(String.valueOf(s.charAt(i)).toUpperCase());
            for (int j = 0; j < i; j++) {
                sb.append(String.valueOf(s.charAt(i)).toLowerCase());
            }
            if (i < s.length() - 1) {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}