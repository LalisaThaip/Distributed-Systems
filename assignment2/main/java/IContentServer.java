/**
 * Reads and parses weather data from local file 
 * serves data to aggregation server via HTTP PUT requests
 * converts data to JSON format and sends PUT requests with lamport clock
 * handles errors gracefully and displays appropriate messages
 */

public interface IContentServer {
    
    /**
     * Parses CLI arguments and initiates PUT requests with retries
     * reads files
     * @param args
     */
    void main(String[] args);

    /** 
     * Parses input file into weatherData object
     * @param filePath Path to input file
     * @return Parsed weather data object
     */
    WeatherData parseWeatherFile(String filePath);

    /** 
     * Constructs and Sends HTTP PUT request with Lamport clock
     * Initiates a PUT request to the specified server URL with the given weather data
     * @param socket Client socket connection
     * @param data Weather data to send
     * @param serverUrl The URL of the aggregation server. ???
     */
    void sendPutRequest(Socket socket, WeatherData data, String serverUrl);

    /**
     * Extracts and updates Lamport clock from response headers 
     * @param response HTTP response from the server
     * @return Updated Lamport clock value
     */
    long updateLamportClock(String response);

    /**
     * Extracts HTTP status code from response
     * @param response HTTP response from the server
     *
     */
}
