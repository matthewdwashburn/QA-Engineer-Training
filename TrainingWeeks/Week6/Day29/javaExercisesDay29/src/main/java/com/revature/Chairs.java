package com.revature;

public class Chairs {
    public static int lastChair(int n) {
        // Array to keep track of which chairs are taken by which person
        int[] chairs = new int[n + 1];
        for (int i = 1; i < n + 1; i++) { // Iterate through people
            // Skip the first two people to avoid edge cases, same behavior on every n greater than 2
            if(i == 1) {
                chairs[1] = 1;

                continue;
            }
            if(i == 2) {
                chairs[n] = 2;
                continue;
            }
            int maxDistanceFromPeople = Integer.MIN_VALUE;
            int minDistanceFromExit = Integer.MAX_VALUE;
            int currentLeadingChair = 1;
            for (int j = 1; j < n + 1; j++) { // Iterate through all chairs
                if (chairs[j] == 0) { // Make sure chair isn't already taken
                    // Find distance from people on the left
                    int distanceFromPeopleLeft = 0;
                    for (int k = j; k > 0; k--) {
                        if (chairs[k] == 0) { // Nobody in this next chair
                            distanceFromPeopleLeft++;
                        } else {
                            break;
                        }
                    }
                    // Find distance from people on the right
                    int distanceFromPeopleRight = 0;
                    for (int k = j; k < n + 1; k++) {
                        if (chairs[k] == 0) { // Nobody in this next chair
                            distanceFromPeopleRight++;
                        } else {
                            break;
                        }
                    }
                    // Max distance from people, take the minimum of the two
                    int maxPeopleDistance = distanceFromPeopleLeft < distanceFromPeopleRight ? distanceFromPeopleLeft
                            : distanceFromPeopleRight;
                    
                    if (maxPeopleDistance > maxDistanceFromPeople) {
                        maxDistanceFromPeople = maxPeopleDistance;
                        currentLeadingChair = j;

                        // Check if they are also the closest to the exit
                        int distanceFromExit = j;
                        if (distanceFromExit < minDistanceFromExit) {
                            minDistanceFromExit = distanceFromExit;
                        }
                        continue;
                    } else if (maxPeopleDistance == maxDistanceFromPeople) { 
                        // Tie on distance from people, tiebreaker distance from exit
                        int distanceFromExit = j;
                        if (distanceFromExit < minDistanceFromExit) {
                            minDistanceFromExit = distanceFromExit;
                            currentLeadingChair = j;
                        } else {
                            continue; // Tying distance from people chair is closer
                        }
                    } else {
                        // Another chair is further from people
                        continue;
                    }
                } else {
                    continue; // Chair is taken
                }
            }
            // Person i sits on the winning chair index
            chairs[currentLeadingChair] = i;
        }
        // Find the last paitent's chair number
        for(int i = 1; i < n + 1; i++) {
            if(chairs[i] == n) {
                return i;
            }
        }
        return -1;
    }
}
