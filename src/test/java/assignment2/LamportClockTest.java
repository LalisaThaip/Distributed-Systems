package assignment2;

import org.junit.Test;
import static org.junit.Assert.*;

public class LamportClockTest {
    // Test that a new LamportClock initializes to 0
    @Test
    public void testInitialClockValue() {
        // Purpose: Verify that a newly created LamportClock starts with a clock value of 0
        ILamportClock clock = new LamportClock();
        assertEquals("Initial clock value should be 0", 0, clock.getClock());
    }

    // Test incrementing the clock
    @Test
    public void testIncrementClock() {
        // Purpose: Ensure increment() increases the clock value by 1 each time
        ILamportClock clock = new LamportClock();
        clock.increment();
        assertEquals("Clock should be 1 after one increment", 1, clock.getClock());
        clock.increment();
        assertEquals("Clock should be 2 after two increments", 2, clock.getClock());
    }

    // Test updating the clock with a received timestamp
    @Test
    public void testUpdateWithReceivedClock() {
        // Purpose: Verify that update() sets the clock to max(current, received) + 1
        ILamportClock clock = new LamportClock();
        clock.increment(); // Clock = 1
        clock.update(5);
        assertEquals("Clock should be max(1, 5) + 1 = 6", 6, clock.getClock());
        clock.update(3);
        assertEquals("Clock should be max(6, 3) + 1 = 7", 7, clock.getClock());
    }

    // Test update with lower received timestamp
    @Test
    public void testUpdateWithLowerClock() {
        // Purpose: Ensure update() handles a lower received timestamp correctly
        ILamportClock clock = new LamportClock();
        clock.increment(); // Clock = 1
        clock.increment(); // Clock = 2
        clock.update(1);
        assertEquals("Clock should be max(2, 1) + 1 = 3", 3, clock.getClock());
    }

    // Test concurrent-like updates
    @Test
    public void testMultipleUpdates() {
        // Purpose: Verify that multiple updates and increments maintain correct clock values
        ILamportClock clock = new LamportClock();
        clock.increment(); // Clock = 1
        clock.update(10);  // Clock = max(1, 10) + 1 = 11
        clock.increment(); // Clock = 12
        clock.update(8);   // Clock = max(12, 8) + 1 = 13
        assertEquals("Clock should be 13 after multiple operations", 13, clock.getClock());
    }
}