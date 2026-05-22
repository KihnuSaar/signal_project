package data_management;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.data_management.*;

/**
 * Tests for the FileDataReader class.
 */
class FileDataReaderTest {

    @TempDir
    Path tempDirectory;

    /**
     * Tests that FileDataReader reads the text output format from FileOutputStrategy.
     */
    @Test
    void testReadFileOutputStrategyFormat() throws IOException {
        Path file = tempDirectory.resolve("HeartRate.txt");

        Files.writeString(file,
                "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 85\n"
        );

        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(tempDirectory.toString());

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);

        assertEquals(1, records.size());
        assertEquals(85.0, records.get(0).getMeasurementValue());
        assertEquals("HeartRate", records.get(0).getRecordType());
        assertEquals(1000L, records.get(0).getTimestamp());
    }

    /**
     * Tests that FileDataReader reads the comma-separated TCP/WebSocket format.
     */
    @Test
    void testReadCommaSeparatedFormat() throws IOException {
        Path file = tempDirectory.resolve("data.txt");

        Files.writeString(file,
                "1,1000,BloodSaturation,95\n"
        );

        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(tempDirectory.toString());

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);

        assertEquals(1, records.size());
        assertEquals(95.0, records.get(0).getMeasurementValue());
        assertEquals("BloodSaturation", records.get(0).getRecordType());
    }

    /**
     * Tests that FileDataReader splits blood pressure data into systolic and diastolic records.
     */
    @Test
    void testReadBloodPressureData() throws IOException {
        Path file = tempDirectory.resolve("BloodPressure.txt");

        Files.writeString(file,
                "Patient ID: 1, Timestamp: 1000, Label: BloodPressure, Data: 120/80 mmHg\n"
        );

        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(tempDirectory.toString());

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);

        assertEquals(2, records.size());

        assertEquals(120.0, records.get(0).getMeasurementValue());
        assertEquals("BloodPressureSystolic", records.get(0).getRecordType());

        assertEquals(80.0, records.get(1).getMeasurementValue());
        assertEquals("BloodPressureDiastolic", records.get(1).getRecordType());
    }

    /**
     * Tests that invalid lines are skipped without crashing.
     */
    @Test
    void testInvalidLinesDoNotCrashReader() throws IOException {
        Path file = tempDirectory.resolve("invalid.txt");

        Files.writeString(file,
                "this is not valid data\n"
                        + "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 85\n"
        );

        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(tempDirectory.toString());

        assertDoesNotThrow(() -> reader.readData(storage));

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);

        assertEquals(1, records.size());
        assertEquals(85.0, records.get(0).getMeasurementValue());
    }
}