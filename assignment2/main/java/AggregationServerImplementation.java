/*AggregationServer manages HTTP requests for weather data, maintains a persistent store, nd enforces lamport clock synchronisation. 
 *  Handles concurrent GET/PUT requests, data expiry, and crash recovery. 
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class AggregationServer implements AggregationServerImplementation  {

    private static final int DEFAULT_PORT = 4567;
    private static final String DATA_FILE = "weather_data.json";
    private static final String LOG_FILE = "transaction.log";
    private static final int EXPIRY_TIME_MS = 30000; // 30 seconds
    private static final int EXPIRY_CHECK_INTERVAL_MS = 10000; // 10 seconds
    private static long lamportClock = 0;
    // Data structures for storing weather data and last contact times
    // private static ConcurrentHashMap<String, WeatherData> weatherStore = new ConcurrentHashMap

    // private static ConcurrentHashMap<String, Long> lastContactTimes = new ConcurrentHashMap();


    //Starts server, parses port from CLI, initializes persistence.
    // @param args CLI arguments for port number
    public static void main (String[] args) {
        int port = DEFAULT_PORT;



        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }

    }

    private static void handleGetRequest(Socket socket) {

    }

    private static void handlePutRequest() {

    }



    


}
