package assignment2;
//import java.io.*; 

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

public class CLIParser implements ICLIParser {
    
    @Override 
    public String parseServerUrl(String[] args) throws IllegalArgumentException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Server URL is required.");
        }
        String url = args[0];
        validateUrl(url);
        return url;
    }

    @Override 
    public String parseStationId(String[] args) {
        if (args.length >= 2) {
            return args[1];
        }
        return null;
    }

    @Override 
    public String parseFilePath(String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException("File path is required for ContentServer.");
        }
        return args[1];
    }

    public WeatherData readFromFile(String inputFile) throws Exception {
        String json = Files.readString(Path.of(inputFile));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, WeatherData.class);
    }

    @Override
    public void validateUrl(String url) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("^(http://)?[a-zA-Z0-9.-]+(:[0-9]+)?$");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }

    @Override
    public String parseHost(String url) {
        if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        int colonIndex = url.indexOf(':');
        if (colonIndex != -1) {
            return url.substring(0, colonIndex);
        }
        return url;
    }

    @Override
      public int parsePort(String url) {
        if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        int colonIndex = url.indexOf(':');
        if (colonIndex != -1) {
            return Integer.parseInt(url.substring(colonIndex + 1));
        }
        return 4567; // Default port
    }


}