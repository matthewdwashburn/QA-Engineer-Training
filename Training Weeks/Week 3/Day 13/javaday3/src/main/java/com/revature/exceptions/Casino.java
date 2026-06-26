package com.revature.exceptions;

public class Casino {
    public static void main(String[] args) {
        double luck = Math.random();
        System.out.println(luck);

        try {
            gamble(luck);
        } catch (LostMoneyException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    public static void gamble(double number) throws LostMoneyException {
        if(number > 0.5) {
            System.out.println("Congrats, you doubled your money!");
        } else {
            throw new LostMoneyException("You lost all your money!");
        }
    }
    
}
