package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Outputs generated patient data to a TCP client.
 *
 * <p>This class starts a TCP server on the given port.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Creates a TCP output strategy and starts a server socket on the given port.
     *
     * <p>The client connection is accepted on a separate thread so that the main
     * simulator can continue running while waiting for a client.
     *
     * @param port the TCP port on which the server should listen
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            Executors.newSingleThreadExecutor()
                    .submit(
                            () -> {
                                try {
                                    clientSocket = serverSocket.accept();
                                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                                    System.out.println(
                                            "Client connected: "
                                                    + clientSocket.getInetAddress());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends one generated health data measurement to the connected TCP client.
     *
     * <p>If no client is connected yet, this method does not send anything. When
     * a client is connected, the data is sent in the format
     * {@code patientId,timestamp,label,data}.
     *
     * @param patientId the identifier of the patient whose data is being sent
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the measurement type, such as {@code Saturation}
     * @param data the generated measurement value formatted as text
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
