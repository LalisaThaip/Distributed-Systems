package interfaces;

import council.impl.Message;
import java.io.IOException;


public interface Acceptor {
    /**
     * Handles a PREPARE message from proposer.
     * @param msg The PREPARE message received.
     * @throws IOException If network/IO failure occurs when replying
     */
    void onPrepare(Message msg) throws IOException;

    /** 
     * Handles an ACCEPT_REQUEST massage from proposer.
     * @param msg The ACCEPT_REQUEST message received.
     * @throws IOException If network/IO failure occurs when replying
     */
    void onAcceptRequest(Message msg) throws IOException;
    
}
