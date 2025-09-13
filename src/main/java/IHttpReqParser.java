public interface IHttpReqParser {
    /** 
     * parses raw HTTP request from input stream
     * @param in Input stream from client socket
     * @return Parsed HTTP request object
     * @throws IOException on read error
     */
    HttpRequest parseRequest(java.io.InputStream in) throws java.io.IOException;
    
}
