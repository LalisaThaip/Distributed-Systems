package council.impl;

import java.io.Serializable; 

public class Message implements Serializable {
    public MessageType type;
    public String sender; // No need for int id, use String name "M1", "M2" etc.
    public double proposalNum; // allows composite no. like (3.1 3rd proposal from M1)
    public String proposalCandidate; // candidate name (text)

    public Message(MessageType type, String sender, double proposalNum, String proposalCandidate) {
        this.type = type;
        this.sender = sender;
        this.proposalNum = proposalNum;
        this.proposalCandidate = proposalCandidate;
    }

    @Override 
    /*
     * Serializes the message to a string for easy transmission.
     * Format: "TYPE:SENDER:PROPOSAL_NUM:PROPOSAL_CANDIDATE"
     */
    public String toString() {
        return type + ":" + sender + ":" + proposalNum + ":" + proposalCandidate;
    }

    /*
     * Deserializes a string back into a Message object.
     * Expects format: "TYPE:SENDER:PROPOSAL_NUM:PROPOSAL_CANDIDATE"
     * Throws IllegalArgumentException if format is incorrect.
     */
    public static Message fromString(String s) throws IllegalArgumentException {
        String[] p = s.split(":", 4); // Limit to 4 parts to handle candidate names with colons
        if (p.length < 3) throw new IllegalArgumentException("Invalid message: " + s); // At least TYPE, SENDER, PROPOSAL_NUM needed
        MessageType t = MessageType.valueOf(p[0]);
        String sender = p[1];
        double num = Double.parseDouble(p[2]);
        String val = p.length == 4 ? p[3] : null; // Candidate can be null
        if (val != null && val.isEmpty()) val = null; // Treat empty string as null
        return new Message(t, sender, num, val); 
    }
}