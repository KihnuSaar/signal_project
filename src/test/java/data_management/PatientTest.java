package data_management;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.data_management.*;

/**
 * Tests for the Patient class.
 */
class PatientTest {

    /**
     * Tests that getRecords returns only records inside the given time range.
     */
    @Test
    void testGetRecordsReturnsRecordsInsideTimeRange() {
        Patient patient = new Patient(1);

        patient.addRecord(80.0, "HeartRate", 1000L);
        patient.addRecord(90.0, "HeartRate", 2000L);
        patient.addRecord(100.0, "HeartRate", 3000L);

        List<PatientRecord> records = patient.getRecords(1500L, 2500L);

        assertEquals(1, records.size());
        assertEquals(90.0, records.get(0).getMeasurementValue());
        assertEquals(2000L, records.get(0).getTimestamp());
    }

    /**
     * Tests that getRecords includes records exactly at the start and end time.
     */
    @Test
    void testGetRecordsIncludesStartAndEndTime() {
        Patient patient = new Patient(1);

        patient.addRecord(80.0, "HeartRate", 1000L);
        patient.addRecord(90.0, "HeartRate", 2000L);
        patient.addRecord(100.0, "HeartRate", 3000L);

        List<PatientRecord> records = patient.getRecords(1000L, 3000L);

        assertEquals(3, records.size());
    }

    /**
     * Tests that getRecords returns an empty list when no records match.
     */
    @Test
    void testGetRecordsReturnsEmptyListWhenNoRecordsMatch() {
        Patient patient = new Patient(1);

        patient.addRecord(80.0, "HeartRate", 1000L);
        patient.addRecord(90.0, "HeartRate", 2000L);

        List<PatientRecord> records = patient.getRecords(3000L, 4000L);

        assertTrue(records.isEmpty());
    }

    /**
     * Tests that getRecords returns an empty list when startTime is after endTime.
     */
    @Test
    void testGetRecordsReturnsEmptyListWhenStartTimeAfterEndTime() {
        Patient patient = new Patient(1);

        patient.addRecord(80.0, "HeartRate", 1000L);

        List<PatientRecord> records = patient.getRecords(3000L, 1000L);

        assertTrue(records.isEmpty());
    }
}