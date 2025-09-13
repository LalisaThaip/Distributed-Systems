/**
 * Reads and parses weather data from local file 
 * serves data to aggregation server via HTTP PUT requests
 * converts data to JSON format and sends PUT requests with lamport clock
 * handles errors gracefully and displays appropriate messages
 */
package assignment2;
import java.net.Socket;
import java.io.IOException;

public interface IContentServer {
    
    /**
     * Parses CLI arguments and initiates PUT requests with retries
     * reads files
     * @param args command-line arguments
     */
    void main(String[] args);

    /** 
     * Parses input file into weatherData object
     * @param filePath Path to input file
     * @return Parsed weather data object
     * @throws IOException if file reading fails.
     */
    WeatherData parseWeatherFile(String filePath) throws IOException;

    /** 
     * Constructs and Sends HTTP PUT request with Lamport clock
     * Initiates a PUT request to the specified server URL with the given weather data
     * @param socket Client socket connection
     * @param data Weather data to send
     * @throws IOException if sending fails.
     */
    void sendPutRequest(Socket socket, WeatherData data) throws IOException;

    /**
     * Extracts and updates Lamport clock from response headers 
     * @param response HTTP response from the server
     * @return Updated Lamport clock value
     */
    long updateLamportClock(String response);

    /**
     * Verifies data by sending GET request and comparing with sent data.
     * @param socket Client socket connection.
     * @param data Sent weather data.
     * @param stationId Station ID to verify.
     * @throws IOException if verification fails.
     */
    void verifyData(Socket socket, WeatherData data, String stationId) throws IOException;

    
    public int extractStatusCode(String response);

    


     
}