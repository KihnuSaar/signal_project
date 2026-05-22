package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Outputs generated patient data to text files.
 *
 * <p>Each measurement label is written to a separate file inside the configured
 * base directory.
 */
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory;

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a file output strategy that writes data to the given directory.
     *
     * @param baseDirectory the directory where output files should be created.
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes one generated health data measurement to a label-specific text file.
     *
     * <p>The method creates the base directory if necessary, chooses a file based
     * on the measurement label, and appends the formatted data line to that file.
     *
     * @param patientId the identifier of the patient whose data is being written
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the measurement type; also used as the output file name
     * @param data the generated measurement value formatted as text
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        String filePath =
                fileMap.computeIfAbsent(
                        label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        try (PrintWriter out =
                new PrintWriter(
                        Files.newBufferedWriter(
                                Paths.get(filePath),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND))) {

            out.printf(
                    "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}