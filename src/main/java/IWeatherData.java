/** 
 * interface for WeatherData, representing the weather data structure ith JSON operations 
 * 
 */

 public interface WeatherData {

    // Getters
    String getId();
    String getName();
    String getState();
    String getTimeZone();
    long getLat();
    long getLon();
    String getLocalDateTime();
    String getLocalDateTimeFull();
    double getAirTemp();
    double getApparentT();
    String getCloud();
    double getDewpt();
    double getPress();
    int getRelHum();
    String getWindDir();
    int getWindSpdKmh();
    int getWindSpdKt();

    // Setters
    void setId(String id);
    void setName(String name);
    void setState(String state);
    void setTimeZone(String time_zone);
    void setLat(long lat);

    void setLon(long lon);
    void setLocalDateTime(String local_date_time);
    void setLocalDateTimeFull(String local_date_time_full);
    void setAirTemp(double air_temp);
    void setApparentT(double apparent_t);
    void setCloud(String cloud);
    void setDewpt(double dewpt);
    void setPress(double press);
    void setRelHum(int rel_hum);
    void setWindDir(String wind_dir);
    void setWindSpdKmh(int wind_spd_kmh);
    void setWindSpdKt(int wind_spd_kt);

    /**
     * Converts the weather data to a JSON string.
     * @return JSON representation.
     */
    String toJson();

    /**
     * Parses a JSON string into a WeatherData object.
     * @param json JSON string.
     * @return WeatherData object.
     */
    IWeatherData fromJson(String json);

    /**
     * Parses a JSON array into an array of WeatherData objects.
     * @param json JSON array string.
     * @return Array of WeatherData objects.
     */
    IWeatherData[] fromJsonArray(String json);

 }