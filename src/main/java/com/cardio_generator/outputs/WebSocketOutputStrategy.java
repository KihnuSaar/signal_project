package com.cardio_generator.outputs;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

/**
 * Outputs generated patient data through a WebSocket server.
 */
public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    /**
     * Creates a WebSocket server on the given port.
     *
     * @param port the port to listen on
     */
    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        server.start();

        System.out.println("WebSocket server started on port " + port);
    }

    /**
     * Sends one patient data record to all connected WebSocket clients.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated
     * @param label     the type of data
     * @param data      the generated data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = formatOutput(patientId, timestamp, label, data);

        for (WebSocket connection : server.getConnections()) {
            connection.send(message);
        }
    }

    /**
     * Formats a patient data record.
     *
     * @param patientId the ID of the patient
     * @param timestamp the timestamp of the record
     * @param label     the data label
     * @param data      the data value
     * @return formatted comma-separated output
     */
    private String formatOutput(int patientId, long timestamp, String label, String data) {
        return patientId + "," + timestamp + "," + label + "," + data;
    }

    /**
     * Simple WebSocket server used to broadcast generated data.
     */
    private static class SimpleWebSocketServer extends WebSocketServer {

        /**
         * Creates the WebSocket server.
         *
         * @param address the address and port to bind to
         */
        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        /**
         * Runs when a client connects.
         */
        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("WebSocket client connected: " + conn.getRemoteSocketAddress());
        }

        /**
         * Runs when a client disconnects.
         */
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("WebSocket client disconnected: " + conn.getRemoteSocketAddress());
        }

        /**
         * Handles incoming messages from clients.
         */
        @Override
        public void onMessage(WebSocket conn, String message) {
            // This simulator only sends data, so incoming messages are ignored.
        }

        /**
         * Handles WebSocket errors.
         */
        @Override
        public void onError(WebSocket conn, Exception exception) {
            System.err.println("WebSocket error: " + exception.getMessage());
        }

        /**
         * Runs when the WebSocket server starts.
         */
        @Override
        public void onStart() {
            System.out.println("WebSocket server is ready.");
        }
    }
}