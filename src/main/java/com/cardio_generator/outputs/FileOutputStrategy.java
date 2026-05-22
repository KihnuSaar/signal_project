package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Outputs generated patient data to text files.
 */
public class FileOutputStrategy implements OutputStrategy {

    private final String baseDirectory;

    /**
     * Creates a file output strategy.
     *
     * @param baseDirectory the directory where output files should be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes one patient data record to a file named after the label.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated
     * @param label     the type of data
     * @param data      the generated data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            Files.createDirectories(Paths.get(baseDirectory));

            Path filePath = Paths.get(baseDirectory, sanitizeFileName(label) + ".txt");

            try (PrintWriter out = new PrintWriter(
                    Files.newBufferedWriter(
                            filePath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND))) {

                out.println(formatOutput(patientId, timestamp, label, data));
            }
        } catch (IOException exception) {
            System.err.println("Error writing output file: " + exception.getMessage());
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
     * Makes a label safe to use as a file name.
     *
     * @param label the original label
     * @return a safe file name
     */
    private String sanitizeFileName(String label) {
        if (label == null || label.isBlank()) {
            return "unknown";
        }

        return label.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}