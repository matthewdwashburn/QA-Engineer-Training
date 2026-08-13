package com.revature.collections;

import java.util.List;
import java.util.Scanner;

public class LinkedVsArrayVsVector {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Choose list type: ArrayList(1), LinkedList(2), Vector(3)");
        int number = scan.nextInt();

        while (number != 0) {

            // Add to End
            List<Object> things = ListFactory.getList(number);

            long start = System.nanoTime();
            for (int i = 0; i < 100000; i++) {
                things.add(new Object());
            }
            long end = System.nanoTime();
            System.out.println("Add to end: " + (end - start) / 1_000_000 + "ms");

            // Add to Front
            things = ListFactory.getList(number);

            start = System.nanoTime();
            for (int i = 0; i < 100000; i++) {
                things.add(0, new Object());
            }
            end = System.nanoTime();
            System.out.println("Add to front: " + (end - start) / 1_000_000 + "ms");

            // Index GET
            things = ListFactory.getList(number);

            // fill list for read/remove tests
            for (int i = 0; i < 200000; i++) {
                things.add(0, new Object());
            }

            start = System.nanoTime();
            for (int i = 0; i < 100000; i++) {
                things.get(i);
            }
            end = System.nanoTime();
            System.out.println("Random access get(): " + (end - start) / 1_000_000 + "ms");

            // ForEACH Traversal

            start = System.nanoTime();
            for (@SuppressWarnings("unused") Object o : things) {
                // just iterate
            }
            end = System.nanoTime();
            System.out.println("Iterator traversal(): " + (end - start) / 1_000_000 + "ms");

            // Remove FRONT

            start = System.nanoTime();
            for (int i = 0; i < 100000; i++) {
                things.remove(i);
            }
            end = System.nanoTime();
            System.out.println("Remove From Front: " + (end - start) / 1_000_000 + "ms");

            System.out.println("\nTry another: 1 ArrayList, 2 LinkedList, 3 Vector, 0 quit");

            number = scan.nextInt();
        }
        scan.close();
    }
}