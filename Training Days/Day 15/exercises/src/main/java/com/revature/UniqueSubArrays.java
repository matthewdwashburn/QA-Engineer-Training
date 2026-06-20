package com.revature;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;

public class UniqueSubArrays {
    public static void main(String[] args) {
            Scanner in = new Scanner(System.in);
            Deque<Integer> deque = new ArrayDeque<>();
            
            // Get n and m values
            int n = in.nextInt();
            int m = in.nextInt();
            
            // Create variable to track max unique nums out of all deque windows
            int maxUniqueCount = 0;

            // Create map to keep track of how many unique nums in each window
            HashMap<Integer, Integer> uniqueMap = new HashMap<>();
            
            // Iterate through all n numbers
            for (int i = 0; i < n; i++) {
                // Get new number
                int num = in.nextInt();
                
                // Add new number to back of deque
                deque.addLast(num);
                
                // Add new number to map
                if(uniqueMap.containsKey(num)){
                    uniqueMap.put(num, uniqueMap.get(num) + 1);
                } else {
                    uniqueMap.put(num, 1);
                }
                
                // Remove front if deque too big
                if(deque.size() > m) {
                    //Remove from the front
                    int removedFront = deque.pollFirst();
                    
                    // Update frequency count of num in map
                    if(uniqueMap.containsKey(removedFront)) {
                        int currCount = uniqueMap.get(removedFront);
                        uniqueMap.put(removedFront, currCount - 1);
                    }
                    
                    //If number map key is empty, remove it
                    if(uniqueMap.get(removedFront) <= 0) {
                        uniqueMap.remove(removedFront);
                    }
                }

                // If we are in a correctly sized new window
                if(deque.size() == m) {
                    // Check if there's a new max unique count
                    if(maxUniqueCount < uniqueMap.size()) {
                        maxUniqueCount = uniqueMap.size();
                    }
                }
            }
            System.out.println(maxUniqueCount);
            in.close();
        }
}

/*
 * Sample Input
 * 
 * 6 3
 * 5 3 5 2 3 2
 * 
 * Sample Output
 * 
 * 3
 * 
 */