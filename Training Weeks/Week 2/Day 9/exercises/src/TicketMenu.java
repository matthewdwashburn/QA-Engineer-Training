import java.util.Scanner;

/**
 * Week 2 Exercise — menu-driven console (implement the menu loop).
 *
 * Compile: javac TicketMenu.java
 * Run: java TicketMenu
 */
public class TicketMenu {

    public static void main(String[] args) {
        String[] tickets = { "BUG-101 Login timeout", "BUG-102 CSV import", "BUG-103 flaky assertion" };
        int[] priorities = { 2, 2, 2 }; // stretch: update in menu option 2

        try (Scanner in = new Scanner(System.in)) {
            outer:
            while (true) {
                System.out.println("Please choose a valid option: 1 = list ticket, 2 = add priority, 3 = summary, 4 = quit");
                switch (in.next()) {
                    case "1":
                        for (int i = 0; i < tickets.length; i++) {
                            System.out.println(tickets[i]);
                        }
                        break;
                    case "2":
                        System.out.print("Please Enter Ticket Index: ");
                        int index = Integer.parseInt(in.next());

                        while (index < 0 || index > priorities.length - 1) {
                            System.out.print("Please Ensure priority is between 0-" + (priorities.length - 1) + ": ");
                            index = Integer.parseInt(in.next());
                        }

                        System.out.print("Please Enter Priority: ");
                        int priority = Integer.parseInt(in.next());

                        while (priority < 1 || priority > 3) {
                            System.out.print("Please Ensure index is between 1-3: ");
                            priority = Integer.parseInt(in.next());
                        }

                        priorities[index] = priority;
                        System.out.println("Priority updated successfully!");
                        break;
                    case "3":
                        System.out.println("There are currently " + tickets.length + " tickets");
                        break;
                    case "4":
                        System.out.println("Goodbye!");
                        break outer;
                    default:
                }
            }
        }
    }

}