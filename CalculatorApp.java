import java.util.Scanner;
import javax.swing.SwingUtilities;

// CalculatorApp.java
public class CalculatorApp {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            SwingUtilities.invokeLater(() -> new CalculatorGUI());
            return;
        }
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();
        boolean exit = false;

        printHeader();

        while (!exit) {
            printMenu();
            int choice = getValidChoice(scanner);

            if (choice == 5) {
                System.out.println("\nThank you for using the Simple Calculator. Goodbye!");
                exit = true;
                continue;
            }

            float num1 = getValidFloat(scanner, "Enter the first number: ");
            float num2 = getValidFloat(scanner, "Enter the second number: ");

            try {
                float result = 0;
                switch (choice) {
                    case 1:
                        result = calculator.add(num1, num2);
                        System.out.printf("Result: %.2f + %.2f = %.2f%n", num1, num2, result);
                        break;
                    case 2:
                        result = calculator.subtract(num1, num2);
                        System.out.printf("Result: %.2f - %.2f = %.2f%n", num1, num2, result);
                        break;
                    case 3:
                        result = calculator.multiply(num1, num2);
                        System.out.printf("Result: %.2f * %.2f = %.2f%n", num1, num2, result);
                        break;
                    case 4:
                        result = calculator.divide(num1, num2);
                        System.out.printf("Result: %.2f / %.2f = %.2f%n", num1, num2, result);
                        break;
                    default:
                        System.out.println("Invalid operation selected.");
                        break;
                }
            } catch (ArithmeticException e) {
                System.out.println(e.getMessage());
            }

            pressEnterToContinue(scanner);
        }

        scanner.close();
    }

    public static void printHeader() {
        System.out.println("======================================");
        System.out.println("        Welcome to Simple Calculator   ");
        System.out.println("======================================\n");
    }

    public static void printMenu() {
        System.out.println("Select an operation to perform:");
        System.out.println("--------------------------------");
        System.out.println("1. Addition (+)");
        System.out.println("2. Subtraction (-)");
        System.out.println("3. Multiplication (*)");
        System.out.println("4. Division (/)");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }

    public static int getValidChoice(Scanner scanner) {
        int choice = 0;
        while (true) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 5) {
                    break;
                } else {
                    System.out.print("Invalid choice! Please enter a number between 1 and 5: ");
                }
            } else {
                System.out.print("Invalid input! Please enter a number between 1 and 5: ");
                scanner.next(); // Clear invalid input
            }
        }
        return choice;
    }

    public static float getValidFloat(Scanner scanner, String prompt) {
        float num = 0;
        System.out.print(prompt);
        while (true) {
            if (scanner.hasNextFloat()) {
                num = scanner.nextFloat();
                break;
            } else {
                System.out.print("Invalid input! Please enter a valid number: ");
                scanner.next(); // Clear invalid input
            }
        }
        return num;
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine(); // Consume newline left-over
        scanner.nextLine(); // Wait for user to press Enter
        System.out.println();
    }
}
