package com.alerts.strategy;

import java.util.Optional;
import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private final AlertFactory factory = new ECGAlertFactory();

    @Override
    public Optional<Alert> checkAlert(Patient patient) {
        Optional<PatientRecord> latest = RecordUtils.latestMatching(
                patient,
                "HeartRate",
                "Heart Rate",
                "ECG"
        );

        if (latest.isEmpty()) {
            return Optional.empty();
        }

        PatientRecord record = latest.get();
        double value = record.getMeasurementValue();

        if (value > 130) {
            return Optional.of(factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "heart rate is too high (" + value + " bpm)",
                    record.getTimestamp()
            ));
        }

        if (value < 40) {
            return Optional.of(factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "heart rate is too low (" + value + " bpm)",
                    record.getTimestamp()
            ));
        }

        return Optional.empty();
    }
}
