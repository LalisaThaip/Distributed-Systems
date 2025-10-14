package council.interfaces;

import council.impl.Message;

public interface Learner {
    /** 
     * Learns the final decision value once consensus is achieved
     * @param msg The ACCEPTED message containing the decided value.
     */
    void learn(Message msg);
    
}
