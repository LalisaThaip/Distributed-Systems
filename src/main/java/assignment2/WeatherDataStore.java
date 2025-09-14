package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class WeatherDataStore implements IWeatherDataStore {
    private final Map<String, IWeatherData> stations = new ConcurrentHashMap<>();
    private final Map<String, Long> lastContacts = new ConcurrentHashMap<>();
    private static final String DATA_FILE = "weather_data.json";
    private static final String TEMP_FILE = "weather_data.temp.json";
    private static final int MAX_STATIONS = 20;
    private final ReentrantLock lock = new ReentrantLock();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void putData(String key, IWeatherData data) {
        lock.lock();
        try {
            stations.put(key, data);
            lastContacts.put(key, System.currentTimeMillis());
            persist();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getData(String stationId) {
        lock.lock();
        try {
            if (stationId == null) {
                String result =  mapper.writeValueAsString(new ArrayList<>(stations.values()));
                System.out.println("getData(null) result: " + result);
                return result;
            } else {
                IWeatherData data = stations.get(stationId);
                return data != null ? data.toJson() : "{}";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeExpiredData(long expirationThreshold) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            List<String> toRemove = new ArrayList<>();
            for (Map.Entry<String, Long> entry : lastContacts.entrySet()) {
                if (now - entry.getValue() > expirationThreshold) {
                    toRemove.add(entry.getKey());
                }
            }
            for (String key : toRemove) {
                stations.remove(key);
                lastContacts.remove(key);
            }
            // Limit to 20 most recent
            if (stations.size() > MAX_STATIONS) {
                List<Map.Entry<String, Long>> sorted = new ArrayList<>(lastContacts.entrySet());
                sorted.sort(Comparator.comparingLong(Map.Entry<String, Long>::getValue).reversed());
                for (int i = MAX_STATIONS; i < sorted.size(); i++) {
                    String key = sorted.get(i).getKey();
                    stations.remove(key);
                    lastContacts.remove(key);
                }
            }
            persist();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void persist() {
        lock.lock();
        try {
            Map<String, Object> root = new HashMap<>();
            root.put("stations", stations);
            root.put("lastContacts", lastContacts);
            File temp = new File(TEMP_FILE);
            mapper.writeValue(temp, root);
            System.out.println("Persisted to " + TEMP_FILE);
            Files.move(Paths.get(TEMP_FILE), Paths.get(DATA_FILE), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            System.out.println("Moved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Persist error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }



    @Override
    public void recover() {
        lock.lock();
        try {
            File file = new File(DATA_FILE);
            System.out.println("Recovering from " + DATA_FILE + ", exists: " + file.exists());
            if (file.exists()) {
                Map<String, Object> root = mapper.readValue(file, Map.class);
                stations.putAll((Map<String, IWeatherData>) root.get("stations"));
                lastContacts.putAll((Map<String, Long>) root.get("lastContacts"));
            }
        } catch (IOException e) {
            System.err.println("Recover error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        lock.lock();
        try {
            return stations.containsKey(key);
        } finally {
            lock.unlock();
        }
    }
}