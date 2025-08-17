import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.lang.Math;

/*
 * Implementation of the Calculator interface for RMI.
 * This class provides a remote stack-based calculator that allows
 * clients to push values, perform operations (min, max, gcd, lcm),
 * pop values, and delay pop operations.
 *
 * The stack is shared across clients connecting to the same server instance.
 */
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    // Stack to store integer values for operations
    private Stack<Integer> stack = new Stack<>();

    // Stores the last result computed or popped from the stack
    private int lastResult;

    /*
     * Default constructor.
     * Initializes the stack and sets lastResult to 0.
     * @throws RemoteException Thrown if RMI export fails.
     */    
    public CalculatorImplementation() throws RemoteException {
        super();
        stack = new Stack<>(); // shared stack for the client
        lastResult = 0;
    }

    /**
     * Returns the last value that was pushed or the result of the last operation.
     * @return int representing the last result
     * @throws RemoteException if remote communication fails
     */
    @Override
    public int getLastResult() throws RemoteException {
        return lastResult;
    }
    
    /**
     * Pushes an integer value onto the server's stack.
     * Updates lastResult to the pushed value.
     * @param value the integer value to push
     * @throws RemoteException if remote communication fails
     */
    @Override
    public void pushValue(int value) throws RemoteException {
        stack.push(value);
        lastResult = value;
    }

    /**
     * Performs an operation on the current stack values.
     * Supports min, max, gcd, and lcm operations.
     * Pops all values from the stack, computes the result, and pushes it back.
     * @param operator String representing the operation ("min", "max", "gcd", "lcm")
     * @throws RemoteException if remote communication fails or invalid operator is provided
     */
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
                // compute minimum of all values on the stack
                result = Integer.MAX_VALUE;
                while (!stack.isEmpty()) {
                    result = Math.min(result, stack.pop());
                }
                break;

            }
            case "max": {
                // compute maximum of all values on the stack
                result = Integer.MIN_VALUE;

                while (!stack.isEmpty()) {
                    result = Math.max(result, stack.pop());
                }
                break;
            }
            case "gcd": {
                // compute gcd of all values on the stack
                result = stack.pop();

                while (!stack.isEmpty()) {
                    result = gcd(result, stack.pop());
                }
                break;
            }
            case "lcm": {
                // compute lcm of all values on the stack
                result = stack.pop();

                while (!stack.isEmpty()) {
                    result = lcm(result, stack.pop());
                }
                break;
            }
            default:
                throw new RemoteException("Invalid operation: " + operator);
        }

        stack.push(result); // push the computed value back onto the stack
        lastResult = result;
    }

    /**
     * Helper function to compute gcd (greatest common divisor) of two integers
     * in pushOperation()
     * @param a first integer
     * @param b second integer
     * @return gcd of a and b
     */
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

    /**
     * Helper function to compute lcm (least common multiple) of two integers.
     * @param num1 first integer
     * @param num2 second integer
     * @return lcm of num1 and num2, returns 0 if either number is 0
     */    
    private int lcm(int num1, int num2) {
        if (num1 == 0 || num2 == 0) return 0;
        return Math.abs(num1 * num2)/gcd(num1, num2);
    }

    /**
     * Pops and returns the top value from the stack.
     * @return int value popped from stack
     * @throws RemoteException if stack is empty or remote communication fails
     */
    @Override
    public int pop() throws RemoteException {
        if(stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }
        lastResult = stack.pop();
        return lastResult;

    }

    /**
     * Checks if the stack is empty.
     * @return true if stack is empty, false otherwise
     * @throws RemoteException if remote communication fails
     */
    @Override
    public boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    /**
     * Pops the top value from the stack after a delay.
     * @param millis the delay in milliseconds
     * @return int value popped after the delay
     * @throws RemoteException if stack is empty or remote communication fails
     */
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