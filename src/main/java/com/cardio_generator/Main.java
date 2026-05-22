package com.cardio_generator;

import java.io.IOException;

import com.data_management.DataStorage;

/**
 * Main entry point for choosing which part of the program to run.
 */
public class Main {

    /**
     * Runs either DataStorage or HealthDataSimulator depending on the command-line argument.
     *
     * @param args command-line arguments
     * @throws IOException if HealthDataSimulator has an input/output error
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("DataStorage")) {
            DataStorage.main(new String[] {});
        } else {
            HealthDataSimulator.main(args);
        }
    }
}