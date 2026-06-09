import java.util.Scanner;

public class InputDemo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Your Name : ");

        String name;

        name=sc.next();

        System.out.println("Your name is " + name);

        sc.close();
    }
}
