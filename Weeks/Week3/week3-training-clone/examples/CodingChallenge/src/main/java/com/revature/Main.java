package com.revature;

import java.util.Arrays;
//https://www.codewars.com/kata/515de9ae9dcfc28eb6000001
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        // Create: 1 -> 2 -> 3 -> 4 -> null
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);

        System.out.println("Original:");
        Kata.printList(head);

        Node reversed = Kata.reverse(head);

        System.out.println("Reversed:");
        Kata.printList(reversed);

        System.out.println(Arrays.toString(StringSplit.solution("abc")));
        System.out.println(Arrays.toString(StringSplit.solution("abcdef")));
        System.out.println(Arrays.toString(StringSplit.solution("hello")));
    }
}