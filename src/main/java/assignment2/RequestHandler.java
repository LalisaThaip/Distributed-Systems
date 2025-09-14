package assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class RequestHandler implements IRequestHandler {
    private final ILamportClock lamportClock;
    private final IWeatherDataStore dataStore;
    private final IHttpReqParser reqParser = new HttpReqParser();
    private final IHttpResBuilder resBuilder = new HttpResBuilder();
    private static final ObjectMapper mapper = new ObjectMapper();

    public RequestHandler(ILamportClock lamportClock, IWeatherDataStore dataStore) {
        this.lamportClock = lamportClock;
        this.dataStore = dataStore;
    }

    @Override
    public void handleRequest(Socket clientSocket) throws IOException {
        HttpRequest request = reqParser.parseRequest(clientSocket.getInputStream());
        long receivedClock = request.getHeaders().containsKey("Lamport-Clock") ?
        Long.parseLong(request.getHeaders().get("Lamport-Clock")) : 0;
        lamportClock.update(receivedClock);

        String method = request.getMethod();
        int status;
        String body = null;

        if ("GET".equals(method)) {
            String stationId = null;
            if (request.getPath().contains("?")) {
                String query = request.getPath().substring(request.getPath().indexOf("?") + 1);
                if (query.startsWith("id=")) {
                    stationId = query.substring(3);
                }
            }
            body = dataStore.getData(stationId);
            status = 200;
        } else if ("PUT".equals(method)) {
            if (request.getBody().isEmpty()) {
                status = 204;
            } else {
                try {
                    WeatherData data = WeatherData.fromJson(request.getBody());
                    String key = data.getId(); // Use id as key
                    boolean isNew = !dataStore.containsKey(key);
                    dataStore.putData(key, data);
                    status = isNew ? 201 : 200;
                } catch (Exception e) {
                    status = 500;
                }
            }
        } else {
            status = 400;
        }

        lamportClock.increment();
        resBuilder.sendResponse(clientSocket, status, body, lamportClock.getClock());
    }
}