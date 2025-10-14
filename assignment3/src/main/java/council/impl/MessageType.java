package council.impl;

/**
 * Enumeration of message types used in the consensus protocol.
 * Enum is type safe alternative to raw strings, easy to serialise and parse
 * PREPARE: Sent by a proposer to initiate a proposal.
 * PROMISE: Sent by an acceptor to acknowledge a PREPARE.
 * ACCEPT_REQUEST: Sent by a proposer to request acceptance of a proposal.
 * ACCEPTED: Sent by an acceptor to confirm acceptance of a proposal.
 * DECIDE: Sent to inform learners of the final decided value.
 */
public enum Messagetype {
    PREPARE, PROMISE, ACCEPT_REQUEST, ACCEPTED, DECIDE;
}