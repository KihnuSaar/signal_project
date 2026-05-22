package com.alerts.strategy;

import java.util.Optional;
import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    private final AlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public Optional<Alert> checkAlert(Patient patient) {
        Optional<PatientRecord> latest = RecordUtils.latestMatching(
                patient,
                "BloodPressure",
                "Systolic",
                "Blood Pressure"
        );

        if (latest.isEmpty()) {
            return Optional.empty();
        }

        PatientRecord record = latest.get();
        double value = record.getMeasurementValue();

        if (value < 90) {
            return Optional.of(factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "systolic blood pressure is critically low (" + value + ")",
                    record.getTimestamp()
            ));
        }

        if (value > 180) {
            return Optional.of(factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "systolic blood pressure is critically high (" + value + ")",
                    record.getTimestamp()
            ));
        }

        return Optional.empty();
    }
}