/*
 * Responsibilities:

Listen for HTTP GET and PUT requests on a configurable port (default: 4567).
Maintain a thread-safe store of weather data, keyed by content server ID and station ID.
Implement Lamport clocks for request ordering.
Persist data to a JSON file with crash recovery via a transaction log.
Expire data from content servers not contacted within 30 seconds.
Return appropriate HTTP status codes (200, 201, 204, 400, 500).

Implementation:

Socket Handling:
Use ServerSocket to listen on the specified port.
Create a thread pool using ExecutorService to handle incoming connections concurrently.
Parse HTTP requests manually, extracting method, path, headers, and body.

Lamport Clocks:
Maintain a local Lamport clock (long lamportClock) incremented on local events (e.g., receiving a request, processing a response).
Include the Lamport clock in request/response headers (e.g., X-Lamport-Clock).
Update the local clock on receiving a request: lamportClock = Math.max(localClock, receivedClock) + 1.
Serialize PUT requests using a ReentrantLock to ensure ordered processing based on Lamport timestamps.

Data Storage:
Use a ConcurrentHashMap<String, WeatherData> to store weather data, where the key is a combination of content server ID and station ID.
Persist data to a file (weather_data.json) using Jackson.
Maintain a transaction log (transaction.log) to record PUT operations before applying them, enabling crash recovery.

Crash Recovery:
On startup, read the transaction log to rebuild the in-memory state if the main JSON file is corrupted or incomplete.
Use a write-ahead logging approach: write PUT data to the log before updating the main file.

Expiry Mechanism:

Use a ScheduledExecutorService to run a task every 10 seconds, checking the last contact time of each content server (stored in a ConcurrentHashMap<String, Long>).
Remove entries from content servers with no contact within 30 seconds and update the persistent store.

HTTP Responses:

GET: Return the aggregated JSON data with status 200, or 204 if no data exists.
PUT: Validate JSON data. Return 201 for first-time uploads, 200 for updates, 400 for invalid requests, or 500 for invalid JSON.
Other methods: Return 400.
*/
package assignment2;

import java.net.Socket;


public interface IAggregationServer {


    /** 
     * processes incoming HTTP requests
     * @param socket Client socket connection 
    */
    void handleRequest(Socket socket);

    /**
     * Handles HTTP GET requests, returning weather data.
     * @param socket Client socket connection
     * @param request Parsed HTTP request details
     */
    void handleGet(Socket socket);

    /**
     * Handles HTTP PUT requests, storing weather data.
     * @param socket Client socket connection
     */
    void handlePut(Socket socket);

    /** 
     * Schedules periodic expiry of stale data from unresponsive content servers.
     */
    void expireData();

    /** 
     * Writes weather data to persistent JSON file.
     * @param data Weather data to persist
     */
    void persistData(WeatherData data);

    /** 
     * Recovers weather data from transaction log on startup (logs PUT operations)
     */
    void recoverFromLog();

    /**
     * loads weather data from persistent JSON file and transaction log on startup
     * @return WeatherData loaded from file
     */
    void loadPersistedData(); 




}
