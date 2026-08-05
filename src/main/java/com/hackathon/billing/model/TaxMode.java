package com.hackathon.billing.model;

public enum TaxMode {
    BEFORE_SERVICE_CHARGE,
    AFTER_SERVICE_CHARGE;

    public String getDisplayName() {
        return name().replace('_', ' ');
    }
}
