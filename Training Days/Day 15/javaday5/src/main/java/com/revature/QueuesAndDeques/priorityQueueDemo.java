package com.revature.QueuesAndDeques;

import java.util.PriorityQueue;
import java.util.Queue;

public class priorityQueueDemo {
    public static void main(String[] args) {
        Queue<Task> tasks = new PriorityQueue<>();

        tasks.add(new Task("Fix bug", 1));
        tasks.add(new Task("Write documenation" ,5));
        tasks.add(new Task("Update Website" ,3));

        while(!tasks.isEmpty()) {
            System.out.println(tasks.poll());
        }

    }
}
