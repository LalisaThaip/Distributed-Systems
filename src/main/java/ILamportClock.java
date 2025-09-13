/**
 * Interface for managine Lamport clocks fpr synochronization
 */
public interface ILamportClock {
    /**
     * Increments the local clock before sending an event
     */
    void increment(); 

    /**
     * Updates the clock based on a recieved timestamp 
     * @param receivedTimestamp Timestamp received from another process
     */
    void update(long receivedClock);

    /** 
     * Gets the current clock value 
     * @return Current clock vlaue
     */
    long getClock();
    
}
