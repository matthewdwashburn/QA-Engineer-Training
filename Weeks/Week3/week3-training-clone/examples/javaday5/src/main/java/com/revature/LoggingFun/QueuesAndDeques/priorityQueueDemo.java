package com.revature.LoggingFun.QueuesAndDeques;

import java.util.PriorityQueue;
import java.util.Queue;

public class priorityQueueDemo {

    public static void main(String[] args) {
        Queue<Task> tasks = new PriorityQueue<>();

        tasks.add(new Task("Fix production bug",1));
        tasks.add(new Task("Write Documentation", 5));
        tasks.add(new Task("Update Website", 3));

        while (!tasks.isEmpty()){
            System.out.println(tasks.poll());
        }

    }
}
