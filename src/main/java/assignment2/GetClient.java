package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class GETClient implements IGETClient {
    private static long lamportClock = 0;
    private static final ObjectMapper mapper = new ObjectMapper();
    private final ICLIParser cliParser = new CLIParser();

    public static void main(String[] args) {
        GETClient client = new GETClient();
        while (true) {
            client.run(args);
            try {  
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void run(String[] args) {
        try {
            String serverUrl = cliParser.parseServerUrl(args);
            String stationId = cliParser.parseStationId(args);
            String host = cliParser.parseHost(serverUrl);
            int port = cliParser.parsePort(serverUrl);

            try (Socket socket = new Socket(host, port)) {
                sendGetRequest(socket, serverUrl, stationId);
                String response = readResponse(socket);
                long receivedClock = updateLamportClock(response);
                lamportClock = Math.max(lamportClock, receivedClock) + 1;

                int status = extractStatusCode(response);
                if (status == 200) {
                    String body = parseBody(response);
                    displayWeatherData(body);
                } else {
                    System.out.println("GET failed with status: " + status);
                }
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void sendGetRequest(Socket socket, String serverUrl, String stationId) throws IOException {
        lamportClock++;
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String path = "/weather.json";
        if (stationId != null) {
            path += "?id=" + stationId;
        }
        out.println("GET " + path + " HTTP/1.1");
        out.println("Host: " + cliParser.parseHost(serverUrl));
        out.println("User-Agent: GETClient/1.0");
        out.println("Lamport-Clock: " + lamportClock);
        out.println();
    }

    @Override
    public void displayWeatherData(String response) {
        try {
            Object obj = mapper.readValue(response, Object.class);
            if (obj instanceof Map) {
                displayMap((Map<String, Object>) obj);
            } else if (obj instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("Station " + (i + 1) + ":");
                    displayMap(list.get(i));
                    System.out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private void displayMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
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
    public String extractHost(String url) {
        return cliParser.parseHost(url);
    }

    @Override
    public int extractPort(String url) {
        return cliParser.parsePort(url);
    }

    @Override
    public int extractStatusCode(String response) {
        String firstLine = response.split("\n")[0];
        return Integer.parseInt(firstLine.split(" ")[1]);
    }

    @Override
    public String readResponse(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        return response.toString();
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