package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

/**
 * AggregationServer implements IAggregationServer to handle HTTP GET and PUT requests,
 * manage weather data with Lamport clocks, persist data to a JSON file, and handle data expiry.
 * It uses a thread pool for concurrent request handling and ensures thread-safety with locks.
 */
public class AggregationServer implements IAggregationServer {
    // Constants for default port, file paths, and expiry settings
    private static final int DEFAULT_PORT = 4567;
    private static final String DATA_FILE = "weather_data.json";
    private static final String TEMP_FILE = "weather_data.temp.json";
    private static final String TRANSACTION_LOG = "transaction.log";
    private static final long EXPIRY_TIME = 30000; // 30 seconds for data expiry
    private static final int MAX_STATIONS = 20; // Maximum number of stations to keep

    // Thread-safe data structures for storing weather data and last contact times
    private final ConcurrentHashMap<String, IWeatherData> stations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastContacts = new ConcurrentHashMap<>();

    // Lock for thread-safe operations on shared resources
    private final ReentrantLock lock = new ReentrantLock();

    // Lamport clock for event ordering
    private final ILamportClock lamportClock = new LamportClock();

    // JSON mapper for serialization/deserialization
    private final ObjectMapper mapper = new ObjectMapper();

    // HTTP request parser and response builder
    private final IHttpReqParser reqParser = new HttpReqParser();
    private final IHttpResBuilder resBuilder = new HttpResBuilder();

    // Thread pool for handling client connections
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Scheduler for periodic data expiry
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructor: Initializes the server, loads persisted data, starts the expiry scheduler,
     * and listens for client connections.
     * @param port The port to listen on (default: 4567).
     * @throws IOException If server socket creation or file operations fail.
     */
    public AggregationServer(int port) throws IOException {
        // Load any existing data from persistent storage
        loadPersistedData();

        // Schedule data expiry task to run every 10 seconds
        scheduler.scheduleAtFixedRate(this::expireData, EXPIRY_TIME, 10000, TimeUnit.MILLISECONDS);

        // Start server socket to listen for incoming connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Aggregation Server started on port " + port);
            while (true) {
                // Accept client connections and handle them in separate threads
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleRequest(clientSocket));
            }
        } finally {
            // Cleanup resources on server shutdown
            executorService.shutdown();
            scheduler.shutdown();
        }
    }

    /**
     * Main method: Starts the server with a configurable port.
     * @param args Command-line arguments (optional port number).
     */
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        try {
            new AggregationServer(port);
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Handles incoming HTTP requests by parsing them and delegating to GET or PUT handlers.
     * Updates the Lamport clock and ensures thread-safety with a lock.
     * @param socket Client socket connection.
     */
    @Override
    public void handleRequest(Socket socket) {
        try {
            // Parse the incoming HTTP request
            HttpRequest request = reqParser.parseRequest(socket.getInputStream());

            // Extract and update Lamport clock from request headers
            long receivedClock = request.getHeaders().containsKey("Lamport-Clock") ?
                    Long.parseLong(request.getHeaders().get("Lamport-Clock")) : 0;
            lock.lock();
            try {
                lamportClock.update(receivedClock);

                // Delegate based on HTTP method
                String method = request.getMethod();
                if ("GET".equals(method)) {
                    handleGet(socket);
                } else if ("PUT".equals(method)) {
                    handlePut(socket);
                } else {
                    // Invalid method: Return 400 Bad Request
                    lamportClock.increment();
                    resBuilder.sendResponse(socket, 400, null, lamportClock.getClock());
                }
            } finally {
                lock.unlock();
            }
        } catch (IOException e) {
            // Handle parsing or connection errors with 500 Internal Server Error
            try {
                lock.lock();
                try {
                    lamportClock.increment();
                    resBuilder.sendResponse(socket, 500, null, lamportClock.getClock());
                } finally {
                    lock.unlock();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            // Ensure socket is closed
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles HTTP GET requests by returning weather data for a specific station or all stations.
     * @param socket Client socket connection.
     */
    @Override
    public void handleGet(Socket socket) {
        try {
            // Parse request again (interface requirement, though already parsed in handleRequest)
            HttpRequest request = reqParser.parseRequest(socket.getInputStream());
            String stationId = null;

            // Extract station ID from query parameter if present
            if (request.getPath().contains("?")) {
                String query = request.getPath().substring(request.getPath().indexOf("?") + 1);
                if (query.startsWith("id=")) {
                    stationId = query.substring(3);
                }
            }

            // Retrieve data from storage
            lock.lock();
            try {
                String body = getData(stationId);
                lamportClock.increment();
                resBuilder.sendResponse(socket, 200, body, lamportClock.getClock());
            } finally {
                lock.unlock();
            }
        } catch (IOException e) {
            // Handle errors with 500 status
            lock.lock();
            try {
                lamportClock.increment();
                resBuilder.sendResponse(socket, 500, null, lamportClock.getClock());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Handles HTTP PUT requests by storing weather data and updating persistence.
     * @param socket Client socket connection.
     */
    @Override
    public void handlePut(Socket socket) {
        try {
            // Parse request again (interface requirement)
            HttpRequest request = reqParser.parseRequest(socket.getInputStream());

            // Check for empty body
            if (request.getBody().isEmpty()) {
                lock.lock();
                try {
                    lamportClock.increment();
                    resBuilder.sendResponse(socket, 204, null, lamportClock.getClock());
                } finally {
                    lock.unlock();
                }
                return;
            }

            // Parse and validate JSON data
            IWeatherData data = WeatherData.fromJson(request.getBody());
            String key = data.getId();
            if (key == null) {
                lock.lock();
                try {
                    lamportClock.increment();
                    resBuilder.sendResponse(socket, 400, null, lamportClock.getClock());
                } finally {
                    lock.unlock();
                }
                return;
            }

            // Store data and update persistence
            lock.lock();
            try {
                boolean isNew = !stations.containsKey(key);
                logTransaction(data); // Log before applying
                stations.put(key, data);
                lastContacts.put(key, System.currentTimeMillis());
                persistData(data);
                lamportClock.increment();
                resBuilder.sendResponse(socket, isNew ? 201 : 200, null, lamportClock.getClock());
            } finally {
                lock.unlock();
            }
        } catch (IOException e) {
            // Handle JSON parsing or IO errors with 500 status
            lock.lock();
            try {
                lamportClock.increment();
                resBuilder.sendResponse(socket, 500, null, lamportClock.getClock());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Periodically removes stale data (older than 30 seconds) and limits to 20 recent stations.
     */
    @Override
    public void expireData() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            List<String> toRemove = new ArrayList<>();

            // Identify expired entries (older than 30 seconds)
            for (Map.Entry<String, Long> entry : lastContacts.entrySet()) {
                if (entry.getValue() == null || (now - entry.getValue() > EXPIRY_TIME)) {
                    toRemove.add(entry.getKey());
                }
            }

            // Remove expired entries
            for (String key : toRemove) {
                stations.remove(key);
                lastContacts.remove(key);
            }

            // Limit to 20 most recent stations
            if (stations.size() > MAX_STATIONS) {
                List<Map.Entry<String, Long>> sorted = new ArrayList<>();
                // Filter out null values to avoid Comparator issues
                for (Map.Entry<String, Long> entry : lastContacts.entrySet()) {
                    if (entry.getValue() != null) {
                        sorted.add(entry);
                    }
                }
                // Sort by timestamp in descending order (newest first)
                sorted.sort((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()));
                // Remove oldest entries beyond 20
                for (int i = MAX_STATIONS; i < sorted.size(); i++) {
                    String key = sorted.get(i).getKey();
                    stations.remove(key);
                    lastContacts.remove(key);
                }
            }

            // Persist updated state
            persistData(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Persists weather data to a JSON file atomically.
     * @param data Weather data to persist (null if persisting all data).
     */
    @Override
    public void persistData(IWeatherData data) {
        lock.lock();
        try {
            // Prepare data for JSON serialization
            Map<String, Object> root = new HashMap<>();
            root.put("stations", stations);
            root.put("lastContacts", lastContacts);

            // Write to a temporary file first for atomicity
            File temp = new File(TEMP_FILE);
            mapper.writeValue(temp, root);

            // Atomically move temp file to main file
            Files.move(Paths.get(TEMP_FILE), Paths.get(DATA_FILE), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Persistence error: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Recovers weather data from the transaction log to rebuild state after a crash.
     */
    @Override
    public void recoverFromLog() {
        lock.lock();
        try {
            File logFile = new File(TRANSACTION_LOG);
            if (!logFile.exists()) {
                return; // No log to recover from
            }

            // Read each line (JSON object) from the transaction log
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        IWeatherData data = WeatherData.fromJson(line);
                        String key = data.getId();
                        stations.put(key, data);
                        lastContacts.put(key, System.currentTimeMillis());
                    } catch (Exception e) {
                        System.err.println("Error recovering transaction: " + e.getMessage());
                    }
                }
            }

            // Persist recovered state to main JSON file
            persistData(null);
        } catch (IOException e) {
            System.err.println("Recovery error: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Loads persisted weather data from JSON file and transaction log on startup.
     */
    @Override
    public void loadPersistedData() {
        lock.lock();
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                // Load JSON file into memory
                Map<String, Object> root = mapper.readValue(file, Map.class);
                Map<String, IWeatherData> loadedStations = (Map<String, IWeatherData>) root.get("stations");
                Map<String, Long> loadedContacts = (Map<String, Long>) root.get("lastContacts");
                if (loadedStations != null) {
                    stations.putAll(loadedStations);
                }
                if (loadedContacts != null) {
                    lastContacts.putAll(loadedContacts);
                }
            }

            // Apply transaction log to ensure no data is lost
            recoverFromLog();
        } catch (IOException e) {
            System.err.println("Error loading persisted data: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Helper method to retrieve data for GET requests.
     * @param stationId Station ID to filter data (null for all data).
     * @return JSON string of weather data.
     * @throws IOException If JSON serialization fails.
     */
    private String getData(String stationId) throws IOException {
        lock.lock();
        try {
            if (stationId == null) {
                return mapper.writeValueAsString(new ArrayList<>(stations.values()));
            } else {
                IWeatherData data = stations.get(stationId);
                return data != null ? data.toJson() : "{}";
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Logs a transaction (PUT operation) to the transaction log for crash recovery.
     * @param data Weather data to log.
     */
    private void logTransaction(IWeatherData data) {
        lock.lock();
        try {
            try (FileWriter writer = new FileWriter(TRANSACTION_LOG, true)) {
                writer.write(data.toJson() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Transaction logging error: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
