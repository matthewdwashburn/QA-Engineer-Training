package com.revature;

public class ReverseLinkedList {
  public static Node reverse(Node node) {   
    // Initialize head, next, and prev
    Node curr = node;
    Node next = null;
    Node prev = null;
    while(curr != null) {
      // Move next pointer
      next = curr.next;
      // Point current node at previous node
      curr.next = prev;
      // Move prev pointer to current node 
      prev = curr;
      // Move current node to next node
      curr = next;
    }

    // Return the last node, now the head of the reversed linked list
    return prev;
  }

  public static void main(String[] args) {
    Node head = new Node(1);
    head.next = new Node(2);
    head.next.next = new Node(3);
    head.next.next.next = new Node(4);
    head.next.next.next.next = new Node(5);

    Node reverse = reverse(head);

    while(reverse != null) {
      System.out.println(reverse.data);
      reverse = reverse.next;
    }

  }
}

