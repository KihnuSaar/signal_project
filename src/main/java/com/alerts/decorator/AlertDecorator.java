package com.alerts.decorator;

import com.alerts.Alert;

public abstract class AlertDecorator extends Alert {
    protected final Alert wrappedAlert;

    protected AlertDecorator(Alert wrappedAlert) {
        super(
            wrappedAlert.getPatientId(),
            wrappedAlert.getCondition(),
            wrappedAlert.getTimestamp()
        );

        this.wrappedAlert = wrappedAlert;
    }
}