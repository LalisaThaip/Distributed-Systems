/**
 * The WeatherDataStore (and its interface IWeatherDataStore) 
 * is a core component in the weather data aggregation system, 
 * serving as a thread-safe repository for managing, persisting, 
 * and maintaining weather data received from content servers via 
 * PUT requests.
 * 
 * It uses a ConcurrentHashMap<String, IWeatherData> to 
 * store weather data entries in memory, where each entry 
 * is keyed by a combination of the content server's ID 
 * (e.g., IP address) and the station ID (e.g., "unknown:IDS60901").
 * 
 * persist aggregated data to a json file via the put operation 
 */
package assignment2;

public interface IWeatherDataStore {
    /**
     * Stores weather data with a key (thread-safe).
     * @param key Unique key for the data (e.g., station ID).
     * @param data Weather data to store.
     */
    void putData(String key, IWeatherData data);

    /**
     * Retrieves weather data for a station ID or all data (thread-safe).
     * @param stationId Optional station ID to filter data; null for all data.
     * @return JSON string of weather data.
     */
    String getData(String stationId);

    /**
     * Removes expired data based on last contact time (thread-safe).
     * @param expirationThreshold Time in milliseconds to consider data expired (e.g., 30000).
     */
    void removeExpiredData(long expirationThreshold);

    /**
     * Persists weather data to a JSON file atomically (thread-safe).
     */
    void persist();

    /**
     * Recovers state from persistent storage (thread-safe).
     */
    void recover();

    /**
     * Checks if a key exists in the data store (thread-safe).
     * @param key Unique key to check.
     * @return True if key exists, false otherwise.
     */
    boolean containsKey(String key);

    /**
     * Limits stored data to the most recent entries (e.g., 20 stations) (thread-safe).
     * @param maxSize Maximum number of entries to keep.
     */
    //void limitToRecent(int maxSize);
}