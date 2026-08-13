package com.revature;

public class StringUtils {

    public static String reverse(String input) {
        if (input == null)
            return null;
        return new StringBuilder(input).reverse().toString();
    }

    public static boolean isEmpty(String input) {
        return input == null || input.length() == 0;
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().length() == 0;
    }

    public static String findFirst(String[] items, String prefix) {
        if (items == null)
            return null;
        for (String item : items) {
            if (item != null && item.startsWith(prefix)) {
                return item;
            }
        }
        return null;
    }

    public static String[] split(String input, String delimiter) {
        if (input == null)
            return new String[0];
        return input.split(delimiter);
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty())
            return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static User parseUser(String csv) {
        String[] parts = csv.split(",");
        return new User(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
    }
}