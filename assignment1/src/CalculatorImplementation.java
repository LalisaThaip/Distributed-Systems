import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.lang.Math;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private Stack<Integer> stack = new Stack<>();
    private int lastResult;

    // default constructor to throw remoteException from its parent constructor
    public CalculatorImplementation() throws RemoteException {
        super();
        stack = new Stack<>(); // shared stack for the client
        lastResult = 0;
    }

    public int getLastResult() throws RemoteException {
        return lastResult;
    }

    @Override
    public void pushValue(int value) throws RemoteException {
        stack.push(value);
        lastResult = value;
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
        lastResult = result;
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
        if (num1 == 0 || num2 == 0) return 0;
        return Math.abs(num1 * num2)/gcd(num1, num2);
    }

    @Override
    public int pop() throws RemoteException {
        if(stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }
        lastResult = stack.pop();
        return lastResult;

    }

    @Override
    public boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    @Override
    public int delayPop(int millis) throws RemoteException {
        try {
            // pause execution for requested number of milliseconds
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }
        lastResult = stack.pop();
        return lastResult;
    }
}