package com.data_management;

import java.net.URI;

import org.java_websocket.handshake.ServerHandshake;

/**
 * Receives WebSocket messages and stores them as patient data.
 */
public class RealTimeWebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final DataStorage dataStorage;

    /**
     * Creates the WebSocket client.
     * @param serverUri WebSocket server URI
     * @param dataStorage storage where parsed data is saved
     */
    public RealTimeWebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    /**
     * Runs when the connection opens.
     * @param handshake server handshake info
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server.");
    }

    /**
     * Parses and stores each incoming message.
     * @param message incoming message
     */
    @Override
    public void onMessage(String message) {
        try {
            parseAndStore(message);
        } catch (Exception e) {
            System.out.println("Invalid message ignored: " + message);
        }
    }

    /**
     * Runs when the connection closes.
     * @param code close code
     * @param reason close reason
     * @param remote true if server closed it
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket closed: " + reason);
    }

    /**
     * Runs when a WebSocket error happens.
     * @param ex error that occurred
     */
    @Override
    public void onError(Exception ex) {
        System.out.println("WebSocket error: " + ex.getMessage());
    }

    /**
     * Converts one message into patient data.
     * @param message format: patientId,timestamp,recordType,measurementValue
     */
    public void parseAndStore(String message) {
        String[] parts = message.split(",");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Message must have 4 parts");
        }

        int patientId = Integer.parseInt(parts[0].trim());
        long timestamp = Long.parseLong(parts[1].trim());
        String recordType = parts[2].trim();
        double measurementValue = Double.parseDouble(parts[3].trim());

        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
    }
}