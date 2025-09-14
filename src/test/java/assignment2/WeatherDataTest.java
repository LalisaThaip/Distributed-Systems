package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class WeatherDataTest {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        // Initialize ObjectMapper for JSON serialization/deserialization
        mapper = new ObjectMapper();
    }

    @After
    public void tearDown() {
        // No cleanup needed, but included for consistency with AggregationServerTest
    }

    // Test getters and setters for all fields
    @Test
    public void testGettersAndSetters() {
        // Purpose: Verify that getters and setters work correctly for all WeatherData fields
        WeatherData data = new WeatherData();
        data.setId("station1");
        data.setName("Station One");
        data.setState("CA");
        data.setTimeZone("PST");
        data.setLat(37.7749);
        data.setLon(-122.4194);
        data.setLocalDateTime("2023-10-01 12:00");
        data.setLocalDateTimeFull("2023-10-01 12:00:00");
        data.setAirTemp(20.5);
        data.setApparentT(21.0);
        data.setCloud("Clear");
        data.setDewpt(15.0);
        data.setPress(1013.25);
        data.setRelHum(65);
        data.setWindDir("NW");
        data.setWindSpdKmh(10);
        data.setWindSpdKt(5);

        assertEquals("ID should be station1", "station1", data.getId());
        assertEquals("Name should be Station One", "Station One", data.getName());
        assertEquals("State should be CA", "CA", data.getState());
        assertEquals("TimeZone should be PST", "PST", data.getTimeZone());
        assertEquals("Latitude should be 37.7749", 37.7749, data.getLat(), 0.0001);
        assertEquals("Longitude should be -122.4194", -122.4194, data.getLon(), 0.0001);
        assertEquals("LocalDateTime should be 2023-10-01 12:00", "2023-10-01 12:00", data.getLocalDateTime());
        assertEquals("LocalDateTimeFull should be 2023-10-01 12:00:00", "2023-10-01 12:00:00", data.getLocalDateTimeFull());
        assertEquals("AirTemp should be 20.5", 20.5, data.getAirTemp(), 0.0001);
        assertEquals("ApparentT should be 21.0", 21.0, data.getApparentT(), 0.0001);
        assertEquals("Cloud should be Clear", "Clear", data.getCloud());
        assertEquals("Dewpt should be 15.0", 15.0, data.getDewpt(), 0.0001);
        assertEquals("Press should be 1013.25", 1013.25, data.getPress(), 0.0001);
        assertEquals("RelHum should be 65", 65, data.getRelHum());
        assertEquals("WindDir should be NW", "NW", data.getWindDir());
        assertEquals("WindSpdKmh should be 10", 10, data.getWindSpdKmh());
        assertEquals("WindSpdKt should be 5", 5, data.getWindSpdKt());
    }

    // Test JSON serialization
    @Test
    public void testToJson() throws Exception {
        // Purpose: Verify that toJson serializes all fields correctly
        WeatherData data = new WeatherData();
        data.setId("station1");
        data.setName("Station One");
        data.setState("CA");
        data.setTimeZone("PST");
        data.setLat(37.7749);
        data.setLon(-122.4194);
        data.setLocalDateTime("2023-10-01 12:00");
        data.setLocalDateTimeFull("2023-10-01 12:00:00");
        data.setAirTemp(20.5);
        data.setApparentT(21.0);
        data.setCloud("Clear");
        data.setDewpt(15.0);
        data.setPress(1013.25);
        data.setRelHum(65);
        data.setWindDir("NW");
        data.setWindSpdKmh(10);
        data.setWindSpdKt(5);

        String json = data.toJson();
        System.out.println("testToJson result: " + json);
        assertTrue("JSON should contain id", json.contains("\"id\":\"station1\""));
        assertTrue("JSON should contain name", json.contains("\"name\":\"Station One\""));
        assertTrue("JSON should contain state", json.contains("\"state\":\"CA\""));
        assertTrue("JSON should contain timeZone", json.contains("\"timeZone\":\"PST\""));
        assertTrue("JSON should contain lat", json.contains("\"lat\":37.7749"));
        assertTrue("JSON should contain lon", json.contains("\"lon\":-122.4194"));
        assertTrue("JSON should contain localDateTime", json.contains("\"localDateTime\":\"2023-10-01 12:00\""));
        assertTrue("JSON should contain localDateTimeFull", json.contains("\"localDateTimeFull\":\"2023-10-01 12:00:00\""));
        assertTrue("JSON should contain airTemp", json.contains("\"airTemp\":20.5"));
        assertTrue("JSON should contain apparentT", json.contains("\"apparentT\":21.0"));
        assertTrue("JSON should contain cloud", json.contains("\"cloud\":\"Clear\""));
        assertTrue("JSON should contain dewpt", json.contains("\"dewpt\":15.0"));
        assertTrue("JSON should contain press", json.contains("\"press\":1013.25"));
        assertTrue("JSON should contain relHum", json.contains("\"relHum\":65"));
        assertTrue("JSON should contain windDir", json.contains("\"windDir\":\"NW\""));
        assertTrue("JSON should contain windSpdKmh", json.contains("\"windSpdKmh\":10"));
        assertTrue("JSON should contain windSpdKt", json.contains("\"windSpdKt\":5"));
    }

    // Test JSON deserialization
    @Test
    public void testFromJson() throws Exception {
        // Purpose: Ensure fromJson correctly deserializes JSON into WeatherData
        String json = "{\"id\":\"station1\",\"name\":\"Station One\",\"state\":\"CA\",\"timeZone\":\"PST\"," +
                      "\"lat\":37.7749,\"lon\":-122.4194,\"localDateTime\":\"2023-10-01 12:00\"," +
                      "\"localDateTimeFull\":\"2023-10-01 12:00:00\",\"airTemp\":20.5,\"apparentT\":21.0," +
                      "\"cloud\":\"Clear\",\"dewpt\":15.0,\"press\":1013.25,\"relHum\":65,\"windDir\":\"NW\"," +
                      "\"windSpdKmh\":10,\"windSpdKt\":5}";
        WeatherData data = WeatherData.fromJson(json);
        System.out.println("testFromJson result: " + data.toJson());
        assertEquals("ID should be station1", "station1", data.getId());
        assertEquals("Name should be Station One", "Station One", data.getName());
        assertEquals("State should be CA", "CA", data.getState());
        assertEquals("TimeZone should be PST", "PST", data.getTimeZone());
        assertEquals("Latitude should be 37.7749", 37.7749, data.getLat(), 0.0001);
        assertEquals("Longitude should be -122.4194", -122.4194, data.getLon(), 0.0001);
        assertEquals("LocalDateTime should be 2023-10-01 12:00", "2023-10-01 12:00", data.getLocalDateTime());
        assertEquals("LocalDateTimeFull should be 2023-10-01 12:00:00", "2023-10-01 12:00:00", data.getLocalDateTimeFull());
        assertEquals("AirTemp should be 20.5", 20.5, data.getAirTemp(), 0.0001);
        assertEquals("ApparentT should be 21.0", 21.0, data.getApparentT(), 0.0001);
        assertEquals("Cloud should be Clear", "Clear", data.getCloud());
        assertEquals("Dewpt should be 15.0", 15.0, data.getDewpt(), 0.0001);
        assertEquals("Press should be 1013.25", 1013.25, data.getPress(), 0.0001);
        assertEquals("RelHum should be 65", 65, data.getRelHum());
        assertEquals("WindDir should be NW", "NW", data.getWindDir());
        assertEquals("WindSpdKmh should be 10", 10, data.getWindSpdKmh());
        assertEquals("WindSpdKt should be 5", 5, data.getWindSpdKt());
    }

    // Test JSON deserialization with null fields
    @Test
    public void testFromJsonWithNullFields() throws Exception {
        // Purpose: Verify that fromJson handles missing fields by setting them to null or default values
        String json = "{\"id\":\"station1\"}";
        WeatherData data = WeatherData.fromJson(json);
        System.out.println("testFromJsonWithNullFields result: " + data.toJson());
        assertEquals("ID should be station1", "station1", data.getId());
        assertNull("Name should be null", data.getName());
        assertNull("State should be null", data.getState());
        assertNull("TimeZone should be null", data.getTimeZone());
        assertEquals("Latitude should be 0.0", 0.0, data.getLat(), 0.0001);
        assertEquals("Longitude should be 0.0", 0.0, data.getLon(), 0.0001);
        assertNull("LocalDateTime should be null", data.getLocalDateTime());
        assertNull("LocalDateTimeFull should be null", data.getLocalDateTimeFull());
        assertEquals("AirTemp should be 0.0", 0.0, data.getAirTemp(), 0.0001);
        assertEquals("ApparentT should be 0.0", 0.0, data.getApparentT(), 0.0001);
        assertNull("Cloud should be null", data.getCloud());
        assertEquals("Dewpt should be 0.0", 0.0, data.getDewpt(), 0.0001);
        assertEquals("Press should be 0.0", 0.0, data.getPress(), 0.0001);
        assertEquals("RelHum should be 0", 0, data.getRelHum());
        assertNull("WindDir should be null", data.getWindDir());
        assertEquals("WindSpdKmh should be 0", 0, data.getWindSpdKmh());
        assertEquals("WindSpdKt should be 0", 0, data.getWindSpdKt());
    }

    // Test equals method with identical objects
    @Test
    public void testEqualsIdenticalObjects() {
        // Purpose: Ensure equals returns true for identical WeatherData objects
        WeatherData data1 = new WeatherData();
        data1.setId("station1");
        data1.setName("Station One");
        data1.setState("CA");
        WeatherData data2 = new WeatherData();
        data2.setId("station1");
        data2.setName("Station One");
        data2.setState("CA");
        assertTrue("Identical objects should be equal", data1.equals(data2));
    }

    // Test equals method with different objects
    @Test
    public void testEqualsDifferentObjects() {
        // Purpose: Verify that equals returns false for different WeatherData objects
        WeatherData data1 = new WeatherData();
        data1.setId("station1");
        data1.setName("Station One");
        WeatherData data2 = new WeatherData();
        data2.setId("station2");
        data2.setName("Station Two");
        assertFalse("Different objects should not be equal", data1.equals(data2));
    }

    // Test equals method with null fields
    @Test
    public void testEqualsWithNullFields() {
        // Purpose: Ensure equals handles null fields correctly
        WeatherData data1 = new WeatherData();
        data1.setId("station1");
        WeatherData data2 = new WeatherData();
        data2.setId("station1");
        assertTrue("Objects with null fields should be equal if IDs match", data1.equals(data2));
    }
}