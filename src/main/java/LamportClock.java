/**
 * LamportClock implementation for evet synchronization
 */
public class LamportClock {
    private long clock;

    public LamportClock() {
        this.clock = 0;
    }

    @Override 
    public synchronized void increment() {
        clock++;
    }

    @Override 
    public synchronized void update(long receivedClock) {
        clock = Math.max(clock, receivedClock) + 1;
    }

    @Override
    public synchronized long getClock() {
        return clock;
    }

}