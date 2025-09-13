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

 public interface IWeatherDataStore {

   /**
    * stores weather data with a key 
    * @param  unique ey for the data 
    * @param data weather ot store 
    */
    void putData(String key, WeatherData data);

    /**
     * retreives weather data for a stiation ID or all data 
     * @param stationId optional station ID to filter data
     * @return JSON string of weather data
     */
     String getData(String stationId);

     /** 
      * removed rexpired data quese from aggregation server 
      * @param expirationTime time in milliseconds to consider data expired
      */
      void removeExpiredData(long expirationTime); 

      /** 
       * persist weather data to a json file 
       */
      void persist();

      /**
       * logs a transaction for crash recovery 
       * @param data weather data to log
       */
      void logTransaction(WeatherData data);

      /** 
       * recover state from persistent storage 
       */
      void recover();

      /** 
       * checks if a key exists in the data store 
       * @param key unique key to check
       * @return true if key exists, false otherwise
       */
      boolean containsKey(String key);


 }
