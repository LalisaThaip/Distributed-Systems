package assignment2;
import java.net.*;
import java.io.IOException;

public interface IHttpResBuilder {
    /** 
     * constructs and sends an HTTP response 
     * @param socket client socket connection 
     * @param status HTTP status code 
     * @param body response body
     * @param lamportClock Lamport clock value
     * @throws IOException if sending fails.
     */
    void sendResponse(Socket socket, int status, String body, long lamportClock) throws IOException;

    /**
     * Builds an HTTp response string
     * @param status HTTP status code 
     * @param body response body
     * @param lamportClock Lamport clock value
     * @return Formatted HTTP response string
     */
    String buildResponse(int status, String body, long lamportClock);

    /** 
     * Parses the body from an HTTP response
     * @param response HTTP response string 
     * @return Body string 
     */
    String parseBody(String response); 
}