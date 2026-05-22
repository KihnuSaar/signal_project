package com.data_management;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alerts.AlertGenerator;

/**
 * Stores and retrieves patient records.
 */
public class DataStorage {

    private Map<Integer, Patient> patientMap;

    private static DataStorage instance;

/**
 * Returns the single shared DataStorage instance.
 * @return the shared DataStorage object
 */
public static DataStorage getInstance() {
    if (instance == null) {
        instance = new DataStorage();
    }
    return instance;
}

    /**
     * Creates an empty storage for patient data.
     */
    private DataStorage() {
        this.patientMap = new ConcurrentHashMap<>();
    }

    /**
     * Adds a new record to a patient, or creates the patient if needed.
     * @param patientId patient ID
     * @param measurementValue measured value
     * @param recordType type of measurement
     * @param timestamp time of the measurement
     */
    public synchronized void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.get(patientId);

        if (patient == null) {
            patient = new Patient(patientId);
            patientMap.put(patientId, patient);
        }

        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Gets records for one patient in a time range.
     * @param patientId patient ID
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return matching records, or an empty list
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);

        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }

        return new ArrayList<>();
    }

    /**
     * Gets all stored patients.
     * @return list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Removes all stored patient data.
     */
    public void clear() {
        patientMap.clear();
    }

    /**
     * Simple manual test for DataStorage.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 85.0, "HeartRate", 1716380000000L);

        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);

        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId()
                    + ", Type: " + record.getRecordType()
                    + ", Data: " + record.getMeasurementValue()
                    + ", Timestamp: " + record.getTimestamp());
        }

        AlertGenerator alertGenerator = new AlertGenerator(storage);

        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
}