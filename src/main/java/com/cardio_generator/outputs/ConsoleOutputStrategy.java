package com.cardio_generator.outputs;

/**
 * Outputs generated patient data to the console.
 */
public class ConsoleOutputStrategy implements OutputStrategy {

    /**
     * Prints one patient data record in comma-separated format.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated
     * @param label     the type of data
     * @param data      the generated data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        System.out.println(formatOutput(patientId, timestamp, label, data));
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