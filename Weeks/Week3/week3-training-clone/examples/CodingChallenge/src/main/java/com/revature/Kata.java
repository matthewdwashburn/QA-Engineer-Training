package com.revature;

//https://www.codewars.com/kata/57262ca48565846f33001365

public class Kata {
    public static Node reverse(Node node) {
        Node prev = null;
        Node current = node;

        while (current != null) {
            Node next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        return prev;
    }

    public static void printList(Node head) {
        Node current = head;

        while (current != null) {
            System.out.print(current.value);

            if (current.next != null) {
                System.out.print(" -> ");
            }

            current = current.next;
        }

        System.out.println(" -> null");
    }
}
