package com.revature;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class Result {

    /*
     * Complete the 'icecreamParlor' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     * 1. INTEGER m
     * 2. INTEGER_ARRAY arr
     */

    public static List<Integer> icecreamParlor(int m, List<Integer> arr) {
        int n = arr.size();
        List<Integer> flavorPrices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Cant add yourself
                if (j == i)
                    continue;
                // I and j add up to m
                if (arr.get(j) + arr.get(i) == m) {
                    System.out.println("Found a sum that works: "
                            + arr.get(j) + " + " + arr.get(i) + " = " + m);
                    System.out.println("i: " + i + ", " + "j: " + j);
                    // Add i and j in ascending order
                    if (j < i) {
                        flavorPrices.add(j + 1);
                        flavorPrices.add(i + 1);
                        return flavorPrices;
                    } else {
                        flavorPrices.add(i + 1);
                        flavorPrices.add(j + 1);
                        return flavorPrices;
                    }
                }
            }
        }
        return flavorPrices;
    }
}

public class iceCreamFlavors {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int t = Integer.parseInt(bufferedReader.readLine().trim());

        IntStream.range(0, t).forEach(tItr -> {
            try {
                int m = Integer.parseInt(bufferedReader.readLine().trim());

                List<Integer> arr = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                        .map(Integer::parseInt)
                        .collect(toList());

                List<Integer> result = Result.icecreamParlor(m, arr);

                bufferedWriter.write(
                        result.stream()
                                .map(Object::toString)
                                .collect(joining(" "))
                                + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        bufferedReader.close();
        bufferedWriter.close();
    }
}
