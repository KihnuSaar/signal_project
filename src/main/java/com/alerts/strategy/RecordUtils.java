package com.alerts.strategy;

import java.util.Comparator;
import java.util.Optional;
import com.data_management.Patient;
import com.data_management.PatientRecord;

final class RecordUtils {
    private RecordUtils() {}

    static Optional<PatientRecord> latestMatching(Patient patient, String... typeParts) {
        return patient.getAllRecords().stream()
                .filter(record -> matches(record.getRecordType(), typeParts))
                .max(Comparator.comparingLong(PatientRecord::getTimestamp));
    }

    static boolean matches(String recordType, String... typeParts) {
        String lower = recordType.toLowerCase();
        for (String part : typeParts) {
            if (lower.contains(part.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
