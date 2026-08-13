package com.revature.Lambdas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class lambdaDemo {
    public static void main(String[] args) {

//        List<String> names = List.of("Bob","Alice","Charlie", " ");
        List<String> names = new ArrayList<>(List.of(" ","Bob","Alice","Charlie", " "));

        //Functional Interfaces include Predicate, Function, Consumer, and Supplier

        //Predicate  represents a condition that returns true or false
        //Predicate for filtering
        Predicate<String> longWord = s->s.length()>4;
        System.out.println(longWord.test("elephant"));

        //Function Interface takes input, transforms something, and returns a value
        //Function for transforming
        Function<String, Integer> getLength = s->s.length();
        System.out.println(getLength.apply("Java"));

        //Consumer Interface takes a value and does something with it, but returns nothing
        //Use/consume something
        Consumer<String> printer = s-> System.out.println(s);
        printer.accept("Hello");

        //Supplier produces a value but takes no input
        //produce something
        Supplier<Double> randomNumber = ()->Math.random();
        System.out.println(randomNumber.get());

        Function<String,String> upper = name ->name.toUpperCase();

        names.stream().filter(longWord).map(upper).forEach(printer);


//        names.removeIf(s->s.isBlank());
//        names.forEach(printer);

        //this lambda can be rewritten with a method reference
        names.removeIf(String::isBlank);
        names.forEach(printer);

    }
}
