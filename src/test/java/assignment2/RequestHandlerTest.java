// package assignment2;

// import org.junit.After;
// import org.junit.Before;
// import org.junit.Test;

// import java.io.*;
// import java.net.Socket;

// import static org.junit.Assert.*;

// public class RequestHandlerTest {
//     private static final int TEST_PORT = 4569;
//     private AggregationServer server;
//     private Thread serverThread;

//     @Before
//     public void setUp() throws IOException {
//         // Start AggregationServer in a separate thread
//         serverThread = new Thread(() -> {
//             try {
//                 server = new AggregationServer(TEST_PORT);
//             } catch (IOException e) {
//                 System.err.println("Server startup failed: " + e.getMessage());
//                 e.printStackTrace();
//             }
//         });
//         serverThread.start();
//         // Wait for server to start
//         try {
//             Thread.sleep(2000);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//     }

//     @After
//     public void tearDown() {
//         if (server != null) {
//             server.shutdown();
//         }
//         try {
//             serverThread.join(2000);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//         new File("weather_data.json").delete();
//         new File("weather_data.temp.json").delete();
//     }

//     // Test GET request for all stations when no data exists
//     @Test
//     public void testGetAllStationsEmpty() throws IOException {
//         // Purpose: Verify that GET /weather.json returns HTTP/1.1 200 OK with empty array when no data is stored
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("GET /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println();
//             String response = readResponse(in);
//             System.out.println("testGetAllStationsEmpty response:\n" + response);
//             assertTrue("Response should start with HTTP/1.1 200 OK", response.contains("HTTP/1.1 200 OK"));
//             assertTrue("Response body should be empty array", response.contains("[]") || response.contains("{}"));
//         } catch (IOException e) {
//             System.err.println("GET request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test GET request for a specific station
//     @Test
//     public void testGetSpecificStation() throws IOException {
//         // Purpose: Ensure GET /weather.json?id=station1 returns data for the specific station after a PUT
//         String json = "{\"id\":\"station1\",\"name\":\"Station One\",\"state\":\"CA\",\"timeZone\":\"PST\",\"lat\":37.7749,\"lon\":-122.4194,\"localDateTime\":\"2023-10-01 12:00\",\"localDateTimeFull\":\"2023-10-01 12:00:00\",\"airTemp\":20.5,\"apparentT\":21.0,\"cloud\":\"Clear\",\"dewpt\":15.0,\"press\":1013.25,\"relHum\":65,\"windDir\":\"NW\",\"windSpdKmh\":10,\"windSpdKt\":5}";
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Content-Length: " + json.length());
//             out.println();
//             out.println(json);
//             String response = readResponse(in);
//             System.out.println("testGetSpecificStation PUT response:\n" + response);
//             assertTrue("PUT response should be 201 Created", response.contains("HTTP/1.1 201 Created"));
//         } catch (IOException e) {
//             System.err.println("PUT request failed: " + e.getMessage());
//             throw e;
//         }
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("GET /weather.json?id=station1 HTTP/1.1");
//             out.println("Host: localhost");
//             out.println();
//             String response = readResponse(in);
//             System.out.println("testGetSpecificStation GET response:\n" + response);
//             assertTrue("Response should start with HTTP/1.1 200 OK", response.contains("HTTP/1.1 200 OK"));
//             assertTrue("Response body should contain station1", response.contains("station1"));
//             assertTrue("Response body should contain Station One", response.contains("Station One"));
//         } catch (IOException e) {
//             System.err.println("GET request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test PUT request with new data
//     @Test
//     public void testPutNewData() throws IOException {
//         // Purpose: Verify that PUT with new data returns 201 Created and stores data correctly
//         String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\",\"timeZone\":\"PST\",\"lat\":37.7749,\"lon\":-122.4194,\"localDateTime\":\"2023-10-01 12:00\",\"localDateTimeFull\":\"2023-10-01 12:00:00\",\"airTemp\":20.5,\"apparentT\":21.0,\"cloud\":\"Clear\",\"dewpt\":15.0,\"press\":1013.25,\"relHum\":65,\"windDir\":\"NW\",\"windSpdKmh\":10,\"windSpdKt\":5}";
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Content-Length: " + json.length());
//             out.println();
//             out.println(json);
//             String response = readResponse(in);
//             System.out.println("testPutNewData response:\n" + response);
//             assertTrue("PUT response should be 201 Created", response.contains("HTTP/1.1 201 Created"));
//         } catch (IOException e) {
//             System.err.println("PUT request failed: " + e.getMessage());
//             throw e;
//         }
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("GET /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println();
//             String response = readResponse(in);
//             System.out.println("testPutNewData GET response:\n" + response);
//             assertTrue("GET response should contain test_station", response.contains("test_station"));
//             assertTrue("GET response should contain Test Station", response.contains("Test Station"));
//         } catch (IOException e) {
//             System.err.println("GET request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test PUT request with existing data
//     @Test
//     public void testPutExistingData() throws IOException {
//         // Purpose: Ensure PUT with existing data returns 200 OK and updates data
//         String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\",\"timeZone\":\"PST\",\"lat\":37.7749,\"lon\":-122.4194}";
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Content-Length: " + json.length());
//             out.println();
//             out.println(json);
//             String response = readResponse(in);
//             System.out.println("testPutExistingData first PUT response:\n" + response);
//             assertTrue("First PUT response should be 201 Created", response.contains("HTTP/1.1 201 Created"));
//         } catch (IOException e) {
//             System.err.println("First PUT request failed: " + e.getMessage());
//             throw e;
//         }
//         String updatedJson = "{\"id\":\"test_station\",\"name\":\"Updated Station\",\"state\":\"NY\"}";
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Content-Length: " + updatedJson.length());
//             out.println();
//             out.println(updatedJson);
//             String response = readResponse(in);
//             System.out.println("testPutExistingData second PUT response:\n" + response);
//             assertTrue("Second PUT response should be 200 OK", response.contains("HTTP/1.1 200 OK"));
//         } catch (IOException e) {
//             System.err.println("Second PUT request failed: " + e.getMessage());
//             throw e;
//         }
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("GET /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println();
//             String response = readResponse(in);
//             System.out.println("testPutExistingData GET response:\n" + response);
//             assertTrue("GET response should contain Updated Station", response.contains("Updated Station"));
//             assertTrue("GET response should contain NY", response.contains("NY"));
//         } catch (IOException e) {
//             System.err.println("GET request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test PUT request with empty body
//     @Test
//     public void testPutEmptyBody() throws IOException {
//         // Purpose: Verify that PUT with empty body returns 204 No Content
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Content-Length: 0");
//             out.println();
//             String response = readResponse(in);
//             System.out.println("testPutEmptyBody response:\n" + response);
//             assertTrue("Response should start with HTTP/1.1 204 No Content", response.contains("HTTP/1.1 204 No Content"));
//         } catch (IOException e) {
//             System.err.println("PUT request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test invalid HTTP method
//     @Test
//     public void testInvalidMethod() throws IOException {
//         // Purpose: Ensure invalid HTTP method returns 400 Bad Request
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("INVALID /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println();
//             String response = readResponse(in);
//             System.out.println("testInvalidMethod response:\n" + response);
//             assertTrue("Response should start with HTTP/1.1 400 Bad Request", response.contains("HTTP/1.1 400 Bad Request"));
//         } catch (IOException e) {
//             System.err.println("INVALID request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test PUT with invalid JSON
//     @Test
//     public void testPutInvalidJson() throws IOException {
//         // Purpose: Verify that PUT with invalid JSON returns 500 Internal Server Error
//         String invalidJson = "{invalid}";
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Content-Length: " + invalidJson.length());
//             out.println();
//             out.println(invalidJson);
//             String response = readResponse(in);
//             System.out.println("testPutInvalidJson response:\n" + response);
//             assertTrue("Response should start with HTTP/1.1 500 Internal Server Error", response.contains("HTTP/1.1 500 Internal Server Error"));
//         } catch (IOException e) {
//             System.err.println("PUT request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     // Test Lamport clock update with received timestamp
//     @Test
//     public void testLamportClockUpdate() throws IOException {
//         // Purpose: Ensure RequestHandler updates Lamport clock with received timestamp
//         String json = "{\"id\":\"test_station\",\"name\":\"Test Station\",\"state\":\"CA\",\"timeZone\":\"PST\",\"lat\":37.7749,\"lon\":-122.4194,\"localDateTime\":\"2023-10-01 12:00\",\"localDateTimeFull\":\"2023-10-01 12:00:00\",\"airTemp\":20.5,\"apparentT\":21.0,\"cloud\":\"Clear\",\"dewpt\":15.0,\"press\":1013.25,\"relHum\":65,\"windDir\":\"NW\",\"windSpdKmh\":10,\"windSpdKt\":5}";
//         try (Socket socket = new Socket("localhost", TEST_PORT)) {
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out.println("PUT /weather.json HTTP/1.1");
//             out.println("Host: localhost");
//             out.println("Lamport-Clock: 10");
//             out.println("Content-Length: " + json.length());
//             out.println();
//             out.println(json);
//             String response = readResponse(in);
//             System.out.println("testLamportClockUpdate response:\n" + response);
//             assertTrue("PUT response should be 201 Created", response.contains("HTTP/1.1 201 Created"));
//             assertTrue("Response should include updated Lamport-Clock", response.contains("Lamport-Clock: 11"));
//         } catch (IOException e) {
//             System.err.println("PUT request failed: " + e.getMessage());
//             throw e;
//         }
//     }

//     private String readResponse(BufferedReader in) throws IOException {
//         StringBuilder response = new StringBuilder();
//         String line;
//         while ((line = in.readLine()) != null && !line.isEmpty()) {
//             response.append(line).append("\n");
//         }
//         response.append("\n");
//         while ((line = in.readLine()) != null) {
//             response.append(line).append("\n");
//         }
//         return response.toString();
//     }
// }