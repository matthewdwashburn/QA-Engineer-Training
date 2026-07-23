package com.revature;

import java.util.List;
import java.util.ArrayList;

public class RemoveElementFromArray {
    public static int[][] selectSubarray(final int[] arr) {
        // Set length
        int length = arr.length;
        // Create result 2d array list
        List<int[]> resultList = new ArrayList<>();
        // Fill q value array
        double[] subArrayAbsQ = new double[length];
        for (int i = 0; i < length; i++) {
            int subArraySum = 0;
            double subArrayProd = 1; // Be careful of wrong data types, can really mess you up
            for (int j = 0; j < length; j++) {
                if (j == i)
                    continue;
                subArraySum += arr[j];
                subArrayProd *= arr[j];
            }
            double subArrayQ = Math.abs(subArrayProd / Double.valueOf(subArraySum));
            subArrayAbsQ[i] = subArrayQ;
        }
        // Find smallest |q| value
        double lowestQ = Double.MAX_VALUE;
        for (int i = 0; i < length; i++) {
            if (subArrayAbsQ[i] < lowestQ)
                lowestQ = subArrayAbsQ[i];
        }
        // Check for duplicate smallest |q|
        for (int i = 0; i < length; i++) {
            if (subArrayAbsQ[i] == lowestQ) {
                int[] tempList = { i, arr[i] };
                resultList.add(tempList);
            }
        }
        // Create final resulting 2d array based on size of list
        int[][] result = new int[resultList.size()][2];
        for (int i = 0; i < resultList.size(); i++) {
            result[i] = resultList.get(i);
        }
        return result;
    }
}