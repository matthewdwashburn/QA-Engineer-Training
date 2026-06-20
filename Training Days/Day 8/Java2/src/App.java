public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        System.err.println(greet("Jacob"));
    }

    static String greet(String name) {
        return "Hello, " + name + "!";
    }
}

