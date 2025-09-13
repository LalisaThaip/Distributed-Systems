package assignment2;

public interface IWeatherData {
    // Getters
    String getId();
    String getName();
    String getState();
    String getTimeZone();
    double getLat();
    double getLon();
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
    void setTimeZone(String timeZone);
    void setLat(double lat);
    void setLon(double lon);
    void setLocalDateTime(String localDateTime);
    void setLocalDateTimeFull(String localDateTimeFull);
    void setAirTemp(double airTemp);
    void setApparentT(double apparentT);
    void setCloud(String cloud);
    void setDewpt(double dewpt);
    void setPress(double press);
    void setRelHum(int relHum);
    void setWindDir(String windDir);
    void setWindSpdKmh(int windSpdKmh);
    void setWindSpdKt(int windSpdKt);

    /**
     * Converts the weather data to a JSON string.
     * @return JSON representation.
     */
    String toJson();

    /**
     * Creates a WeatherData instance from a JSON string.
     * @param json JSON string.
     * @return WeatherData instance.
     */
    static IWeatherData fromJson(String json);
}