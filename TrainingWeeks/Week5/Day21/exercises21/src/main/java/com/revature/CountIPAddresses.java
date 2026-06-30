package com.revature;

import java.util.*;

public class CountIPAddresses {

    public static long ipsBetween(String start, String end) {
        String[] split_start_string = start.split("\\.");
        int[] split_start = Arrays.stream(split_start_string).mapToInt(Integer::parseInt).toArray();

        String[] split_end_string = end.split("\\.");
        int[] split_end = Arrays.stream(split_end_string).mapToInt(Integer::parseInt).toArray();

        long start_total = (split_start[0] * ((long) Math.pow(256, 3))) + (split_start[1] * ((long) Math.pow(256, 2)))
                + (split_start[2] * 256) + split_start[3];
        long end_total = (split_end[0] * ((long) Math.pow(256, 3))) + (split_end[1] * ((long) Math.pow(256, 2)))
                + (split_end[2] * 256) + split_end[3];

        long total_difference = start_total - end_total;

        if (total_difference < 0) {
            total_difference *= -1;
        }

        return total_difference;

    }
}
