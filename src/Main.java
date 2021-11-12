import java.io.IOException;

public class Main
{
    private static void printMenu()
    {
        System.out.println("============");
        System.out.println("0. Exit");
        System.out.println("1. Print states");
        System.out.println("2. Print alphabet");
        System.out.println("3. Print starting symbol");
        System.out.println("4. Print final states");
        System.out.println("5. Print transitions");
        System.out.println("6. Check DFA");
        System.out.println("============");
    }

    public static void main(String[] args) throws IOException
    {
        Scanner scanner = new Scanner();
        scanner.scan("p3.orange");

        FA fa = new FA();
        fa.readFA("test.in");

        java.util.Scanner keyboard = new java.util.Scanner(System.in);

        printMenu();

        String input = keyboard.next();
        while (!input.equals("0"))
        {
            fa.printFA(input);

            printMenu();

            input = keyboard.next();
        }
    }
}
