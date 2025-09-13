/** 
 * WeatherData represent the weather data structure ith JSOn perations 
 * 
 */
package assignment2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

 public class WeatherData implements IWeatherData {

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
    private static final Gson gson = new GsonBuilder().create();


    // Getter and setter methods for each field 

    public String getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public String getTimeZone() { return time_zone; }
    public long getLat() { return lat; }
    public long getLon() { return lon; }
    public String getLocalDateTime() { return local_date_time; }
    public String getLocalDateTimeFull() { return local_date_time_full; }
    public double getAirTemp() { return air_temp; }           
    public double getApparentT() { return apparent_t; }
    public String getCloud() { return cloud; }
    public double getDewpt() { return dewpt; }
    public double getPress() { return press; }
    public int getRelHum() { return rel_hum; }
    public String getWindDir() { return wind_dir; }
    public int getWindSpdKmh() { return wind_spd_kmh; }
    public int getWindSpdKt() { return wind_spd_kt; }


    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setState(String state) { this.state = state; }
    public void setTimeZone(String time_zone) { this.time_zone = time_zone; }
    public void setLat(long lat) { this.lat = lat; }
    public void setLon(long lon) { this.lon = lon; }
    public void setLocalDateTime(String local_date_time) { this.local_date_time = local_date_time; }
    public void setLocalDateTimeFull(String local_date_time_full) { this.local_date_time_full = local_date_time_full; }
    public void setAirTemp(double air_temp) { this.air_temp = air_temp; }           
    public void setApparentT(double apparent_t) { this.apparent_t = apparent_t; }
    public void setCloud(String cloud) { this.cloud = cloud; }
    public void setDewpt(double dewpt) { this.dewpt = dewpt; }
    public void setPress(double press) { this.press = press; }
    public void setRelHum(int rel_hum) { this.rel_hum = rel_hum; }
    public void setWindDir(String wind_dir) { this.wind_dir = wind_dir; }
    public void setWindSpdKmh(int wind_spd_kmh) { this.wind_spd_kmh = wind_spd_kmh; } 
    public void setWindSpdKt(int wind_spd_kt) { this.wind_spd_kt = wind_spd_kt; }


    /** Converts Weather data */

    /**
     * converts weather data to JSON string
     * @return JSON representation of weather data
     */
    public String toJson(){
        return gson.toJson(this); 
    }

    /**
     * creates WeatherData from JSON string
     * @param json JSON string
     * @return WeatherData object
     */
    public static WeatherData fromJson(String json) {
        try {
            return gson.fromJson(json, WeatherData.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON: " + e.getMessage());
        }
    }

    public static WeatherData[] fromJsonArray(String json) {
        try {
            Type listType = new TypeToken<WeatherData[]>() {}.getType();
            WeatherData[] array = gson.fromJson(json, listType);
            return array != null ? array : new WeatherData[0];
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON array: " + e.getMessage());
        }
    }
}