package council.impl;

import council.impl.Message;
import council.impl.NetworkSimulator;

/** 
 * This is the core class where each council member runs as a 
 * PaxosNode implementing Proposer, Acceptor, and Learner roles.
 * It handles message processing, state management, and consensus logic.
 * Each node can propose values, respond to proposals, and learn the final decision.
 * The class interacts with the NetworkSimulator to send and receive messages.
 */


import council.interfaces.Acceptor;
import council.interfaces.Learner;
import council.interfaces.Proposer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
 
/**
* Implements Proposer, Acceptor and Learner.
* Uses synchronized methods where shared state is mutated.
*/
public class PaxosNode implements Proposer, Acceptor, Learner {
    private final String memberId; 
    private final int port; 
    private final NetworkSimulator network;
    private final Map<String, Integer> memberPorts; 

    // Paxos state
    private volatile boolean decided = false; 
    private String decidedValue = null;

    private double promisedNum = -1;        // highest prepare seen 
    private double acceptedNum = -1;        // highest accepted propasal number
    private String acceptedVal = null;      // value accepted with acceptedNum

    // proposal counters
    private int localProposalSeq = 0;       // To generate unique proposal numbers

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public PaxosNode(String id, int port, String profile, Map<String, Integer> memberPorts) {
        this.memberId = id;
        this.port = port;
        this.network = new NetworkSimulator(profile);
        this.memberPorts = new HashMap<>(memberPorts);
    }

    // ---------- Proposer ----------
    @Override
    public void propose(String candidate) throws IOException {
        if (candidate == null || candidate.isEmpty()) throw new IllegalArgumentException("candidate must be non-empty");
        double proposalNum = generateProposalNumber();
        Message prepare = new Message(MessageType.PREPARE, memberId, proposalNum, candidate);
        broadcast(prepare);
    }

    @Override
    public void handlePromise(Message msg) throws IOException {
        // In a full implementation we'd collect promises and only send accept when majority collected.
        // For clarity here, when any PROMISE arrives we attempt an accept request for the promised value.
        // This method should be extended to track counts in production.
        try {
        Message acceptReq = new Message(MessageType.ACCEPT_REQUEST, memberId, msg.proposalNum, msg.proposalVal == null ? "" : msg.proposalVal);
        broadcast(acceptReq);
        } catch (Exception e) {
        System.err.println(memberId + " failed to broadcast ACCEPT_REQUEST: " + e.getMessage());
        }
    }

    private synchronized double generateProposalNumber() {
        localProposalSeq += 1;
        // unique monotonic number: seq + small fractional part from member id
        double idPart = 0.0;
        try { idPart = Double.parseDouble(memberId.substring(1)) / 10.0; } catch (Exception ignored) {}
        return localProposalSeq + idPart;
    }

    // ---------- Acceptor ----------
    @Override
    public synchronized void onPrepare(Message msg) throws IOException {
        // If proposal number is higher than any promised, promise it and reply with any accepted value
        if (msg.proposalNum > promisedNum) {
            promisedNum = msg.proposalNum;
            String valToReturn = acceptedVal; // could be null if none accepted yet
            Message promise = new Message(MessageType.PROMISE, memberId, promisedNum, valToReturn == null ? "" : valToReturn);
            sendTo(msg.sender, promise);
        } else {
            // Ignore lower-numbe prepare
        }
    }

   

    @Override
    public synchronized void onAcceptRequest(Message msg) throws IOException {
        if (msg.proposalNum >= promisedNum) {
            acceptedNum = msg.proposalNum;
            acceptedVal = msg.proposalCandidate;
            network.simulateDelay();
            sendTo(msg.sender, new Message(MessageType.ACCEPTED, memberId, acceptedNum, acceptedVal));
        }
    }


}