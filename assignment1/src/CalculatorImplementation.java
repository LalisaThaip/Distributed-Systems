import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.lang.Math;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private Stack<Integer> stack = new Stack<>();

    // default constructor to throw remoteException from its parent constructor
    public CalculatorImplementation() throws RemoteException {
        super();
        stack = new Stack<>(); // shared stack for the client
    }

    @Override
    public void pushValue(int value) throws RemoteException {
        stack.push(value);
    }

    // pushes an operation on the server's stack and executes it
    // this method pops all the values from the stack, performs the operation,
    // and pushes the result back onto the stack
    @Override
    public void pushOperation(String operator) throws RemoteException {

        if (stack.isEmpty()) {
            System.out.println("Stack is empty");
            return;
        }

        int result = 0;

        // use switch cases for getting client operations
        switch (operator.toLowerCase()) {
            case "min": {
                // set the min value to a high value
                result = Integer.MAX_VALUE;
                while (!stack.isEmpty()) {
                    result = Math.min(result, stack.pop());
                }
                break;

            }
            case "max": {
                result = Integer.MIN_VALUE;

                while (!stack.isEmpty()) {
                    result = Math.max(result, stack.pop());
                }
                break;
            }
            case "gcd": {
                result = stack.pop();

                while (!stack.isEmpty()) {
                    result = gcd(result, stack.pop());
                }
                break;
            }
            case "lcm": {
                result = stack.pop();

                while (!stack.isEmpty()) {
                    result = lcm(result, stack.pop());
                }
                break;
            }
            default:
                throw new RemoteException("Invalid operation: " + operator);
        }

        stack.push(result);
    }

    // Helper function to calculate gcd in pushOperation()
    private int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Helper function to calculate lcm in pushOperation()
    private int lcm(int num1, int num2) {
        return Math.abs(num1 * num2)/gcd(num1, num2);
    }

    @Override
    public int pop() throws RemoteException {
        return stack.pop();
    }

    @Override
    public boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    @Override
    public int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
        return stack.pop();
    }

}