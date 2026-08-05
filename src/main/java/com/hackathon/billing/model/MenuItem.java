package com.hackathon.billing.model;

import com.hackathon.billing.exception.BillingException;
import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;

public class MenuItem {
    private final int id;
    private final String name;
    private final BigDecimal basePrice;
    private final BigDecimal taxRate;

    public MenuItem(int id, String name, BigDecimal basePrice, BigDecimal taxRate) {
        this.id = id;
        this.name = name;
        this.basePrice = CurrencyUtil.scale(basePrice);
        this.taxRate = CurrencyUtil.scale(taxRate);
        validate();
    }

    public void validate() {
        if (id <= 0) {
            throw new BillingValidationException("Menu item id must be positive.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BillingValidationException("Menu item name cannot be empty.");
        }
        if (basePrice == null) {
            throw new BillingValidationException("Menu item base price cannot be null.");
        }
        if (basePrice.compareTo(CurrencyUtil.zero()) < 0) {
            throw new BillingValidationException("Menu item base price cannot be negative.");
        }
        if (taxRate == null) {
            throw new BillingValidationException("Menu item tax rate cannot be null.");
        }
        if (taxRate.compareTo(CurrencyUtil.zero()) < 0) {
            throw new BillingValidationException("Menu item tax rate cannot be negative.");
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

    public OrderItem toOrderItem(int quantity) {
        return new OrderItem(id, name, basePrice, quantity, taxRate);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }
}
