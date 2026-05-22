package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AlertGenerator {
    private static final long TEN_MINUTES_IN_MILLISECONDS = 10L * 60L * 1000L;
    private static final int ECG_WINDOW_SIZE = 5;
    private static final double ECG_PEAK_MULTIPLIER = 1.5;

    private DataStorage dataStorage;
    private List<Alert> alerts;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
    }

    public void evaluateData(Patient patient) {
        if (patient == null) {
            return;
        }

        List<PatientRecord> records = patient.getAllRecords();
        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        checkBloodPressureAlerts(patient, records);
        checkSaturationAlerts(patient, records);
        checkHypotensiveHypoxemiaAlert(patient, records);
        checkEcgAlerts(patient, records);
        checkManualTriggeredAlerts(patient, records);
    }

    public void evaluateAllData() {
        if (dataStorage == null) {
            return;
        }

        for (Patient patient : dataStorage.getAllPatients()) {
            evaluateData(patient);
        }
    }

    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }

    public void clearAlerts() {
        alerts.clear();
    }

    private void checkBloodPressureAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = getRecordsByType(records, "systolic");
        List<PatientRecord> diastolicRecords = getRecordsByType(records, "diastolic");

        checkCriticalBloodPressure(patient, systolicRecords, true);
        checkCriticalBloodPressure(patient, diastolicRecords, false);

        checkBloodPressureTrend(patient, systolicRecords, "systolic");
        checkBloodPressureTrend(patient, diastolicRecords, "diastolic");
    }

    private void checkCriticalBloodPressure(Patient patient, List<PatientRecord> records, boolean systolic) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();

            if (systolic && (value > 180 || value < 90)) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Critical systolic blood pressure",
                        record.getTimestamp()));
            } else if (!systolic && (value > 120 || value < 60)) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Critical diastolic blood pressure",
                        record.getTimestamp()));
            }
        }
    }

    private void checkBloodPressureTrend(Patient patient, List<PatientRecord> records, String pressureType) {
        for (int i = 0; i <= records.size() - 3; i++) {
            PatientRecord first = records.get(i);
            PatientRecord second = records.get(i + 1);
            PatientRecord third = records.get(i + 2);

            double changeOne = second.getMeasurementValue() - first.getMeasurementValue();
            double changeTwo = third.getMeasurementValue() - second.getMeasurementValue();

            if (changeOne > 10 && changeTwo > 10) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Increasing " + pressureType + " blood pressure trend",
                        third.getTimestamp()));
            }

            if (changeOne < -10 && changeTwo < -10) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Decreasing " + pressureType + " blood pressure trend",
                        third.getTimestamp()));
            }
        }
    }

    private void checkSaturationAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = getRecordsByType(records, "saturation");

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Low blood oxygen saturation",
                        record.getTimestamp()));
            }
        }

        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord older = saturationRecords.get(i);

            for (int j = i + 1; j < saturationRecords.size(); j++) {
                PatientRecord newer = saturationRecords.get(j);
                long timeDifference = newer.getTimestamp() - older.getTimestamp();

                if (timeDifference > TEN_MINUTES_IN_MILLISECONDS) {
                    break;
                }

                if (older.getMeasurementValue() - newer.getMeasurementValue() >= 5) {
                    triggerAlert(new Alert(
                            String.valueOf(patient.getPatientId()),
                            "Rapid blood oxygen saturation drop",
                            newer.getTimestamp()));
                    break;
                }
            }
        }
    }

    private void checkHypotensiveHypoxemiaAlert(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = getRecordsByType(records, "systolic");
        List<PatientRecord> saturationRecords = getRecordsByType(records, "saturation");

        for (PatientRecord systolic : systolicRecords) {
            if (systolic.getMeasurementValue() >= 90) {
                continue;
            }

            for (PatientRecord saturation : saturationRecords) {
                boolean closeInTime = Math.abs(saturation.getTimestamp() - systolic.getTimestamp())
                        <= TEN_MINUTES_IN_MILLISECONDS;

                if (saturation.getMeasurementValue() < 92 && closeInTime) {
                    triggerAlert(new Alert(
                            String.valueOf(patient.getPatientId()),
                            "Hypotensive Hypoxemia Alert",
                            Math.max(systolic.getTimestamp(), saturation.getTimestamp())));
                    return;
                }
            }
        }
    }

    private void checkEcgAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = getRecordsByType(records, "ecg");

        for (int i = ECG_WINDOW_SIZE; i < ecgRecords.size(); i++) {
            double sum = 0.0;

            for (int j = i - ECG_WINDOW_SIZE; j < i; j++) {
                sum += ecgRecords.get(j).getMeasurementValue();
            }

            double average = sum / ECG_WINDOW_SIZE;
            PatientRecord current = ecgRecords.get(i);

            if (average > 0 && current.getMeasurementValue() > average * ECG_PEAK_MULTIPLIER) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Abnormal ECG peak",
                        current.getTimestamp()));
            }
        }
    }

    private void checkManualTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> alertRecords = getRecordsByType(records, "alert");

        for (PatientRecord record : alertRecords) {
            if (record.getMeasurementValue() == 1.0) {
                triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Manual alert triggered",
                        record.getTimestamp()));
            }
        }
    }

    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String keyword) {
        List<PatientRecord> matchingRecords = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);

        for (PatientRecord record : records) {
            String recordType = record.getRecordType();

            if (recordType != null && recordType.toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                matchingRecords.add(record);
            }
        }

        matchingRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
        return matchingRecords;
    }

    private void triggerAlert(Alert alert) {
        alerts.add(alert);

        System.out.println("ALERT: Patient " + alert.getPatientId()
                + " - " + alert.getCondition()
                + " at " + alert.getTimestamp());
    }
}