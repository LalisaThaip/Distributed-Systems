package assignment2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AggregationServer implements IAggregationServer to handle HTTP GET and PUT requests,
 * manage weather data with Lamport clocks, persist data to a JSON file, and handle data expiry.
 */
public class AggregationServer implements IAggregationServer {
    private static final int DEFAULT_PORT = 4567;
    private static final long EXPIRY_TIME = 30000; // 30 seconds for data expiry
    private final ReentrantLock lock = new ReentrantLock(); // For thread-safe operations
    private final ILamportClock lamportClock = new LamportClock();
    private final IWeatherDataStore dataStore = new WeatherDataStore();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public AggregationServer(int port) throws IOException {
        dataStore.recover();
        scheduler.scheduleAtFixedRate(() -> dataStore.removeExpiredData(EXPIRY_TIME), EXPIRY_TIME, 10000, TimeUnit.MILLISECONDS);
        serverSocket = new ServerSocket(port);
        System.out.println("Aggregation Server started on port " + port);
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleRequest(clientSocket));
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                throw e;
            }
        } finally {
            shutdown();
        }
    }

    // Graceful shutdown of server and resources
    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        try {
            new AggregationServer(port);
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    @Override
    public void handleRequest(Socket socket) {
        try {
            new RequestHandler(lamportClock, dataStore).handleRequest(socket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleGet(Socket socket) {
        // Not used; handled by RequestHandler
    }

    @Override
    public void handlePut(Socket socket) {
        // Not used; handled by RequestHandler
    }

    @Override
    public void expireData() {
        // Handled by WeatherDataStore
    }

    @Override
    public void persistData(IWeatherData data) {
        // Handled by WeatherDataStore
    }

    @Override
    public void recoverFromLog() {
        // Handled by WeatherDataStore
    }

    @Override
    public void loadPersistedData() {
        // Handled by WeatherDataStore
    }

    // For testing
    public IWeatherDataStore getDataStore() {
        return dataStore;
    }
}