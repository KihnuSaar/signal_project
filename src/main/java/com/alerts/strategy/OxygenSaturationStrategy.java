package com.alerts.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private static final long TEN_MINUTES_MS = 10 * 60 * 1000L;

    private final AlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    public Optional<Alert> checkAlert(Patient patient) {
        List<PatientRecord> oxygenRecords = patient.getAllRecords().stream()
                .filter(record -> RecordUtils.matches(
                        record.getRecordType(),
                        "BloodSaturation",
                        "Oxygen",
                        "Saturation"
                ))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());

        if (oxygenRecords.isEmpty()) {
            return Optional.empty();
        }

        PatientRecord latest = oxygenRecords.get(oxygenRecords.size() - 1);

        if (latest.getMeasurementValue() < 92) {
            return Optional.of(factory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "oxygen saturation is critically low (" + latest.getMeasurementValue() + "%)",
                    latest.getTimestamp()
            ));
        }

        for (PatientRecord earlier : oxygenRecords) {
            long timeDifference = latest.getTimestamp() - earlier.getTimestamp();
            double drop = earlier.getMeasurementValue() - latest.getMeasurementValue();

            if (timeDifference >= 0 && timeDifference <= TEN_MINUTES_MS && drop >= 5) {
                return Optional.of(factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "oxygen saturation dropped by " + drop + "% within 10 minutes",
                        latest.getTimestamp()
                ));
            }
        }

        return Optional.empty();
    }
}