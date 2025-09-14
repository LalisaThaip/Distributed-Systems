package assignment2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AggregationServerTest {
    private static final int TEST_PORT = 4568;
    private AggregationServer server;
    private Thread serverThread;

    @Before
    public void setUp() throws IOException {
        // Start AggregationServer in a separate thread
        serverThread = new Thread(() -> {
            try {
                server = new AggregationServer(TEST_PORT);
            } catch (IOException e) {
                System.err.println("Server startup failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
        serverThread.start();
        // Wait for server to start (increased to 2s for stability)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @After
    public void tearDown() {
        if (server != null) {
            server.shutdown();
        }
        try {
            serverThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        new File("weather_data.json").delete();
    }

    // Test that the server starts and accepts connections
    @Test
    public void testServerStartup() throws IOException {
        // Purpose: Verify that AggregationServer starts and accepts socket connections on port 4568
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            assertTrue("Server should accept connections on port " + TEST_PORT, socket.isConnected());
        }
    }

    // Test GET request when no data exists, expecting 200 OK and empty array
    @Test
    public void testGetWeatherJsonWhenEmpty() throws IOException {
        // Purpose: Ensure GET /weather.json returns HTTP/1.1 200 OK with [] when WeatherDataStore is empty
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("GET /weather.json HTTP/1.1");
            out.println("Host: localhost");
            out.println();
            String response = readResponse(in);
            System.out.println("testGetWeatherJsonWhenEmpty response:\n" + response);
            assertTrue("Response should start with HTTP/1.1 200 OK", response.contains("HTTP/1.1 200 OK"));
            assertTrue("Response body should contain empty array", response.contains("[]"));
        } catch (IOException e) {
            System.err.println("GET request failed: " + e.getMessage());
            throw e;
        }
    }

    // Test PUT request to store data and GET to retrieve it
    @Test
    public void testPutWeatherData() throws IOException {
        // Purpose: Verify that PUT stores data in WeatherDataStore and GET retrieves it
        String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\"}";
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("PUT /weather.json HTTP/1.1");
            out.println("Host: localhost");
            out.println("Content-Length: " + json.length());
            out.println();
            out.println(json);
            String response = readResponse(in);
            System.out.println("testPutWeatherData PUT response:\n" + response);
            assertTrue("PUT response should be 201 Created", response.contains("HTTP/1.1 201 Created"));
        } catch (IOException e) {
            System.err.println("PUT request failed: " + e.getMessage());
            throw e;
        }
        // Verify data was stored
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("GET /weather.json HTTP/1.1");
            out.println("Host: localhost");
            out.println();
            String response = readResponse(in);
            System.out.println("testPutWeatherData GET response:\n" + response);
            assertTrue("GET response should contain test_station", response.contains("test_station"));
            assertTrue("GET response should contain Test Station", response.contains("Test Station"));
        } catch (IOException e) {
            System.err.println("GET request failed: " + e.getMessage());
            throw e;
        }
    }

    // Test data expiry after 30 seconds
    @Test
    public void testDataExpiry() throws IOException, InterruptedException {
        // Purpose: Verify that WeatherDataStore removes data after 30s expiry, so GET returns []
        String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\"}";
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("PUT /weather.json HTTP/1.1");
            out.println("Host: localhost");
            out.println("Content-Length: " + json.length());
            out.println();
            out.println(json);
            String response = readResponse(in);
            System.out.println("testDataExpiry PUT response:\n" + response);
        } catch (IOException e) {
            System.err.println("PUT request failed: " + e.getMessage());
            throw e;
        }
        // Wait for expiry (increased to 40s for stability)
        Thread.sleep(40000);
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("GET /weather.json HTTP/1.1");
            out.println("Host: localhost");
            out.println();
            String response = readResponse(in);
            System.out.println("testDataExpiry GET response:\n" + response);
            assertTrue("Response should start with HTTP/1.1 200 OK", response.contains("HTTP/1.1 200 OK"));
            assertTrue("Response should be empty after expiry", response.contains("[]"));
        } catch (IOException e) {
            System.err.println("GET request failed: " + e.getMessage());
            throw e;
        }
    }

    private String readResponse(BufferedReader in) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            response.append(line).append("\n");
        }
        response.append("\n");
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        return response.toString();
    }
}