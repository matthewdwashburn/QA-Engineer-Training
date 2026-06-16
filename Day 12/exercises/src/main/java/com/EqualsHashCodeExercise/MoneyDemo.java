package com.EqualsHashCodeExercise;

import java.util.HashSet;

public class MoneyDemo {
    public static void main(String[] args) {
        HashSet<Money> moneyMap = new HashSet<>();
        Money m1 = new Money("USD", 1000);
        Money m2 = new Money("USD", 1000);

        // No duplicates
        moneyMap.add(m1);
        moneyMap.add(m2);

        System.out.println("Money Map Size: " + moneyMap.size());
        System.out.println("m1 == m2: " + (m1 == m2));
        System.out.println("m1.equals(m2): " + (m1.equals(m2)));

    }
}
