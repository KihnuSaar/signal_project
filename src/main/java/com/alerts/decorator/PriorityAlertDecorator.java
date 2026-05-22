package com.alerts.decorator;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator {
    private final String priority;

    public PriorityAlertDecorator(Alert wrappedAlert, String priority) {
        super(wrappedAlert);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return "[PRIORITY: " + priority + "] " + wrappedAlert.getCondition();
    }

    public String getPriority() {
        return priority;
    }
}