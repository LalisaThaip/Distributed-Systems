package assignment2;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface for ContentServer, responsible for parsing weather data files,
 * creating WeatherData objects, serializing to JSON, and sending to AggregationServer via PUT requests.
 */
public interface IContentServer {

    /**
     * Parses a weather data file and creates a WeatherData object.
     * @param filePath Path to the weather data file.
     * @return WeatherData object or null if parsing fails.
     */
    WeatherData parseWeatherFile(String filePath);

    /**
     * Sends a PUT request with the serialized WeatherData JSON to the AggregationServer.
     * @param socket Socket connected to the AggregationServer.
     * @param data WeatherData object to send.
     * @throws IOException If an I/O error occurs during the request.
     */
    void sendPutRequest(Socket socket, WeatherData data) throws IOException;

    /**
     * Updates the Lamport clock based on the server's response.
     * @param response HTTP response from the AggregationServer.
     * @return The received Lamport clock value.
     */
    long updateLamportClock(String response);

    /**
     * Extracts the HTTP status code from the server's response.
     * @param response HTTP response from the AggregationServer.
     * @return The HTTP status code.
     */
    int extractStatusCode(String response);
}