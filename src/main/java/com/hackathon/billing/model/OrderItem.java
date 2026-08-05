package com.hackathon.billing.model;

import com.hackathon.billing.exception.BillingException;
import com.hackathon.billing.exception.BillingStateException;
import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderItem {
    private final int itemId;
    private final String itemName;
    private final BigDecimal basePrice;
    private final int quantity;
    private final BigDecimal taxRate;
    private final List<Modifier> modifiers;
    private boolean voided;

    public OrderItem(int itemId, String itemName, BigDecimal basePrice, int quantity, BigDecimal taxRate) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.basePrice = CurrencyUtil.scale(basePrice);
        this.quantity = quantity;
        this.taxRate = CurrencyUtil.scale(taxRate);
        this.modifiers = new ArrayList<>();
        this.voided = false;
        validate();
    }

    public void validate() {
        if (itemId <= 0) {
            throw new BillingValidationException("Item id must be positive.");
        }
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new BillingValidationException("Item name cannot be empty.");
        }
        if (basePrice == null) {
            throw new BillingValidationException("Base price cannot be null.");
        }
        if (basePrice.compareTo(CurrencyUtil.zero()) < 0) {
            throw new BillingValidationException("Base price cannot be negative.");
        }
        if (quantity <= 0) {
            throw new BillingValidationException("Quantity must be positive.");
        }
        if (taxRate == null) {
            throw new BillingValidationException("Tax rate cannot be null.");
        }
        if (taxRate.compareTo(CurrencyUtil.zero()) < 0) {
            throw new BillingValidationException("Tax rate cannot be negative.");
        }
        for (Modifier modifier : modifiers) {
            modifier.validate();
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

    public void addModifier(Modifier modifier) {
        if (modifier == null) {
            throw new BillingValidationException("Modifier cannot be null.");
        }
        if (voided) {
            throw new BillingStateException("Cannot add a modifier to a voided item.");
        }
        for (Modifier existingModifier : modifiers) {
            if (existingModifier.getId() == modifier.getId()) {
                throw new BillingValidationException("Modifier already exists on this item.");
            }
        }
        modifiers.add(modifier.copy());
    }

    public void voidItem() {
        this.voided = true;
    }

    public BigDecimal getUnitPriceWithModifiers() {
        BigDecimal total = basePrice;
        for (Modifier modifier : modifiers) {
            total = total.add(modifier.getEffectivePriceDelta());
        }
        return CurrencyUtil.scale(total);
    }

    public BigDecimal getLineSubtotal() {
        return CurrencyUtil.scale(getUnitPriceWithModifiers().multiply(BigDecimal.valueOf(quantity)));
    }

    public BigDecimal getTaxAmount() {
        return CurrencyUtil.scale(getLineSubtotal().multiply(taxRate));
    }

    public BigDecimal getLineTotal() {
        return CurrencyUtil.scale(getLineSubtotal().add(getTaxAmount()));
    }

    public boolean isActive() {
        return !voided;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public List<Modifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    public boolean isVoided() {
        return voided;
    }
}
