package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class WeatherDataStoreTest {
    private WeatherDataStore store;
    private WeatherData data;
    private static final String DATA_FILE = "weather_data.json";
    private static final String TEMP_FILE = "weather_data.temp.json";
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        // Initialize WeatherDataStore and clean up files
        store = new WeatherDataStore();
        data = new WeatherData();
        data.setId("station1");
        data.setName("Station One");
        data.setState("CA");
        mapper = new ObjectMapper();
        new File(DATA_FILE).delete();
        new File(TEMP_FILE).delete();
    }

    @After
    public void tearDown() {
        // Clean up files after each test
        new File(DATA_FILE).delete();
        new File(TEMP_FILE).delete();
    }

    // Test storing and retrieving data for a single station
    @Test
    public void testPutAndGetDataSingleStation() throws IOException {
        // Purpose: Verify that putData stores data and getData retrieves it correctly
        store.putData("station1", data);
        String result = store.getData("station1");
        System.out.println("testPutAndGetDataSingleStation result: " + result);
        assertTrue("Result should contain station1", result.contains("station1"));
        assertTrue("Result should contain Station One", result.contains("Station One"));
    }

    // Test retrieving all stations
    @Test
    public void testGetAllStations() throws IOException {
        // Purpose: Ensure getData(null) returns all stored stations as a JSON array
        store.putData("station1", data);
        String result = store.getData(null);
        System.out.println("testGetAllStations result: " + result);
        assertTrue("Result should be a JSON array", result.startsWith("["));
        assertTrue("Result should contain station1", result.contains("station1"));
    }

    // Test data expiration
    @Test
    public void testRemoveExpiredData() throws IOException, InterruptedException {
        // Purpose: Verify that removeExpiredData removes data older than the threshold
        store.putData("station1", data);
        Thread.sleep(100); // Ensure some time passes
        store.removeExpiredData(0); // Immediate expiry
        String result = store.getData(null);
        System.out.println("testRemoveExpiredData result: " + result);
        assertTrue("Result should be empty array after expiry", result.equals("[]"));
    }

    // Test persistence to file
    @Test
    public void testPersistData() throws IOException {
        // Purpose: Ensure putData persists data to weather_data.json
        store.putData("station1", data);
        File file = new File(DATA_FILE);
        System.out.println("testPersistData file exists: " + file.exists());
        assertTrue("weather_data.json should exist after persist", file.exists());
        String content = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
        System.out.println("testPersistData file content: " + content);
        assertTrue("File should contain station1", content.contains("station1"));
        assertTrue("File should contain lastContacts", content.contains("lastContacts"));
    }

    // Test recovery from file
    @Test
    public void testRecoverData() throws IOException {
        // Purpose: Verify that recover restores data from weather_data.json
        store.putData("station1", data);
        WeatherDataStore newStore = new WeatherDataStore();
        newStore.recover();
        String result = newStore.getData("station1");
        System.out.println("testRecoverData result: " + result);
        assertTrue("Recovered data should contain station1", result.contains("station1"));
    }

    // Test containsKey method
    @Test
    public void testContainsKey() {
        // Purpose: Ensure containsKey correctly identifies stored keys
        store.putData("station1", data);
        assertTrue("Should contain station1", store.containsKey("station1"));
        assertFalse("Should not contain station2", store.containsKey("station2"));
    }

    // Test logTransaction
    @Test
    public void testLogTransaction() {
        // Purpose: Verify that logTransaction does not throw errors
        try {
            store.logTransaction(data);
            assertTrue("logTransaction should complete without errors", true);
        } catch (Exception e) {
            fail("logTransaction should not throw an exception: " + e.getMessage());
        }
    }

    // Test maximum station limit
    @Test
    public void testMaxStationsLimit() throws IOException, InterruptedException {
        // Purpose: Ensure removeExpiredData limits stations to 20 by removing oldest
        for (int i = 1; i <= 22; i++) {
            WeatherData tempData = new WeatherData();
            tempData.setId("station" + i);
            tempData.setName("Station " + i);
            store.putData("station" + i, tempData);
            Thread.sleep(10); // Ensure different timestamps
        }
        store.removeExpiredData(1000); // Use high threshold to test max limit
        String result = store.getData(null);
        System.out.println("testMaxStationsLimit result: " + result);
        assertFalse("Should not contain station1 (oldest)", result.contains("station1"));
        assertTrue("Should contain station22 (newest)", result.contains("station22"));
    }
}