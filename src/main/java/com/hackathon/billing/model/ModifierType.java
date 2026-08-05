package com.hackathon.billing.model;

public enum ModifierType {
    ADD_ON,
    CUSTOMIZATION,
    DISCOUNT;

    public String getDisplayName() {
        return name().replace('_', ' ');
    }
}
