package com.revature;

import java.util.function.IntUnaryOperator;

public class AdderFactory {
    public static IntUnaryOperator create(int addTo) {
        IntUnaryOperator f = num -> num + addTo;
        return f;
    }

}