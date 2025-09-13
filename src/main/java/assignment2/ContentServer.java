package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ContentServer implements IContentServer {
    private static long lamportClock = 0;
    private static final ObjectMapper mapper = new ObjectMapper();
    private final ICLIParser cliParser = new CLIParser();

    @Override
    public void main(String[] args) {
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
                    System.out.println("PUT successful.");
                    verifyData(socket, data, data.getId());
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
        Map<String, Object> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                int colon = line.indexOf(':');
                if (colon > 0) {
                    String key = line.substring(0, colon).trim();
                    String valueStr = line.substring(colon + 1).trim();
                    Object value;
                    try {
                        value = Double.parseDouble(valueStr);
                    } catch (NumberFormatException e) {
                        try {
                            value = Integer.parseInt(valueStr);
                        } catch (NumberFormatException ex) {
                            value = valueStr;
                        }
                    }
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }

        if (!map.containsKey("id")) {
            System.out.println("No id found in file.");
            return null;
        }

        WeatherData data = new WeatherData();
        data.setId((String) map.get("id"));
        data.setName((String) map.get("name"));
        data.setState((String) map.get("state"));
        data.setTimeZone((String) map.get("time_zone"));
        data.setLat(map.get("lat") instanceof Double ? (Double) map.get("lat") : ((Integer) map.get("lat")).doubleValue());
        data.setLon(map.get("lon") instanceof Double ? (Double) map.get("lon") : ((Integer) map.get("lon")).doubleValue());
        data.setLocalDateTime((String) map.get("local_date_time"));
        data.setLocalDateTimeFull((String) map.get("local_date_time_full"));
        data.setAirTemp((Double) map.get("air_temp"));
        data.setApparentT((Double) map.get("apparent_t"));
        data.setCloud((String) map.get("cloud"));
        data.setDewpt((Double) map.get("dewpt"));
        data.setPress((Double) map.get("press"));
        data.setRelHum((Integer) map.get("rel_hum"));
        data.setWindDir((String) map.get("wind_dir"));
        data.setWindSpdKmh((Integer) map.get("wind_spd_kmh"));
        data.setWindSpdKt((Integer) map.get("wind_spd_kt"));
        return data;
    }

    @Override
    public void sendPutRequest(Socket socket, WeatherData data) throws IOException {
        lamportClock++;
        String json = data.toJson();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("PUT /weather.json HTTP/1.1");
        out.println("User-Agent: ATOMClient/1/0");
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

    public void verifyData(Socket socket, WeatherData sentData, String stationId) throws IOException {
        // Reuse socket or create new? Assignment implies verify with GET
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        lamportClock++;
        out.println("GET /weather.json?id=" + stationId + " HTTP/1.1");
        out.println("Lamport-Clock: " + lamportClock);
        out.println();
        String response = readResponse(socket);
        long receivedClock = updateLamportClock(response);
        lamportClock = Math.max(lamportClock, receivedClock) + 1;
        int status = extractStatusCode(response);
        if (status == 200) {
            String body = parseBody(response);
            WeatherData receivedData = WeatherData.fromJson(body);
            if (receivedData.equals(sentData)) {
                System.out.println("Verification successful.");
            } else {
                System.out.println("Verification failed.");
            }
        }
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

    private String parseBody(String response) {
        int bodyStart = response.indexOf("\n\n");
        if (bodyStart != -1) {
            return response.substring(bodyStart + 2);
        }
        return "";
    }
}