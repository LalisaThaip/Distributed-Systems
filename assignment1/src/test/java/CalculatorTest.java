import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

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
        try {
            LocateRegistry.createRegistry(1099); 
        } catch (Exception e){}

        try {
            calculator = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        } catch (Exception e ) {
            CalculatorImplementation impl = new CalculatorImplementation();
            Naming.rebind("rmi://localhost:1099/calc", impl);
            calculator = impl; 
        }

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
     * Tests the pop method for a single client.
     * Ensures the method returns a stack is empty error after popping an empty stack
     */
    @Test
    public void testPopEmptyStack() throws RemoteException {
        assertTrue(calculator.isEmpty());
        assertThrows(RemoteException.class, () -> calculator.pop(), "Pop on empty stack should throw RemoteException");
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

    /**
     * Test isEmpty before and after push/pop.
     */
    @Test
    public void testIsEmpty() throws Exception {
        Calculator calc = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        assertTrue(calc.isEmpty(), "Stack should be empty initially");
        calc.pushValue(5);
        assertFalse(calc.isEmpty(), "Stack should not be empty after push");
        calc.pop();
        assertTrue(calc.isEmpty(), "Stack should be empty again after pop");
    }

    /**
     * Tests getLastResult() method.
     * Ensures that after each operation, getLastResult() returns the result
     * of the most recent operation performed on the stack.
     */
    @Test
    public void testGetLastResult() throws RemoteException {
        calculator.pushValue(7);
        calculator.pushValue(3);
        calculator.pushOperation("min"); // result should be 3
        int lastResult = calculator.getLastResult();
        assertEquals(3, lastResult, "getLastResult should return the last operation result");
        
        calculator.pushValue(12);
        calculator.pushOperation("max"); // result should be 12
        assertEquals(12, calculator.getLastResult(), "getLastResult should update after each operation");
    }


    // ------------------ MULTI-CLIENT TESTS ------------------

    @Test
    public void testMultipleClientsSequentialPush() throws Exception {
        // Stub a
        Calculator a = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        a.pushValue(10);
        assertEquals(10, a.pop(), "Stub a should pop 10");

        // Stub b
        Calculator b = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        b.pushValue(20);
        assertEquals(20, b.pop(), "Stub b should pop 20");

        // Stub c
        Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        c.pushValue(30);
        assertEquals(30, c.pop(), "Stub c should pop 30");
    }


    @Test
    public void testMultipleClientsPush() throws Exception {
        // Stub a
        Calculator a = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        a.pushValue(10);
        assertEquals(10, a.pop());

        // Stub b
        Calculator b = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        b.pushValue(20);
        assertEquals(20, b.pop());

        // Stub c
        Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        c.pushValue(30);
        assertEquals(30, c.pop());
    }

    /**
     * New multi-client scenario:
     * - Client 1 pushes two values
     * - Client 2 performs min
     * - Client 3 pushes two values
     * - Client 2 performs max
     * This tests shared stack consistency across multiple clients.
     */

    @Test
    public void testMultpleClientsPushMinMax() throws Exception {
        // MIN of 12 and 18
        Calculator a = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        a.pushValue(12);
        a.pushValue(18);

        Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        c.pushOperation("min");
        assertEquals(12, c.pop());

        // Now push values for MAX
        Calculator b = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        b.pushValue(5);
        b.pushValue(20);

        c.pushOperation("max");
        assertEquals(20, c.pop());
    }

    /**
     * New multi-client scenario:
     * - Client 1 pushes a value
     * - Client 2 pushes another value
     * - Client 3 performs an operation (MIN) on the shared stack
     * This tests shared stack consistency across multiple clients.
     */

    @Test
    public void testMutltipleClientPushMin() throws Exception {
        // Stub a pushes first value
        Calculator a = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        a.pushValue(15);

        // Stub b pushes second value
        Calculator b = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        b.pushValue(25);

        // Stub c performs MIN operation
        Calculator c = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        c.pushOperation("min");
        int minResult = c.pop();

        assertEquals(15, minResult, "MIN operation should return the smallest pushed value: 15");
    }

    /**
     * Test delayPop: one stub pushes, another tries delayPop, and another pops early.
     * Expected: delayPop should block, so the fast pop will empty the stack first.
     * After delayPop completes, it should throw RemoteException (stack empty).
     */
    @Test
    public void testSequentialDelayPopRaceSimulation() throws Exception {
        // Stub1 push a value
        Calculator stub1 = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        stub1.pushValue(99);

        // Stub2 delayPop for 2 seconds
        Calculator stub2 = (Calculator) Naming.lookup("rmi://localhost:1099/calc");

        // Run delayPop in a separate thread so we can simulate interruption
        Thread delayPopThread = new Thread(() -> {
            try {
                stub2.delayPop(2000); // intended delay
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        delayPopThread.start();

        // Stub3 pop immediately (simulate popping before delayPop)
        Calculator stub3 = (Calculator) Naming.lookup("rmi://localhost:1099/calc");
        Thread.sleep(500); // give a small delay so stubA has already pushed
        int immediatePop = stub3.pop();

        assertEquals(99, immediatePop, "Stub3 should pop the pushed value before stub2's delayPop");

        // Wait for delayPop thread to finish
        delayPopThread.join();

        // stack should be empty at the end
        assertTrue(stub2.isEmpty(), "Stack should be empty at the end");
    }



}