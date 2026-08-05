package com.hackathon.billing.model;

import com.hackathon.billing.exception.BillingException;
import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Order {
    private final String orderId;
    private final List<OrderItem> items;
    private BigDecimal serviceChargeRate;
    private TaxMode taxMode;

    public Order(String orderId, BigDecimal serviceChargeRate, TaxMode taxMode) {
        this(orderId, new ArrayList<>(), serviceChargeRate, taxMode);
    }

    public Order(String orderId, List<OrderItem> items, BigDecimal serviceChargeRate, TaxMode taxMode) {
        this.orderId = orderId;
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
        this.serviceChargeRate = CurrencyUtil.scale(serviceChargeRate);
        this.taxMode = taxMode;
        validateState();
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new BillingValidationException("Order item cannot be null.");
        }
        if (findItemById(item.getItemId()).isPresent()) {
            throw new BillingValidationException("An item with the same id already exists in the order.");
        }
        item.validate();
        items.add(item);
    }

    public List<OrderItem> getActiveItems() {
        List<OrderItem> activeItems = new ArrayList<>();
        for (OrderItem item : items) {
            if (item.isActive()) {
                activeItems.add(item);
            }
        }
        return Collections.unmodifiableList(activeItems);
    }

    public Optional<OrderItem> findItemById(int itemId) {
        for (OrderItem item : items) {
            if (item.getItemId() == itemId) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public void updateServiceChargeRate(BigDecimal serviceChargeRate) {
        this.serviceChargeRate = CurrencyUtil.scale(serviceChargeRate);
        validateState();
    }

    public void updateTaxMode(TaxMode taxMode) {
        this.taxMode = taxMode;
        validateState();
    }

    public void validateState() {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new BillingValidationException("Order id cannot be empty.");
        }
        if (serviceChargeRate == null) {
            throw new BillingValidationException("Service charge rate cannot be null.");
        }
        if (serviceChargeRate.compareTo(CurrencyUtil.zero()) < 0) {
            throw new BillingValidationException("Service charge rate cannot be negative.");
        }
        if (taxMode == null) {
            throw new BillingValidationException("Tax mode cannot be null.");
        }
        for (OrderItem item : items) {
            item.validate();
        }
    }

    public boolean isValid() {
        try {
            validateState();
            return true;
        } catch (BillingException exception) {
            return false;
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getServiceChargeRate() {
        return serviceChargeRate;
    }

    public TaxMode getTaxMode() {
        return taxMode;
    }
}
