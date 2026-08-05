package com.hackathon.billing.model;

import com.hackathon.billing.exception.BillingException;
import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;

public class Modifier {
    private final int id;
    private final String name;
    private final ModifierType type;
    private final BigDecimal priceDelta;
    private boolean active;

    public Modifier(int id, String name, ModifierType type, BigDecimal priceDelta) {
        this(id, name, type, priceDelta, true);
    }

    public Modifier(int id, String name, ModifierType type, BigDecimal priceDelta, boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.priceDelta = CurrencyUtil.scale(priceDelta);
        this.active = active;
        validate();
    }

    public Modifier copy() {
        return new Modifier(id, name, type, priceDelta, active);
    }

    public void validate() {
        if (id <= 0) {
            throw new BillingValidationException("Modifier id must be positive.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BillingValidationException("Modifier name cannot be empty.");
        }
        if (type == null) {
            throw new BillingValidationException("Modifier type cannot be null.");
        }
        if (priceDelta == null) {
            throw new BillingValidationException("Modifier price delta cannot be null.");
        }
    }

    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (BillingException exception) {
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ModifierType getType() {
        return type;
    }

    public BigDecimal getPriceDelta() {
        return priceDelta;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public BigDecimal getEffectivePriceDelta() {
        return active ? priceDelta : CurrencyUtil.zero();
    }

    public String getDisplayName() {
        return name;
    }
}
