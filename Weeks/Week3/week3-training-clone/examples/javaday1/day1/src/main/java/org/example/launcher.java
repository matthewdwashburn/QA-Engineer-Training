package org.example;
import java.util.stream.IntStream;

public class launcher {
    public static void main(String[] args) {
            DemoClassesObjects.Student a = new DemoClassesObjects.Student("Asha");
            DemoClassesObjects.Student b = new DemoClassesObjects.Student("Ben");
            System.out.println(a);
            System.out.println(b);

            System.out.println("totalStudents (static):" + DemoClassesObjects.Student.getTotalStudents());
            DemoClassesObjects.Student c = new DemoClassesObjects.Student("Chen");
            System.out.println("identity a==c ? " + a.equals(c));

        System.out.println("Fibonacci example");
        int n = 7;
        int[] arr = IntStream.range(1,n).toArray();
//        System.out.println((MethodRecursion.fib(6)));
        for (int number: arr){
            System.out.println((MethodRecursion.fib(number)));
        }
        }
    }

