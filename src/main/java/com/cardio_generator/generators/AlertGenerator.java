package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated patient alert events.
 *
 * <p>Each patient has an alert state that is either resolved or pressed. If an
 * alert is already active, the generator has a high chance of resolving it. If
 * no alert is active, the generator calculates a probability of triggering a new
 * alert.
 */
public class AlertGenerator implements PatientDataGenerator {

    //changed some variables to lowerCamelCase.

    public static final Random randomGenerator = new Random();
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates an alert generator for a fixed number of patients.
     *
     * <p>The alert state array uses patient IDs starting from 1, so its size is
     * one larger than the patient count.
     *
     * @param patientCount the number of patients in the simulation; should be a
     *     positive integer
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates one possible alert update for the given patient.
     *
     * <p>If the patient currently has an active alert, there is a 90% chance that
     * the alert becomes resolved. If the patient has no active alert, a new alert
     * may be triggered based on a probability calculated from a fixed average
     * event rate.
     *
     * @param patientId the identifier of the patient whose alert state is being
     *     updated; expected to be between 1 and the patient count
     * @param outputStrategy the strategy used to output triggered or resolved
     *     alert events
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) {
                    alertStates[patientId] = false;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1;
                double p = -Math.expm1(-lambda);
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}