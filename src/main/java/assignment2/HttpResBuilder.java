package assignment2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpResBuilder implements IHttpResBuilder {

    @Override
    public void sendResponse(Socket socket, int status, String body, long lamportClock) throws IOException {
        OutputStream os = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(os, true);
        String response = buildResponse(status, body, lamportClock);
        writer.println(response);
        writer.flush();
    }

    @Override
    public String buildResponse(int status, String body, long lamportClock) {
        String statusLine;
        switch (status) {
            case 200: statusLine = "200 OK"; break;
            case 201: statusLine = "201 Created"; break;
            case 204: statusLine = "204 No Content"; break;
            case 400: statusLine = "400 Bad Request"; break;
            case 500: statusLine = "500 Internal Server Error"; break;
            default: statusLine = "500 Internal Server Error";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusLine).append("\n");
        sb.append("Lamport-Clock: ").append(lamportClock).append("\n");
        if (body != null && !body.isEmpty()) {
            sb.append("Content-Type: application/json\n");
            sb.append("Content-Length: ").append(body.length()).append("\n");
            sb.append("\n");
            sb.append(body);
        } else {
            sb.append("Content-Length: 0\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String parseBody(String response) {
        int bodyStart = response.indexOf("\n\n");
        if (bodyStart != -1) {
            return response.substring(bodyStart + 2);
        }
        return "";
    }
}