package com.revature.exceptions;

public class LostMoneyException extends RuntimeException {

    public LostMoneyException(String msg){
        super(msg);
    }

    public LostMoneyException(){
        super("You just lost some money!");
    }
}
