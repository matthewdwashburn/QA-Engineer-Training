package org.example;

public class MethodRecursion {

    public static int fib(int n){
        //base cases
        if (n==0) return 0;
        if (n==1) return 1;

        //recursive case
        return fib(n-1 ) + fib(n-2);
    }
}
