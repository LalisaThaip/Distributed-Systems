package council.interfaces;

import council.impl.Message;
import java.io.IOException;

public interface Proposer {
    /** 
     * Initiates a proposal to elect a candidate.
     * @param candidate Candidate name or ID to be proposed.
     * @throws IOException If network/IO failure occurs.
     */
    void propose(String candidate) throws IOException;

    /** 
     * Handles a PROMISE message from an acceptor.
     * @param msg The PROMISE message received.
     * @throws IOException if there is a communication error.
     */
    void handlePromise(Message msg) throws IOException;
}
