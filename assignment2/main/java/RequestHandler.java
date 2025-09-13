import java.io.*;
import java. util.*;
import java.net.*;

public interface RequestHandler {
    public void handleRequest(Socket clientSocket) throws IOException;
}   
