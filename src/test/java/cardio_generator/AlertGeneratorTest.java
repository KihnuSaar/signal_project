package cardio_generator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Tests for the AlertGenerator class.
 */
class AlertGeneratorTest {

    /**
     * Tests that high systolic blood pressure triggers an alert.
     */
    @Test
    void testHighSystolicPressureTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 181.0, "BloodPressureSystolic", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Critical systolic");
    }

    /**
     * Tests that low systolic blood pressure triggers an alert.
     */
    @Test
    void testLowSystolicPressureTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 89.0, "BloodPressureSystolic", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Critical systolic");
    }

    /**
     * Tests that high diastolic blood pressure triggers an alert.
     */
    @Test
    void testHighDiastolicPressureTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 121.0, "BloodPressureDiastolic", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Critical diastolic");
    }

    /**
     * Tests that low diastolic blood pressure triggers an alert.
     */
    @Test
    void testLowDiastolicPressureTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 59.0, "BloodPressureDiastolic", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Critical diastolic");
    }

    /**
     * Tests that three increasing systolic readings trigger a trend alert.
     */
    @Test
    void testIncreasingBloodPressureTrendTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 110.0, "BloodPressureSystolic", 1000L);
        storage.addPatientData(1, 125.0, "BloodPressureSystolic", 2000L);
        storage.addPatientData(1, 140.0, "BloodPressureSystolic", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Increasing systolic");
    }

    /**
     * Tests that three decreasing systolic readings trigger a trend alert.
     */
    @Test
    void testDecreasingBloodPressureTrendTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 150.0, "BloodPressureSystolic", 1000L);
        storage.addPatientData(1, 135.0, "BloodPressureSystolic", 2000L);
        storage.addPatientData(1, 120.0, "BloodPressureSystolic", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Decreasing systolic");
    }

    /**
     * Tests that oxygen saturation below 92 triggers an alert.
     */
    @Test
    void testLowOxygenSaturationTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 91.0, "BloodSaturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Low blood oxygen saturation");
    }

    /**
     * Tests that oxygen saturation dropping by at least 5 within 10 minutes triggers an alert.
     */
    @Test
    void testRapidOxygenSaturationDropTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 98.0, "BloodSaturation", 1000L);
        storage.addPatientData(1, 93.0, "BloodSaturation", 1000L + 5L * 60L * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Rapid blood oxygen saturation drop");
    }

    /**
     * Tests that low systolic pressure and low oxygen saturation together trigger a combined alert.
     */
    @Test
    void testHypotensiveHypoxemiaAlertTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 85.0, "BloodPressureSystolic", 1000L);
        storage.addPatientData(1, 91.0, "BloodSaturation", 2000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Hypotensive Hypoxemia Alert");
    }

    /**
     * Tests that an ECG value far above the moving average triggers an alert.
     */
    @Test
    void testAbnormalEcgPeakTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();

        storage.addPatientData(1, 10.0, "ECG", 1000L);
        storage.addPatientData(1, 10.0, "ECG", 2000L);
        storage.addPatientData(1, 10.0, "ECG", 3000L);
        storage.addPatientData(1, 10.0, "ECG", 4000L);
        storage.addPatientData(1, 10.0, "ECG", 5000L);
        storage.addPatientData(1, 30.0, "ECG", 6000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Abnormal ECG peak");
    }

    /**
     * Tests that a manual alert record triggers an alert.
     */
    @Test
    void testManualAlertTriggersAlert() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 1.0, "Alert", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertAlertContains(generator.getAlerts(), "Manual alert triggered");
    }

    /**
     * Tests that normal data does not trigger any alerts.
     */
    @Test
    void testNormalDataCreatesNoAlerts() {
        DataStorage storage = DataStorage.getInstance();

        storage.addPatientData(1, 120.0, "BloodPressureSystolic", 1000L);
        storage.addPatientData(1, 80.0, "BloodPressureDiastolic", 1000L);
        storage.addPatientData(1, 97.0, "BloodSaturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        generator.evaluateData(patient);

        assertTrue(generator.getAlerts().isEmpty());
    }

    /**
     * Checks whether at least one alert contains the expected text.
     *
     * @param alerts       the generated alerts
     * @param expectedText the expected alert condition text
     */
    private void assertAlertContains(List<Alert> alerts, String expectedText) {
        boolean found = false;

        for (Alert alert : alerts) {
            if (alert.getCondition().contains(expectedText)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected alert containing: " + expectedText);
    }
}