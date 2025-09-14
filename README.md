# Distributed Weather Data System
This project implements a RESTful API for collecting and serving weather data in a distributed system. It consists of three main components: an Aggregation Server that collects and stores weather data, Content Servers that send weather data to the server, and GET Clients that retrieve data from the server. The system uses a Lamport Clock for event synchronization and handles HTTP requests/responses with JSON data.

Purpose of Each File
lib Folder

Contains external libraries required by the project:
JUnit 4.13.2: For unit testing.
Jackson 2.15.2: For JSON serialization/deserialization.
Mockito 4.8.0: For mocking dependencies in tests.


# src Folder

AggregationServer.java: The central server that manages incoming HTTP GET and PUT requests from clients and content servers. It uses a WeatherDataStore to store weather data, a RequestHandler to process requests, and a LamportClock for synchronization.

ContentServer.java: A weather station that periodically sends weather data to the Aggregation Server via PUT requests. It reads data from local JSON files and communicates using HTTP.

GETClient.java: A client that sends HTTP GET requests to the Aggregation Server to retrieve weather data for specific stations or all stations.
WeatherData.java: Represents weather data with fields like id, name, state, lat, lon, airTemp, etc. Provides JSON serialization/deserialization via toJson and fromJson.

WeatherDataStore.java: Manages storage of weather data in a ConcurrentHashMap, handles data persistence to weather_data.json, and removes expired data based on timestamps or a 20-station limit.

RequestHandler.java: Processes HTTP requests (GET/PUT) for the Aggregation Server. Parses requests using HttpReqParser, stores/retrieves data via WeatherDataStore, and sends responses using HttpResBuilder.

HttpReqParser.java: Parses incoming HTTP requests from an InputStream into an HttpRequest object, extracting method, path, headers, and body.

HttpResBuilder.java: Builds and sends HTTP responses with status codes, headers (including Lamport-Clock), and JSON bodies.

LamportClock.java: Implements a Lamport Clock for logical synchronization of events across distributed components, ensuring consistent request ordering.

CLIParser.java: Parses command-line arguments for AggregationServer, ContentServer, and GETClient, configuring ports, hosts, and file paths.

Test Files

AggregationServerTest.java: Tests the Aggregation Server’s ability to start, handle requests, and shut down correctly.
ContentServerTest.java: Validates the Content Server’s functionality, including reading JSON files and sending PUT requests to the Aggregation Server.

GETClientTest.java: Tests the GET Client’s ability to send GET requests and process responses from the Aggregation Server.
WeatherDataTest.java: Ensures WeatherData correctly handles getters/setters, JSON serialization (toJson), deserialization (fromJson), and equality checks.

WeatherDataStoreTest.java: Tests WeatherDataStore’s data storage, retrieval, expiration, persistence, and recovery functionalities.
RequestHandlerTest.java: Verifies RequestHandler’s processing of GET/PUT requests, including status codes (200, 201, 204, 400, 500) and Lamport Clock updates.

HttpReqParserTest.java: Tests HttpReqParser’s ability to parse valid and invalid HTTP requests, including headers and bodies.

HttpResBuilderTest.java: Tests HttpResBuilder’s response formatting, including status codes, headers, and JSON bodies.

LamportClockTest.java: Validates the Lamport Clock’s timestamp updates and synchronization logic.

CLIParserTest.java: Ensures CLIParser correctly parses command-line arguments for all components.

GETClientConcurrentTest.java: Tests concurrent GET requests to the Aggregation Server to ensure thread safety.

# How to Run
## Prerequisites

Java 8 or higher.
Maven for dependency management.
Ensure pom.xml includes:
```
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.8.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```


Compilation
Navigate to the project directory and compile using the Makefile:
```
make compile
```

### Starting the Aggregation Server
On one terminal run the server on port 4567 (default):
```
make run-agg-server
```

Or specify a custom port:
```
make run-agg-server PORT=4569
```

### Starting a Content Server
Open a new terminal and run with a JSON file and server address:
```
make run-content-server
```
Or specify a custom file:
```
make run-content-server FILE_PATH=/path/to/custom.json
```
### Starting a GET Client
In another new terminal run to retrieve data from the server:
```
make run-get-client
```
Or for a specific station
```
run-get-station
```

# Running Tests
Execute unit tests:
```
make test-project
```

# Clean Up
Remove compiled classes and generated JSON files (`weather_data.json`):
```
make clean-project
```

# Stopping the Server
```
make stop-server
```


## Notes:

### Startup Order: 
Start the Aggregation Server before Content Servers or GET Clients.

### Input File: 
Ensure `Weather.json` (or custom file) exists and matches the `WeatherData` format. 

Create it if needed:
```
echo '{"id":"station1","name":"Station One","state":"CA","timeZone":"PST","lat":37.7749,"lon":-122.4194,"localDateTime":"2023-10-01 12:00","localDateTimeFull":"2023-10-01 12:00:00","airTemp":20.5,"apparentT":21.0,"cloud":"Clear","dewpt":15.0,"press":1013.25,"relHum":65,"windDir":"NW","windSpdKmh":10,"windSpdKt":5}' > Weather.json
```

File Permissions: Ensure weather_data.json and transaction.log are writable:
```
chmod 666 weather_data.json transaction.log
```