package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ContentServer implements IContentServer {
    private static long lamportClock = 0;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ICLIParser cliParser = new CLIParser();

    public static void main(String[] args) {
        ContentServer server = new ContentServer();
        while (true) {
            server.run(args);
            try {  
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void run(String args[]) {
        try {
            String serverUrl = cliParser.parseServerUrl(args);
            String filePath = cliParser.parseFilePath(args);
            String host = cliParser.parseHost(serverUrl);
            int port = cliParser.parsePort(serverUrl);

            WeatherData data = parseWeatherFile(filePath);
            if (data == null) {
                System.out.println("Failed to parse weather file.");
                return;
            }

            try (Socket socket = new Socket(host, port)) {
                sendPutRequest(socket, data);
                String response = readResponse(socket);
                long receivedClock = updateLamportClock(response);
                lamportClock = Math.max(lamportClock, receivedClock) + 1;

                int status = extractStatusCode(response);
                if (status == 200 || status == 201) {
                    System.out.println("PUT successful with status: " + status);
                } else {
                    System.out.println("PUT failed with status: " + status);
                }
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public WeatherData parseWeatherFile(String filePath) {
        try {
            return mapper.readValue(new File(filePath), WeatherData.class);
        } catch (IOException e) {
            System.out.println("Error parsing weather file: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void sendPutRequest(Socket socket, WeatherData data) throws IOException {
        lamportClock++;
        String json = data.toJson();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("PUT /weather.json HTTP/1.1");
        out.println("User-Agent: ContentServer/1.0");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + json.length());
        out.println("Lamport-Clock: " + lamportClock);
        out.println();
        out.println(json);
    }

    @Override
    public long updateLamportClock(String response) {
        for (String line : response.split("\n")) {
            if (line.startsWith("Lamport-Clock:")) {
                return Long.parseLong(line.split(":")[1].trim());
            }
        }
        return 0;
    }

    @Override
    public int extractStatusCode(String response) {
        String firstLine = response.split("\n")[0];
        return Integer.parseInt(firstLine.split(" ")[1]);
    }

    private String readResponse(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        return response.toString();
    }
}