package com.alerts.strategy;

import java.util.Optional;
import com.alerts.Alert;
import com.data_management.Patient;

public interface AlertStrategy {
    Optional<Alert> checkAlert(Patient patient);
}