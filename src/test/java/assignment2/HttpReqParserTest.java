// package assignment2;

// import org.junit.Before;
// import org.junit.Test;

// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.Map;

// import static org.junit.Assert.*;

// public class HttpReqParserTest {
//     private HttpReqParser parser;

//     @Before
//     public void setUp() {
//         // Initialize HttpReqParser
//         parser = new HttpReqParser();
//     }

//     // Test parsing a simple GET request
//     @Test
//     public void testParseGetRequest() throws IOException {
//         // Purpose: Verify that parseRequest correctly parses a GET request with headers
//         String request = "GET /weather.json HTTP/1.1\nHost: localhost\n\n";
//         ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
//         HttpRequest result = parser.parseRequest(input);
//         System.out.println("testParseGetRequest: method=" + result.getMethod() + ", path=" + result.getPath());
//         assertEquals("Method should be GET", "GET", result.getMethod());
//         assertEquals("Path should be /weather.json", "/weather.json", result.getPath());
//         assertEquals("Headers should contain Host", "localhost", result.getHeaders().get("Host"));
//         assertEquals("Body should be empty", "", result.getBody());
//     }

//     // Test parsing a PUT request with body
//     @Test
//     public void testParsePutRequestWithBody() throws IOException {
//         // Purpose: Ensure parseRequest handles PUT request with Content-Length and body
//         String request = "PUT /weather.json HTTP/1.1\nHost: localhost\nContent-Length: 25\n\n{\"id\":\"station1\",\"name\":\"Test\"}";
//         ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
//         HttpRequest result = parser.parseRequest(input);
//         System.out.println("testParsePutRequestWithBody: method=" + result.getMethod() + ", body=" + result.getBody());
//         assertEquals("Method should be PUT", "PUT", result.getMethod());
//         assertEquals("Path should be /weather.json", "/weather.json", result.getPath());
//         assertEquals("Headers should contain Host", "localhost", result.getHeaders().get("Host"));
//         assertEquals("Headers should contain Content-Length", "25", result.getHeaders().get("Content-Length"));
//         assertEquals("Body should contain JSON", "{\"id\":\"station1\",\"name\":\"Test\"}", result.getBody());
//     }

//     // Test parsing invalid request line
//     @Test(expected = IOException.class)
//     public void testParseInvalidRequestLine() throws IOException {
//         // Purpose: Verify that parseRequest throws IOException for invalid request line
//         String request = "GET\n\n";
//         ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
//         parser.parseRequest(input);
//     }

//     // Test parsing empty request
//     @Test(expected = IOException.class)
//     public void testParseEmptyRequest() throws IOException {
//         // Purpose: Ensure parseRequest throws IOException for empty input
//         String request = "";
//         ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
//         parser.parseRequest(input);
//     }

//     // Test parsing request with multiple headers
//     @Test
//     public void testParseMultipleHeaders() throws IOException {
//         // Purpose: Ensure parseRequest correctly handles multiple headers
//         String request = "GET /weather.json HTTP/1.1\nHost: localhost\nUser-Agent: TestClient\nLamport-Clock: 10\n\n";
//         ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
//         HttpRequest result = parser.parseRequest(input);
//         System.out.println("testParseMultipleHeaders: headers=" + result.getHeaders());
//         assertEquals("Method should be GET", "GET", result.getMethod());
//         assertEquals("Path should be /weather.json", "/weather.json", result.getPath());
//         assertEquals("Headers should contain Host", "localhost", result.getHeaders().get("Host"));
//         assertEquals("Headers should contain User-Agent", "TestClient", result.getHeaders().get("User-Agent"));
//         assertEquals("Headers should contain Lamport-Clock", "10", result.getHeaders().get("Lamport-Clock"));
//         assertEquals("Body should be empty", "", result.getBody());
//     }
// }