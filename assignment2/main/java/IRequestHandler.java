import java.io.*;
import java. util.*;
import java.net.*;

public interface IRequestHandler {
    public void handleRequest(Socket clientSocket) throws IOException;
}   
