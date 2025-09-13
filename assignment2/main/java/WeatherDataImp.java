public class WeatherDataImp {
    
}
/** 
 * WeatherData represent the weather data structure ith JSOn perations 
 * 
 */

 public interface WeatherData {
    private String id;
    private String name;
    private String state;
    private String time_zone;
    private long lat;
    private long lon;
    private String local_date_time;
    private String local_date_time_full;
    private double air_temp;
    private double apparent_t;
    private String cloud;
    private double dewpt;
    private double press;
    private int rel_hum;
    private String wind_dir;
    private int wind_spd_kmh;
    private int wind_spd_kt;    

    /**
     * converts weather data to JSON string
     * @return JSON representation of weather data
     */
    String toJson();

    /**
     * creates WeatherData from JSON string
     * @param json JSON string
     * @return WeatherData object
     */
    static WeatherData fromJson(String json) {
        // Implement JSON parsing logic here
        return null; // Placeholder
    }
 }