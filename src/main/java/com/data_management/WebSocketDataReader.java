package com.data_management;

import java.io.IOException;
import java.net.URI;

/**
 * Reads live patient data from a WebSocket server.
 */
public class WebSocketDataReader implements DataReader {

    private final String serverUrl;
    private RealTimeWebSocketClient client;

    /**
     * Creates a WebSocket reader.
     * @param serverUrl WebSocket URL, for example ws://localhost:8080
     */
    public WebSocketDataReader(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Starts reading live data into DataStorage.
     * @param dataStorage storage where incoming data is saved
     * @throws IOException if the connection cannot start
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try {
            client = new RealTimeWebSocketClient(new URI(serverUrl), dataStorage);
            client.connect();

            System.out.println("WebSocket reader started: " + serverUrl);

        } catch (Exception e) {
            throw new IOException("Could not start WebSocket reader", e);
        }
    }

    /**
     * Stops the WebSocket connection.
     */
    @Override
    public void stop() {
        if (client != null) {
            client.close();
        }
    }
}