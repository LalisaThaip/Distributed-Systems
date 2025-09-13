package assignment2;

import java.net.Socket;
import java.io.IOException;

public interface IRequestHandler {
    /**
     * Handles an incoming client request.
     * @param clientSocket Client socket connection.
     * @throws IOException if handling fails.
     */
    void handleRequest(Socket clientSocket) throws IOException;
}