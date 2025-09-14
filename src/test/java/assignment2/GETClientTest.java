package assignment2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class GETClientTest {
    private static final int TEST_PORT = 4568;
    private static final String SERVER_URL = "http://localhost:" + TEST_PORT;
    private static final String TEST_JSON = "test_weather.json";
    private AggregationServer server;
    private Thread serverThread;

    @Before
    public void setUp() throws IOException {
        // Create test JSON
        String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\"}";
        try (FileWriter writer = new FileWriter(TEST_JSON)) {
            writer.write(json);
        }
        // Start AggregationServer
        serverThread = new Thread(() -> {
            try {
                server = new AggregationServer(TEST_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Send data via ContentServer
        ContentServer contentServer = new ContentServer();
        contentServer.run(new String[]{SERVER_URL, TEST_JSON});
        try {
            Thread.sleep(1000);
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
        new File(TEST_JSON).delete();
        new File("weather_data.json").delete();
    }

    // Test GET request for all stations, verifying data output
    @Test
    public void testGetAllStations() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        GETClient client = new GETClient();
        Thread clientThread = new Thread(() -> client.run(new String[]{SERVER_URL}));
        clientThread.start();
        Thread.sleep(2000); // Allow one iteration
        clientThread.interrupt();
        try {
            clientThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.setOut(originalOut);
        String output = outContent.toString();
        assertFalse("Should not print GET failure", output.contains("GET failed"));
        assertTrue("Should print station ID", output.contains("id: test_station"));
        assertTrue("Should print station name", output.contains("name: Test Station"));
    }

    // Test GET request for a specific station
    @Test
    public void testGetSpecificStation() throws InterruptedException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        GETClient client = new GETClient();
        Thread clientThread = new Thread(() -> client.run(new String[]{SERVER_URL, "test_station"}));
        clientThread.start();
        Thread.sleep(2000);
        clientThread.interrupt();
        try {
            clientThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.setOut(originalOut);
        String output = outContent.toString();
        assertFalse("Should not print GET failure", output.contains("GET failed"));
        assertTrue("Should print station ID", output.contains("id: test_station"));
        assertTrue("Should print station name", output.contains("name: Test Station"));
    }

    // Test handling of invalid URL input
    @Test
    public void testInvalidUrl() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        GETClient client = new GETClient();
        client.run(new String[]{"invalid_url"});
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue("Should print error for invalid URL", output.contains("Invalid") || output.contains("error"));
    }

    // Test handling of connection failure
    @Test
    public void testConnectionFailure() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        GETClient client = new GETClient();
        client.run(new String[]{"http://localhost:9999"}); // Non-existent port
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue("Should print connection error", output.contains("Connection error"));
    }
}