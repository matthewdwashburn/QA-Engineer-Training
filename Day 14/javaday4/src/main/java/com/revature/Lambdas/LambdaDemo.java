package com.revature.Lambdas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaDemo {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Bob", "Lisa", "Charlie", "Jonathan", ""));

        // Functional interfaces include predicate, function, consumer, and supplier

        // Predicate represents a condition that returns true or false
        // Predicate for filtering
        Predicate<String> longWord = s->s.length()>5;
        System.out.println(longWord.test("elephant"));

        // Functional interface takes input, transforms something, and returns a value
        // Functional for transforming
        Function<String, Integer> getLength = s-> s.length();
        System.out.println(getLength.apply("Java"));

        // Consumer interface takes a value and does something with it, but returns nothing
        // Use/consume something
        Consumer<String> printer = s -> System.out.println(s);
        printer.accept("Hello");

        //Supplier produces a value but takes no input
        //produce something
        Supplier<Double> randomNumber = () -> Math.random();
        System.out.println(randomNumber.get());

        Function<String,String> upper = name -> name.toUpperCase();

        // Filter out strings less than 5 characters, map them to upper case, and print them
        names.stream().filter(longWord).map(upper).forEach(printer);

        // names.removeIf(s->s.isBlank());
        // names.forEach(printer);

        names.removeIf(String::isBlank);
        names.forEach(printer);

    }
}