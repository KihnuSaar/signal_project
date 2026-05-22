package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation data for patients.
 *
 * <p>Each patient starts with a baseline saturation value between 95% and 100%.
 * On each generation step, the value changes slightly by -1, 0, or 1 to simulate
 * realistic small fluctuations. The value is limited to the range 90% to 100%.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {

    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Creates a blood saturation data generator for a fixed number of patients.
     *
     * <p>The array uses patient IDs starting from 1, so its size is one larger
     * than the patient count.
     *
     * @param patientCount the number of patients in the simulation; should be a
     *     positive integer
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6);
        }
    }

    /**
     * Generates one simulated blood saturation value for a patient and outputs it.
     *
     * <p>The generated value is based on the patient's previous value. It changes
     * by at most one percentage point and is kept between 90% and 100%.
     *
     * @param patientId the identifier of the patient whose saturation value is
     *     generated; expected to be between 1 and the patient count
     * @param outputStrategy the strategy used to output the generated saturation
     *     value
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            int variation = random.nextInt(3) - 1;
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;

            outputStrategy.output(
                    patientId,
                    System.currentTimeMillis(),
                    "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println(
                    "An error occurred while generating blood saturation data for patient "
                            + patientId);
            e.printStackTrace();
        }
    }
}