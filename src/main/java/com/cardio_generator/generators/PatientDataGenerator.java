package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the common behavior for all patient data generators in the simulator.
 *
 * <p>Implementing classes are responsible for generating one type of simulated
 * health data, such as blood saturation, heart rate or blood pressure.
 */
public interface PatientDataGenerator {

    /**
     * Generates one data value for the given patient and sends it to the selected
     * output strategy.
     *
     * @param patientId the identifier of the patient for whom data is generated;
     *     expected to refer to a valid patient in the simulation
     * @param outputStrategy the strategy used to output the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}