package com.data_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDataReader implements DataReader {
    private String directoryPath;

    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        Path directory = Paths.get(directoryPath);

        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IOException("Data directory does not exist: " + directoryPath);
        }

        try (java.util.stream.Stream<Path> paths = Files.list(directory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> readFile(path, dataStorage));
        }
    }

    private void readFile(Path filePath, DataStorage dataStorage) {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                parseLine(line, dataStorage);
            }
        } catch (IOException exception) {
            System.err.println("Could not read file " + filePath + ": " + exception.getMessage());
        }
    }

    private void parseLine(String line, DataStorage dataStorage) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        try {
            ParsedLine parsedLine = parseOutputLine(line);
            storeParsedLine(parsedLine, dataStorage);
        } catch (Exception exception) {
            System.err.println("Skipping invalid data line: " + line);
        }
    }

    private ParsedLine parseOutputLine(String line) {
        String[] parts = line.split(",", 4);

        if (parts.length < 4) {
            throw new IllegalArgumentException("Line does not contain four fields.");
        }

        int patientId;
        long timestamp;
        String label;
        String data;

        if (line.contains("Patient ID:") || line.contains("Timestamp:") || line.contains("Label:")) {
            patientId = Integer.parseInt(valueAfterColon(parts[0]).trim());
            timestamp = Long.parseLong(valueAfterColon(parts[1]).trim());
            label = valueAfterColon(parts[2]).trim();
            data = valueAfterColon(parts[3]).trim();
        } else {
            patientId = Integer.parseInt(parts[0].trim());
            timestamp = Long.parseLong(parts[1].trim());
            label = parts[2].trim();
            data = parts[3].trim();
        }

        return new ParsedLine(patientId, timestamp, label, data);
    }

    private void storeParsedLine(ParsedLine line, DataStorage dataStorage) {
        if (isBloodPressure(line.label) && line.data.contains("/")) {
            String[] pressureValues = line.data.split("/");

            if (pressureValues.length >= 2) {
                double systolic = parseNumericValue(pressureValues[0]);
                double diastolic = parseNumericValue(pressureValues[1]);

                dataStorage.addPatientData(line.patientId, systolic, "BloodPressureSystolic", line.timestamp);
                dataStorage.addPatientData(line.patientId, diastolic, "BloodPressureDiastolic", line.timestamp);
            }

            return;
        }

        double value = parseMeasurementValue(line.data, line.label);
        dataStorage.addPatientData(line.patientId, value, line.label, line.timestamp);
    }

    private boolean isBloodPressure(String label) {
        return label != null && label.toLowerCase().contains("pressure");
    }

    private String valueAfterColon(String text) {
        int colonIndex = text.indexOf(":");

        if (colonIndex == -1) {
            return text;
        }

        return text.substring(colonIndex + 1);
    }

    private double parseMeasurementValue(String rawData, String label) {
        String cleanData = rawData.trim().toLowerCase();

        if (label != null && label.equalsIgnoreCase("Alert")) {
            if (cleanData.contains("triggered") || cleanData.equals("1") || cleanData.equals("true")) {
                return 1.0;
            }

            return 0.0;
        }

        return parseNumericValue(cleanData);
    }

    private double parseNumericValue(String rawData) {
        String cleanData = rawData.replaceAll("[^0-9.\\-]", "");

        if (cleanData.isEmpty()) {
            throw new IllegalArgumentException("No numeric data found.");
        }

        return Double.parseDouble(cleanData);
    }

    private static class ParsedLine {
        private int patientId;
        private long timestamp;
        private String label;
        private String data;

        private ParsedLine(int patientId, long timestamp, String label, String data) {
            this.patientId = patientId;
            this.timestamp = timestamp;
            this.label = label;
            this.data = data;
        }
    }
}
