package assignment2;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.*;


/** 
 * AggregationServer manages HTTP requests for weather data, 
 * maintains a persistent store, and enforces lamport clock synchronisation. 
 * Handles concurrent GET/PUT requests, data expiry, and crash recovery. 
 */


public class AggregationServer implements IAggregationServer  {
    private static final int DEFAULT_PORT = 4567;
    private static final int THREAD_POOL_SIZE = 10;
    private static final String DATA_FILE = "weather_data.json";
    private static final String LOG_FILE = "transaction.log";
    private static final int EXPIRY_TIME_MS = 30000; // 30 seconds
    private static final int EXPIRY_CHECK_INTERVAL_MS = 10000; // 10 seconds
    private final IWeatherDataStore dataStore;
    private final ILamportClock lamportClock;

    private static Map<String, Map

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
