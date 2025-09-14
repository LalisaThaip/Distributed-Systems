package assignment2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

import assignment2.GETClient;

/**
 * Test class to simulate multiple concurrent GETClient instances
 * connecting to the AggregationServer and retrieving weather data.
 * This tests the server's ability to handle concurrent GET requests
 * while data is being updated by a ContentServer.
 */

public class GETClientConcurrentTest {
    private static final int TEST_PORT = 4568; // Avoid conflict with default 4567
    private static final String TEST_JSON = "test_weather.json";
    private static final String SERVER_URL = "http://localhost:" + TEST_PORT;
    private AggregationServer server;
    private Thread serverThread;
    private List<String> clientOutputs;

    @Before
    public void setUp() throws IOException {
        // Create test JSON file
        String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\",\"timeZone\":\"PST\"," +
                "\"lat\":34.05,\"lon\":-118.25,\"localDateTime\":\"15/09/25 14:30\",\"localDateTimeFull\":\"20250915143000\"," +
                "\"airTemp\":22.5,\"apparentT\":20.0,\"cloud\":\"Clear\",\"dewpt\":10.0,\"press\":1013.25," +
                "\"relHum\":50,\"windDir\":\"N\",\"windSpdKmh\":10,\"windSpdKt\":5}";
        try (FileWriter writer = new FileWriter(TEST_JSON)) {
            writer.write(json);
        } catch (IOException e) {
            fail("Failed to create test JSON file: " + e.getMessage());
        }

        // Start AggregationServer in a separate thread
        serverThread = new Thread(() -> {
            try {
                server = new AggregationServer(TEST_PORT);
            } catch (IOException e) {
                System.err.println("Server startup failed: " + e.getMessage());
            }
        });
        serverThread.start();
        // Wait for server to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Send data to server using ContentServer
        ContentServer contentServer = new ContentServer();
        try {
            contentServer.run(new String[]{SERVER_URL, TEST_JSON});
        } catch (Exception e) {
            fail("ContentServer failed to send data: " + e.getMessage());
        }
        // Wait for data to be sent
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Initialize client output capture
        clientOutputs = new ArrayList<>();
    }

    @After
    public void tearDown() {
        // Stop server
        if (server != null) {
            server.shutdown();
        }
        try {
            serverThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Delete test JSON file
        new File(TEST_JSON).delete();
        // Delete weather_data.json
        new File("weather_data.json").delete();
    }

    @Test
    public void testMultipleGetClients() throws InterruptedException {
        int numClients = 5; // create 5 concurrent clients
        ExecutorService executor = Executors.newFixedThreadPool(numClients * 2);
        CountDownLatch latch = new CountDownLatch(numClients * 2); // For all stations and specific station
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Simulate multiple GETClients for all stations and specific station
        for (int i = 0; i < numClients; i++) {
            // GET all stations
            executor.submit(() -> {
                try {
                    GETClient client = new GETClient();
                    client.run(new String[]{SERVER_URL});
                    synchronized (clientOutputs) {
                        String output = outputStream.toString();
                        clientOutputs.add("Client " + Thread.currentThread().getId() + " (all stations): " + output);
                        outputStream.reset();
                    }
                } catch (Exception e) {
                    synchronized (clientOutputs) {
                        clientOutputs.add("Client " + Thread.currentThread().getId() + " (all stations) error: " + e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
            // GET specific station
            executor.submit(() -> {
                try {
                    GETClient client = new GETClient();
                    client.run(new String[]{SERVER_URL, "test_station"});
                    synchronized (clientOutputs) {
                        String output = outputStream.toString();
                        clientOutputs.add("Client " + Thread.currentThread().getId() + " (test_station): " + output);
                        outputStream.reset();
                    }
                } catch (Exception e) {
                    synchronized (clientOutputs) {
                        clientOutputs.add("Client " + Thread.currentThread().getId() + " (test_station) error: " + e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all clients to complete
        assertTrue("Clients did not complete in time", latch.await(10, TimeUnit.SECONDS));

        // Restore System.out
        System.setOut(originalOut);

        // Print captured outputs for debugging
        System.out.println("Client outputs:");
        for (String output : clientOutputs) {
            System.out.println(output);
        }

        // Verify responses
        for (String output : clientOutputs) {
            assertFalse("Client output contains error: " + output, output.contains("error"));
            assertFalse("Client output contains GET failure: " + output, output.contains("GET failed"));
        }

        // Verify one client manually for detailed checks
        GETClient client = new GETClient();
        ByteArrayOutputStream singleOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(singleOutput));
        client.run(new String[]{SERVER_URL});
        System.setOut(originalOut);
        String allStationsOutput = singleOutput.toString();
        assertTrue("All stations response missing id", allStationsOutput.contains("id: test_station"));
        assertTrue("All stations response missing name", allStationsOutput.contains("name: Test Station"));
        assertFalse("All stations response contains GET failure", allStationsOutput.contains("GET failed"));

        // Verify specific station
        singleOutput.reset();
        System.setOut(new PrintStream(singleOutput));
        client.run(new String[]{SERVER_URL, "test_station"});
        System.setOut(originalOut);
        String specificStationOutput = singleOutput.toString();
        assertTrue("Specific station response missing id", specificStationOutput.contains("id: test_station"));
        assertTrue("Specific station response missing name", specificStationOutput.contains("name: Test Station"));
        assertFalse("Specific station response contains GET failure", specificStationOutput.contains("GET failed"));

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}