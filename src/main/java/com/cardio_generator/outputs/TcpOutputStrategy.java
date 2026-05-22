package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Outputs generated patient data through a TCP socket.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private PrintWriter out;

    /**
     * Creates a TCP server on the given port.
     *
     * @param port the port to listen on
     */
    public TcpOutputStrategy(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("TCP server started on port " + port);

            Executors.newSingleThreadExecutor().submit(() -> acceptClient(serverSocket));
        } catch (IOException exception) {
            System.err.println("Could not start TCP server: " + exception.getMessage());
        }
    }

    /**
     * Waits for one TCP client to connect.
     *
     * @param serverSocket the TCP server socket
     */
    private void acceptClient(ServerSocket serverSocket) {
        try {
            Socket clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("TCP client connected: " + clientSocket.getInetAddress());
        } catch (IOException exception) {
            System.err.println("Could not accept TCP client: " + exception.getMessage());
        }
    }

    /**
     * Sends one patient data record to the connected TCP client.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated
     * @param label     the type of data
     * @param data      the generated data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            out.println(formatOutput(patientId, timestamp, label, data));
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
}
