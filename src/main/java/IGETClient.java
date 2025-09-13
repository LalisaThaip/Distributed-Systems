/**
 * GETClient interface for fetching weather data from the server.
 * display weather data in human-readable format
 * this class parses CLI args for server URL and station ID
 * sends https GET requests with lamport clock to the server
 * handle errors gracefully and display appropriate messages

*/
public interface IGETClient {

    /** 
     * Parses CLI arguments and initiates GET request with retries
     */
    void main(String[] args);

    /**
     * Constructs and sends GET request with Lamport clock 
     * Initiates a GET request to the specified server URL for the given station ID
     * to fetch weather data
     * @param Socket Client socket connection
     * @param serverUrl The URL of the aggregation server.
     * @param stationId The ID of the weather station to fetch data for. (optional)
     */
    void sendGetRequest(Socket socket, String serverUrl);

    /**
     * Displays weather data from the server response
     * @param response HTTP response containing JSON string of weather data
     */
    void displayWeatherData(String response);

    /**
     * Extracts and updates Lamport clock from response headers
     * @param response HTTP response from the server
     * @return Updated Lamport clock value
     */
    long updateLamportClock(String response);

    /**
     * Extract host from URL
     * @param url The URL string
     * @return The host part of the URL
     *
     */
    String extractHost(String url);

    /**
     * Extract port from URL    
     * @param url
     * @return
     */
    String extractPort(String url);


}

