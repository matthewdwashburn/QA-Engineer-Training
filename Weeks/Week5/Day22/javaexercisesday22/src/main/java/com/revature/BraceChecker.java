package com.revature;

import java.util.*;

public class BraceChecker {

    public boolean isValid(String braces) {
        Deque<Character> braceStack = new ArrayDeque<>();
        for (int i = 0; i < braces.length(); i++) {
            System.out.println("Index: " + i + " Stack: " + braceStack);
            char c = braces.charAt(i);
            if (c == '(' || c == '{' || c == '[') {
                braceStack.addLast(c);
            } else {
                // If it starts with a close, invalid
                if (braceStack.peek() == null) {
                    return false;
                }
                char top = braceStack.peekLast();
                switch (c) {
                    case ')':
                        if (top != '(') {
                            return false;
                        }
                        braceStack.removeLast();
                        break;
                    case '}':
                        if (top != '{') {
                            return false;
                        }
                        braceStack.removeLast();
                        break;
                    case ']':
                        if (top != '[') {
                            return false;
                        }
                        braceStack.removeLast();
                        break;
                    default:
                        break;
                }
            }
        }
        // If there was only open braces
        if (braceStack.size() != 0) {
            return false;
        }
        return true;
    }

}