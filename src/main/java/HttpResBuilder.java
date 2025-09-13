import java.io.*;
import java.net.Socket;

/** HttpResBuilder implementation for constructing and sending HTTP responses */
public class HttpResBuilder implements IHttpResBuilder {
    @Override 
    public void sendResponse(Socket socket, int status, String body, long lamportClock) {
        try {
            String response = buildResponse(status, body, lamportClock);
            OutputStream out = socket.getOutputStream();
            out.write(response.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String buildResponse(int status, String body, long lamportClock) {
        String statusText;
        switch (status) {
            case 200: statusText = "OK"; break;
            case 400: statusText = "Bad Request"; break;
            case 404: statusText = "Not Found"; break;
            case 500: statusText = "Internal Server Error"; break;
            default: statusText = "Unknown";
        };

        return "HTTP/1.1 " + status + " " + statusText + "\r\n" +
               "Content-Type: application/json\r\n" +
               "Content-Length: " + body.length() + "\r\n" +
               "Lamport-Clock: " + lamportClock + "\r\n" +
               "\r\n" + body;
    }

    @Override 
    public String parseBody(String response) {
        int bodyStart = response.indexOf("\r\n\r\n");
        if (bodyStart != -1) {
            return response.substring(bodyIndex + 4);
        }
        return "";
    }

}
