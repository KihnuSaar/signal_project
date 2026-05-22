package com.cardio_generator.outputs;

/**
 * Defines a common interface for outputting generated patient health data.
 *
 * <p>Different implementations can send the data to different destinations.
 */
public interface OutputStrategy {

    /**
     * Outputs one generated health data measurement.
     *
     * @param patientId the identifier of the patient whose data is being output
     * @param timestamp the time at which the data was generated, usually in
     *     milliseconds since the Unix epoch
     * @param label the type of measurement, such as {@code Saturation}
     * @param data the generated measurement value formatted as text
     */
    void output(int patientId, long timestamp, String label, String data);
}