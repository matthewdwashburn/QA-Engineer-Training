package com.revature;

public class InvalidNameException extends Exception {
    public InvalidNameException(String message) {
        super(message);
    }

    public static void main(String[] args) throws InvalidNameException {
        throw new InvalidNameException("Invalid Name!!");
    }
}