import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.concurrent.*;

/**
 * Test class for Calculator RMI server.
 * Includes tests for both single-client and multi-client scenarios.
 */
public class CalculatorTest {

    // Reference to the remote Calculator object
    private static Calculator calculator;

    /**
     * Runs once before all tests.
     * Connects to the RMI server using the lookup URL.
     * Make sure rmiregistry and CalculatorServer are running before this.
     */
    @BeforeAll
    public static void setup() throws Exception {
        calculator = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
    }

    /**
     * Runs before each test.
     * Ensures the calculator stack is empty before each test.
     */
    @BeforeEach
    public void clearStack() throws RemoteException {
        while (!calculator.isEmpty()) {
            calculator.pop();
        }
    }

    // ------------------ SINGLE CLIENT TESTS ------------------

    /**
     * Tests basic push and pop operations for a single client.
     * Verifies LIFO behavior and that stack is empty afterwards.
     */
    @Test
    public void testSingleClientPushPop() throws RemoteException {
        calculator.pushValue(5);          // Push first value
        calculator.pushValue(10);         // Push second value
        assertEquals(10, calculator.pop(), "Last value pushed should be popped first (LIFO).");
        assertEquals(5, calculator.pop(), "Second value should be popped second.");
        assertTrue(calculator.isEmpty(), "Stack should be empty after popping all values.");
    }

    /**
     * Tests calculator operations for a single client:
     * min, max, gcd, and lcm.
     */
    @Test
    public void testSingleClientOperations() throws RemoteException {
        // MIN operation
        calculator.pushValue(4);
        calculator.pushValue(8);
        calculator.pushOperation("min");  // Push minimum of 4 and 8
        assertEquals(4, calculator.pop());

        // MAX operation
        calculator.pushValue(7);
        calculator.pushValue(3);
        calculator.pushOperation("max");  // Push maximum of 7 and 3
        assertEquals(7, calculator.pop());

        // GCD operation
        calculator.pushValue(12);
        calculator.pushValue(18);
        calculator.pushOperation("gcd");  // Push greatest common divisor
        assertEquals(6, calculator.pop());

        // LCM operation
        calculator.pushValue(5);
        calculator.pushValue(20);
        calculator.pushOperation("lcm");  // Push least common multiple
        assertEquals(20, calculator.pop());
    }

    /**
     * Tests the delayPop method for a single client.
     * Ensures the method waits for the specified delay before returning the value.
     */
    @Test
    public void testDelayPop() throws RemoteException {
        calculator.pushValue(99);        // Push a value
        long start = System.currentTimeMillis();
        int val = calculator.delayPop(500); // Wait 500ms before popping
        long end = System.currentTimeMillis();
        assertEquals(99, val, "Value returned by delayPop should match pushed value.");
        assertTrue((end - start) >= 500, "delayPop should wait at least 500ms.");
    }

    // ------------------ MULTI-CLIENT TESTS ------------------

    /**
     * Simulates multiple clients interacting with the RMI server concurrently.
     * Each client pushes a value and pops it.
     * Uses ExecutorService to run clients in parallel.
     */
    @Test
    public void testMultipleClients() throws InterruptedException, ExecutionException {
        // Create a thread pool to simulate 3 clients
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Client 1: push 10, then pop
        Callable<Integer> client1 = () -> {
            try {
                Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
                c.pushValue(10);
                return c.pop();
            } catch (RemoteException e) {
                throw new RuntimeException(e); // wrap checked exception
            }
        };

        // Client 2: push 20, then pop
        Callable<Integer> client2 = () -> {
            try {
                Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
                c.pushValue(20);
                return c.pop();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        // Client 3: push 30, then pop
        Callable<Integer> client3 = () -> {
            try {
                Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
                c.pushValue(30);
                return c.pop();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        };

        // Run all clients concurrently
        Future<Integer> f1 = executor.submit(client1);
        Future<Integer> f2 = executor.submit(client2);
        Future<Integer> f3 = executor.submit(client3);

        // Collect results
        int result1 = f1.get();
        int result2 = f2.get();
        int result3 = f3.get();

        // Assert that each result matches one of the pushed values
        assertTrue(result1 == 10 || result1 == 20 || result1 == 30);
        assertTrue(result2 == 10 || result2 == 20 || result2 == 30);
        assertTrue(result3 == 10 || result3 == 20 || result3 == 30);

        // Shutdown executor service
        executor.shutdown();
    }
}
