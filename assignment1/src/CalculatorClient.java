import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.rmi.NotBoundException;

/* The Client class is used to test the Calculator RMI service
 * pushValue, pushOperation, pop, delayPop, getLastResult methods are used here
 */

public class CalculatorClient {
    public static void main(String[] args) {
        try {
            // Lookup the remote Calculator object from the RMI registry
            // and connect to remote Calculator object
            Calculator calculator = (Calculator) Naming.lookup("rmi://localhost:1099/calc");

            // scanner for getting user input
            Scanner scan = new Scanner(System.in);
            System.out.print("===== Connected to Calculator RMI server! =====\n");

            boolean running = true;
            // give the user choices for operations
            while (running) {
                System.out.println("Calculator options: ");
                System.out.println("1. push value");
                System.out.println("2. Pop value");
                System.out.println("3. Calculate MIN");
                System.out.println("4. Calculate MAX");
                System.out.println("5. Calculate GCD");
                System.out.println("6. Calculate LCM");
                System.out.println("7. Delay pop");
                System.out.println("8. Leave");
                System.out.println("Choose an option (enter 1-8): ");

                int command = scan.nextInt();

                switch (command) {
                    case 1: // push value
                        System.out.print("Enter an integer to push: ");
                        int value = scan.nextInt();
                        calculator.pushValue(value);
                        System.out.println("Value pushed: " + value);
                        break;
                    case 2: // Pop value
                        if (!calculator.isEmpty()) {
                            int popped = calculator.pop();
                            System.out.println("Popped value: " + popped);
                        } else {
                            System.out.println("Stack is empty!");
                        }
                        break;

                    case 3: // MIN {
                        if (!calculator.isEmpty()) {
                            calculator.pushOperation("min");
                            System.out.println("MIN result pushed: " + calculator.getLastResult());
                        } else {
                            System.out.println("Stack is empty!");
                        }
                        break;


                    case 4: // MAX
                        calculator.pushOperation("max");
                        System.out.println("MAX result pushed: " + calculator.getLastResult());
                        break;

                    case 5: // GCD
                        calculator.pushOperation("gcd");
                        System.out.println("GCD result pushed: " + calculator.getLastResult());
                        break;

                    case 6: // LCM
                        calculator.pushOperation("lcm");
                        System.out.println("LCM result pushed: " + calculator.getLastResult());
                        break;

                    case 7: // Delay Pop
                        System.out.print("Enter delay in milliseconds: ");
                        int millis = scan.nextInt();
                        int delayedPop = calculator.delayPop(millis);
                        System.out.println("Delayed pop result: " + delayedPop);
                        break;


                    case 8: // Exit
                        running = false;
                        System.out.println("Exiting Calculator Client...");
                        break;

                    default:
                        System.out.println("Invalid option! Please choose 1-9.");
                }
            }
            scan.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

