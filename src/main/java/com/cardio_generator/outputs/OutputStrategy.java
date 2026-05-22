package com.cardio_generator.outputs;

/**
 * Defines how generated patient data should be output.
 */
public interface OutputStrategy {

    /**
     * Outputs one generated patient data record.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated
     * @param label     the type of data, such as ECG or BloodPressure
     * @param data      the generated data value
     */
    void output(int patientId, long timestamp, String label, String data);
}