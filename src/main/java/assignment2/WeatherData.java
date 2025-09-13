package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeatherData implements IWeatherData {
    private String id;
    private String name;
    private String state;
    private String timeZone;
    private double lat;
    private double lon;
    private String localDateTime;
    private String localDateTimeFull;
    private double airTemp;
    private double apparentT;
    private String cloud;
    private double dewpt;
    private double press;
    private int relHum;
    private String windDir;
    private int windSpdKmh;
    private int windSpdKt;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLon() {
        return lon;
    }

    @Override
    public String getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public String getLocalDateTimeFull() {
        return localDateTimeFull;
    }

    @Override
    public double getAirTemp() {
        return airTemp;
    }

    @Override
    public double getApparentT() {
        return apparentT;
    }

    @Override
    public String getCloud() {
        return cloud;
    }

    @Override
    public double getDewpt() {
        return dewpt;
    }

    @Override
    public double getPress() {
        return press;
    }

    @Override
    public int getRelHum() {
        return relHum;
    }

    @Override
    public String getWindDir() {
        return windDir;
    }

    @Override
    public int getWindSpdKmh() {
        return windSpdKmh;
    }

    @Override
    public int getWindSpdKt() {
        return windSpdKt;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public void setLocalDateTimeFull(String localDateTimeFull) {
        this.localDateTimeFull = localDateTimeFull;
    }

    @Override
    public void setAirTemp(double airTemp) {
        this.airTemp = airTemp;
    }

    @Override
    public void setApparentT(double apparentT) {
        this.apparentT = apparentT;
    }

    @Override
    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    @Override
    public void setDewpt(double dewpt) {
        this.dewpt = dewpt;
    }

    @Override
    public void setPress(double press) {
        this.press = press;
    }

    @Override
    public void setRelHum(int relHum) {
        this.relHum = relHum;
    }

    @Override
    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    @Override
    public void setWindSpdKmh(int windSpdKmh) {
        this.windSpdKmh = windSpdKmh;
    }

    @Override
    public void setWindSpdKt(int windSpdKt) {
        this.windSpdKt = windSpdKt;
    }

    @Override
    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static WeatherData fromJson(String json) {
        try {
            return mapper.readValue(json, WeatherData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Override equals for verification
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WeatherData other = (WeatherData) obj;
        return id.equals(other.id) &&
            (name == null ? other.name == null : name.equals(other.name)) &&
            (state == null ? other.state == null : state.equals(other.state)) &&
            (timeZone == null ? other.timeZone == null : timeZone.equals(other.timeZone)) &&
            Double.compare(lat, other.lat) == 0 &&
            Double.compare(lon, other.lon) == 0 &&
            (localDateTime == null ? other.localDateTime == null : localDateTime.equals(other.localDateTime)) &&
            (localDateTimeFull == null ? other.localDateTimeFull == null : localDateTimeFull.equals(other.localDateTimeFull)) &&
            Double.compare(airTemp, other.airTemp) == 0 &&
            Double.compare(apparentT, other.apparentT) == 0 &&
            (cloud == null ? other.cloud == null : cloud.equals(other.cloud)) &&
            Double.compare(dewpt, other.dewpt) == 0 &&
            Double.compare(press, other.press) == 0 &&
            relHum == other.relHum &&
            (windDir == null ? other.windDir == null : windDir.equals(other.windDir)) &&
            windSpdKmh == other.windSpdKmh &&
            windSpdKt == other.windSpdKt;
    }
}