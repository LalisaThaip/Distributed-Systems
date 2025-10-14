package council.impl;

/**
 * This class simulates a network environment for each profile type 
 * reliable, standard, latent, failure
*/
import java.io.*;
import java.util.*;

import council.impl.Message;

import java.net.*;

/**
* Network simulator to introduce delays and simulate failures.
*/
public class NetworkSimulator {
    private final String profile;
    private final Random random = new Random(); 

    public NetworkSimulator(String profile) {
        this.profile = profile == null ? "standard" : profile; // Default to standard if null
    }

    public void simulateDelay() {
        try {
            switch (profile.toLowerCase()) {
                case "reliable":
                    Thread.sleep(10);
=                    break;
                case "standard":
                    Thread.sleep(50 + random.nextInt(150)); // 50-200 ms delay
                    break;
                case "latent":
                    Thread.sleep(500 + random.nextInt(1500)); // 500-2000 ms delay
                    break;
                case "failure":
                    // 30% chance of failure / not responding 
                    if (random.nextInt(100) < 30) throw new IOException("Simulated node failure (profile=failure)");
                    Thread.sleep(500 + random.nextInt(500)); // 500-1000 ms delay if not failed
                    break;
                default:
                    throw new IllegalArgumentException("Unknown profile: " + profile);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } 
    }

    /**
    * Send a message to a host:port with a single-line text protocol.
    * Retries a couple times on transient failures.
    */
    public void send(String host, int port, Message msg) {
        // Simulate network delay based on profile before sending
        simulateDelay();
        IOException lastException = null;
        for (int attempt = 1; attempt <= 3; attempt++) { // Retry up to 3 times
            try (Socket socket = new Socket(host, port); // Connect to target
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { 
                out.println(msg.toString()); // Send serialized message
                return; // Success, exit method
            } catch (IOException e) {
                lastEx = e;
                // small backoff before retrying
                try { Thread.sleep(50 * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        throw lastEx != null ? lastEx : new IOException("Unknown network error sending message");
    }
    
}