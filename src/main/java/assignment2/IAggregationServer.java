package assignment2;

import java.net.Socket;

public interface IAggregationServer {
    /**
     * Processes incoming HTTP requests.
     * @param socket Client socket connection.
     */
    void handleRequest(Socket socket);

    /**
     * Handles HTTP GET requests, returning weather data.
     * @param socket Client socket connection.
     */
    void handleGet(Socket socket);

    /**
     * Handles HTTP PUT requests, storing weather data.
     * @param socket Client socket connection.
     */
    void handlePut(Socket socket);

    /**
     * Schedules periodic expiry of stale data from unresponsive content servers.
     */
    void expireData();

    /**
     * Persists weather data to a JSON file atomically.
     * @param data Weather data to persist (null if persisting all data).
     */
    void persistData(IWeatherData data);

    /**
     * Recovers weather data from transaction log on startup (logs PUT operations).
     */
    void recoverFromLog();

    /**
     * Loads weather data from persistent JSON file and transaction log on startup.
     */
    void loadPersistedData();
}