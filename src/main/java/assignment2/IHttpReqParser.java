package assignment2;

import java.io.IOException;
import java.io.InputStream;

public interface IHttpReqParser {
    /**
     * Parses raw HTTP request from input stream.
     * @param in Input stream from client socket.
     * @return Parsed HTTP request object.
     * @throws IOException on read error.
     */
    HttpRequest parseRequest(InputStream in) throws IOException;
}