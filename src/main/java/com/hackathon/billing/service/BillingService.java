package com.hackathon.billing.service;

import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.model.Order;
import com.hackathon.billing.model.OrderItem;
import com.hackathon.billing.model.TaxMode;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;

public class BillingService {
    public BigDecimal calculateSubtotal(Order order) {
        requireOrder(order);
        BigDecimal subtotal = CurrencyUtil.zero();
        for (OrderItem item : order.getActiveItems()) {
            subtotal = subtotal.add(item.getLineSubtotal());
        }
        return CurrencyUtil.scale(subtotal);
    }

    public BigDecimal calculateServiceCharge(Order order) {
        requireOrder(order);
        return CurrencyUtil.calculateAmount(calculateSubtotal(order), order.getServiceChargeRate());
    }

    public BigDecimal calculateTax(Order order) {
        requireOrder(order);
        BigDecimal itemTaxTotal = CurrencyUtil.zero();
        BigDecimal subtotal = CurrencyUtil.zero();
        for (OrderItem item : order.getActiveItems()) {
            subtotal = subtotal.add(item.getLineSubtotal());
            itemTaxTotal = itemTaxTotal.add(item.getTaxAmount());
        }

        BigDecimal taxTotal = itemTaxTotal;
        if (order.getTaxMode() == TaxMode.AFTER_SERVICE_CHARGE) {
            BigDecimal serviceChargeTax = calculateServiceChargeTax(order, subtotal, itemTaxTotal);
            taxTotal = taxTotal.add(serviceChargeTax);
        }
        return CurrencyUtil.scale(taxTotal);
    }

    public BigDecimal calculateGrandTotal(Order order) {
        requireOrder(order);
        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal serviceCharge = calculateServiceCharge(order);
        BigDecimal tax = calculateTax(order);
        return CurrencyUtil.scale(subtotal.add(serviceCharge).add(tax));
    }

    private BigDecimal calculateServiceChargeTax(Order order, BigDecimal subtotal, BigDecimal itemTaxTotal) {
        if (subtotal.compareTo(CurrencyUtil.zero()) == 0) {
            return CurrencyUtil.zero();
        }
        BigDecimal weightedTaxRate = itemTaxTotal.divide(subtotal, 8, java.math.RoundingMode.HALF_UP);
        return CurrencyUtil.scale(calculateServiceCharge(order).multiply(weightedTaxRate));
    }

    private void requireOrder(Order order) {
        if (order == null) {
            throw new BillingValidationException("Order cannot be null.");
        }
        order.validateState();
    }
}
