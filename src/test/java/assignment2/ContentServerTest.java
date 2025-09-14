package assignment2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ContentServerTest {
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

    // Test PUT request with valid JSON, verifying data is stored
    @Test
    public void testPutValidJson() throws InterruptedException, IOException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        ContentServer contentServer = new ContentServer();
        Thread contentThread = new Thread(() -> contentServer.run(new String[]{SERVER_URL, TEST_JSON}));
        contentThread.start();
        Thread.sleep(2000);
        contentThread.interrupt();
        try {
            contentThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue("Should print PUT success", output.contains("PUT successful") || output.contains("201"));
        // Verify data was stored
        try (Socket socket = new Socket("localhost", TEST_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("GET /weather.json HTTP/1.1");
            out.println("Host: localhost");
            out.println();
            String response = readResponse(in);
            assertTrue("Response should contain test_station", response.contains("test_station"));
            assertTrue("Response should contain Test Station", response.contains("Test Station"));
        }
    }

    // Test handling of invalid JSON file
    @Test
    public void testInvalidJsonFile() throws IOException {
        try (FileWriter writer = new FileWriter("invalid.json")) {
            writer.write("{invalid}");
        }
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        ContentServer contentServer = new ContentServer();
        contentServer.run(new String[]{SERVER_URL, "invalid.json"});
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue("Should print error for invalid JSON", output.contains("Error") || output.contains("Invalid"));
        new File("invalid.json").delete();
    }

    // Test handling of non-existent JSON file
    @Test
    public void testNonExistentFile() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        ContentServer contentServer = new ContentServer();
        contentServer.run(new String[]{SERVER_URL, "nonexistent.json"});
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue("Should print error for nonexistent file", output.contains("Error") || output.contains("not found"));
    }

    // Test connection failure to invalid server
    @Test
    public void testConnectionFailure() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        ContentServer contentServer = new ContentServer();
        contentServer.run(new String[]{"http://localhost:9999", TEST_JSON});
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue("Should print connection error", output.contains("Connection error") || output.contains("refused"));
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