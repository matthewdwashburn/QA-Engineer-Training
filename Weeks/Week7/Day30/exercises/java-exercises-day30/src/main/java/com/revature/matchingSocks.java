package com.revature;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.toList;

class Result1 {

    /*
     * Complete the 'sockMerchant' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER n
     * 2. INTEGER_ARRAY ar
     */

    public static int sockMerchant(int n, List<Integer> ar) {
        HashMap<Integer, Integer> sockMap = new HashMap<>();
        // Count how many socks of each color
        for (int i = 0; i < n; i++) {
            int sockNum = ar.get(i);
            if (sockMap.containsKey(sockNum)) {
                sockMap.put(sockNum, sockMap.get(sockNum) + 1);
            } else {
                sockMap.put(sockNum, 1);
            }
        }
        // Count how many matching pairs in sock color groups
        int matchingPairs = 0;
        for (int count : sockMap.values()) {
            matchingPairs += count / 2;
        }
        return matchingPairs;
    }

}

public class matchingSocks {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        List<Integer> ar = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                .map(Integer::parseInt)
                .collect(toList());

        int result = Result1.sockMerchant(n, ar);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
